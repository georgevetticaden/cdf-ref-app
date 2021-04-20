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
import com.hortonworks.hwc.HiveWarehouseSession;
import com.hortonworks.hwc.HiveWarehouseSession.*;

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
		

		//HiveWarehouseSession hiveSession = HiveWarehouseSession.session(spark).build();
		

		String s3FolderLocationUrl = "s3a://"+ s3SourceFolder ;		
		
		Dataset<Row> telemetryDataSet = spark.read().json(s3FolderLocationUrl);
		telemetryDataSet = extractTimeComponents(telemetryDataSet);
		
		Dataset<Row> truckSpeedEvents =  
				telemetryDataSet.filter(col("eventSource").equalTo("truck_speed_event"))
								.drop("eventType", "longitude", "latitude", "correlationId", "eventTimeLong", "eventTimeString");

		long speedEventsCount = truckSpeedEvents.count();

		
		truckSpeedEvents.createOrReplaceTempView("truck_speed_events");
		Dataset<Row> fileredTruckSpeedEvents =  spark.sql("select * from truck_speed_events where speed > 60");		
		debug(fileredTruckSpeedEvents, "Truck Speed Event Telemetry Filtered");
		
		long speedEventsFilteredCount = fileredTruckSpeedEvents.count();
		LOG.warn("Before filter, speed events count is: " + speedEventsCount + ". After Filter, count is: " + speedEventsFilteredCount);
		
		fileredTruckSpeedEvents.write().format(HiveWarehouseSession.HIVE_WAREHOUSE_CONNECTOR).mode("append")
	    .option("database", "truck_fleet_db")
		.option("table", "hive_truck_speed_events")
	    .save();		
		
		
//		fileredTruckSpeedEvents.write().format(HiveWarehouseSession.HIVE_WAREHOUSE_CONNECTOR).mode("append")
//		    .option("database", "truck_fleet_db")
//			.option("table", "hive_truck_speed_events").option("partition", "c1='truckId', c2='year', c3='month', c4='day', c5='hour'")
//		   .save();

	}

	/**
	 * Convert timestampString from event to TimeStamp type to get time components using Spark functions
	 */
	private static Dataset<Row> extractTimeComponents(Dataset<Row> telemetryDataSet) {
		return telemetryDataSet.withColumn("eventTimeString", col("eventTime"))
						.withColumn("eventTime", functions.to_timestamp(col("eventTimeString")));
//						.withColumn("year", functions.year(col("eventTime")))
//						.withColumn("month", functions.month(col("eventTime")))
//						.withColumn("day", functions.dayofmonth(col("eventTime")))
//						.withColumn("hour", functions.hour(col("eventTime")));
	}

	private static void debug(Dataset<Row> dataSet, String message) {
		System.out.println("----------- "  + message + " ------------ ");
		dataSet.show();
		dataSet.printSchema();
	}	

}
