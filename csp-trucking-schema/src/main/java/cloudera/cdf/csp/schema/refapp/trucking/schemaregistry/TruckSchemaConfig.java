package cloudera.cdf.csp.schema.refapp.trucking.schemaregistry;

public  final class TruckSchemaConfig {
	
	/** ----------Common Schema Registry Meta Info ------------------- **/
	
	/* Serializer Names */
	//public static final String AVRO_SERDES_JAR_NAME = "/schema/schema-registry-serdes-0.1.0-SNAPSHOT.jar";
	public static final String AVRO_SERIALIZER_NAME = "avro-serializer";
	public static final String AVRO_DESERIALIZER_NAME = "avro-deserializer";	
	
	
	
	/** ----------- The following are schema meta info for the schema for the truck event log data -------------*/

	
	/* Schema Group Name */
	public static final String LOG_SCHEMA_GROUP_NAME = "truck-sensors-log";
	
	/* Schema names for the two streams of data */
	public static final String LOG_TRUCK_SPEED_EVENT_SCHEMA_NAME = "truck_speed_events_log";
	public static final String LOG_TRUCK_GEO_EVENT_SCHEMA_NAME = "truck_events_log";
	
	
	/* Versions for each of the schemas */
	public static final int LOG_TRUCK_GEO_EVENT_SCHEMA_VERSION = 1;
	public static final int LOG_TRUCK_SPEED_EVENT_SCHEMA_VERSION = 1;		
	
	
	/** ------------ The following are schema meta info for the schema for the truck event kafka topics ----------------*/
	
	
	/* Schema Group Name */
	public static final String KAFKA_SCHEMA_GROUP_NAME = "truck-sensors-kafka";
	
	/* Schema names for the two streams of data */
	public static final String KAFKA_TRUCK_SPEED_EVENT_SCHEMA_NAME = "truck_speed_events_avro";
	public static final String KAFKA_TRUCK_GEO_EVENT_SCHEMA_NAME = "truck_events_avro";
	
	/* Schema names for the two streams of data for SMM Demo */
	public static final String KAFKA_TRUCK_SPEED_EVENT_FOR_SMM_SCHEMA_NAME = "syndicate-speed-event-avro";
	public static final String KAFKA_TRUCK_GEO_EVENT_FOR_SMM_SCHEMA_NAME = "syndicate-geo-event-avro";	
	
	/* Kafka Topics for raw event.  Events are published to these topics if truck generator serializes truck events into kafka */
	public static final String KAFKA_RAW_TRUCK_SPEED_EVENT_TOPIC_NAME = "raw-truck_speed_events_avro" ;
	public static final String KAFKA_RAW_TRUCK_GEO_EVENT_TOPIC_NAME = "raw-truck_events_avro";
	// This topic is a single kafka topic for both speed and geo events
	public static final String KAFKA_RAW_TRUCK_EVENT_TOPIC_NAME = "raw-all_truck_events_avro";
	// This topic is a single kafka for csv eventsfor both speed and geo events that has schema header using Kafka 1. info
	public static final String KAFKA_RAW_TRUCK_CSV_EVENT_TOPIC_NAME = "raw-all_truck_events_csv";
		

	public static final String KAFKA_TRUCK_SPEED_EVENT_TOPIC_NAME = "truck_speed_events_avro";
	public static final String KAFKA_TRUCK_GEO_EVENT_TOPIC_NAME = "truck_events_avro";
	
	public static final String KAFKA_RAW_TRUCK_SPEED_EVENT_SCHEMA_NAME = KAFKA_RAW_TRUCK_SPEED_EVENT_TOPIC_NAME ;
	public static final String KAFKA_RAW_TRUCK_GEO_EVENT_SCHEMA_NAME = KAFKA_RAW_TRUCK_GEO_EVENT_TOPIC_NAME ;
	
	/* Versions for each of the schemas */
	public static final int KAFKA_TRUCK_GEO_EVENT_SCHEMA_VERSION = 1;
	public static final int KAFKA_TRUCK_SPEED_EVENT_SCHEMA_VERSION = 1;	
	public static final int KAFKA_RAW_TRUCK_GEO_EVENT_SCHEMA_VERSION = 1;
	public static final int KAFKA_RAW_TRUCK_SPEED_EVENT_SCHEMA_VERSION = 1;	
	
	


}
