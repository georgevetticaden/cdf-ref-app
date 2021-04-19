package cloudera.cdf.cde.spark.refapp.trucking;

import static org.apache.spark.sql.functions.col;

import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.functions.*;
import org.apache.spark.sql.types.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TruckingTelemetrySparkETLS3toHive {
	
	private static final Logger LOG = LoggerFactory.getLogger(TruckingTelemetrySparkETLS3toHive.class);
	
	
	public static void main(String[] args) throws AnalysisException {
		// $example on:init_session$
		
		String s3SourceFolder = args[0];
		String s3DestinationFolder = args[1];
		
		String accessKey = args[2];
		String accessPassword = args[3];
		
		String writeFormat = args[4];
		
		SparkSession spark = SparkSession.builder()
				.appName("Java Spark SQL basic example")
				.config("spark.some.config.option", "some-value").getOrCreate();

		spark.sparkContext().setLogLevel("WARN");
		
		// Replace Key with your AWS account key (You can find this on IAM 
		spark.sparkContext().hadoopConfiguration().set("fs.s3a.access.key", accessKey);
		spark.sparkContext().hadoopConfiguration().set("fs.s3a.secret.key", accessPassword);
		spark.sparkContext().hadoopConfiguration().set("s.s3a.endpoint", "s3.amazonaws.com");
		

		truckingTelemetryETL(spark, s3SourceFolder, s3DestinationFolder, writeFormat);

		spark.stop();
	}

	private static void truckingTelemetryETL(SparkSession spark, String s3SourceFolder, String s3DestinationFolder, String writeFormat) {
		
		
		/* Removing Star as the adding the wildcard caused error when running in DataHub */
		//String s3FolderLocationUrl = "s3a://"+ s3SourceFolder + "/*";
		String s3FolderLocationUrl = "s3a://"+ s3SourceFolder ;

		
		LOG.warn("S3 Folder Url is: " + s3FolderLocationUrl);
		
		Dataset<Row> telemetryDataSet = spark.read().json(s3FolderLocationUrl);
		
		LOG.warn("Number of records is: " + telemetryDataSet.count());
		
		debug(telemetryDataSet, "Raw Data Set");
		
		/* Extract Time Components for partitions when writing to Impala */
		telemetryDataSet = extractTimeComponents(telemetryDataSet);
		debug(telemetryDataSet, "Raw Data Set With Time Components......change..");
		
		/* Filter by telemetry source */		
		Dataset<Row> truckGeoEvents =  
				telemetryDataSet.filter(col("eventSource").equalTo("truck_geo_event"))
								.drop("speed", "correlationId");
		debug(truckGeoEvents, "Truck Geo Event Telemetry");
	
		Dataset<Row> truckSpeedEvents =  
				telemetryDataSet.filter(col("eventSource").equalTo("truck_speed_event"))
								.drop("eventType", "longitude", "latitude", "correlationId");
		
		
		long speedEventsCount = truckSpeedEvents.count();
		
		/* filter for Speed Events greater than 60 */
		
		// Register DataFrame as as SQL temporary view
		truckSpeedEvents.createOrReplaceTempView("truck_speed_events");
		Dataset<Row> fileredTruckSpeedEvents =  spark.sql("select * from truck_speed_events where speed > 60");
		debug(fileredTruckSpeedEvents, "Truck Speed Event Telemetry Filtered");
		
		long speedEventsFilteredCount = fileredTruckSpeedEvents.count();
		LOG.warn("Before filter, speed events count is: " + speedEventsCount + ". After Filter, count is: " + speedEventsFilteredCount);
		
		String geoDestS3File = "s3a://" + s3DestinationFolder + "/truck_geo_events";
		String speedDestS3File = "s3a://" + s3DestinationFolder + "/truck_speed_events";
		
		/* Write geo and speed data to destination in based on configured format*/
		if(writeFormat.equals("parquet")) {
			truckGeoEvents.write().mode("append").partitionBy("truckId", "year", "month", "day", "hour").parquet(geoDestS3File);
			fileredTruckSpeedEvents.write().mode("append").partitionBy("truckId", "year", "month", "day", "hour").parquet(speedDestS3File);
		} else if (writeFormat.equals("orc")) {
			truckGeoEvents.write().mode("append").partitionBy("truckId", "year", "month", "day", "hour").orc(geoDestS3File);
			fileredTruckSpeedEvents.write().mode("append").partitionBy("truckId", "year", "month", "day", "hour").orc(speedDestS3File);
		} else {
			throw new RuntimeException("Unsupported format["+ writeFormat + "]");
		}

		
		
	}

	/**
	 * Convert timestampString from event to TimeStamp type to get time components using Spark functions
	 */
	private static Dataset<Row> extractTimeComponents(Dataset<Row> telemetryDataSet) {
		return telemetryDataSet.withColumn("eventTimeString", col("eventTime"))
						.withColumn("eventTime", functions.to_timestamp(col("eventTimeString")))
						.withColumn("year", functions.year(col("eventTime")))
						.withColumn("month", functions.month(col("eventTime")))
						.withColumn("day", functions.dayofmonth(col("eventTime")))
						.withColumn("hour", functions.hour(col("eventTime")));
	}

	private static void debug(Dataset<Row> dataSet, String message) {
		System.out.println("----------- "  + message + " ------------ ");
		dataSet.show();
		dataSet.printSchema();
	}	

}
