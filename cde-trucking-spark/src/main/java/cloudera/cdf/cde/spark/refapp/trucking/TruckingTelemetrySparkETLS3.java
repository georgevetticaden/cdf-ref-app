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

public class TruckingTelemetrySparkETLS3 {
	
	
	public static void main(String[] args) throws AnalysisException {
		// $example on:init_session$
		
		String sourceFile = args[0];
		String destination = args[1];
		
		SparkSession spark = SparkSession.builder()
				.appName("Java Spark SQL basic example")
				.config("spark.some.config.option", "some-value").getOrCreate();

		spark.sparkContext().setLogLevel("WARN");
		
		truckingTelemetryETL(spark, sourceFile, destination);

		spark.stop();
	}

	private static void truckingTelemetryETL(SparkSession spark, String sourceFile, String destinationFile) {
		Dataset<Row> telemetryDataSet = spark.read().json(sourceFile);
		debug(telemetryDataSet, "Raw Data Set");
		
		/* Extract Time Components for partitions when writing to Impala */
		telemetryDataSet = extractTimeComponents(telemetryDataSet);
		debug(telemetryDataSet, "Raw Data Set With Time Components");
		
		/* Filter by telemetry source */		
		Dataset<Row> truckGeoEvents =  
				telemetryDataSet.filter(col("eventSource").equalTo("truck_geo_event"))
								.drop("speed", "correlationId");
		debug(truckGeoEvents, "Truck Geo Event Telemetry");
	
		Dataset<Row> truckSpeedEvents =  
				telemetryDataSet.filter(col("eventSource").equalTo("truck_speed_event"))
								.drop("eventType", "longitude", "latitude", "correlationId");
		debug(truckSpeedEvents, "Truck Speed Event Telemetry");
		
		/* Write telemetry data to destination */
		truckGeoEvents.write().mode("append").partitionBy("truckId", "year", "month", "day", "hour").parquet(destinationFile);
		
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
