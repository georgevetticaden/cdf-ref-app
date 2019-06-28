package cloudera.cdf.csp.schema.refapp.trucking.schemaregistry;


import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hortonworks.registries.schemaregistry.SchemaBranch;
import com.hortonworks.registries.schemaregistry.SchemaCompatibility;
import com.hortonworks.registries.schemaregistry.SchemaIdVersion;
import com.hortonworks.registries.schemaregistry.SchemaMetadata;
import com.hortonworks.registries.schemaregistry.SchemaMetadataInfo;
import com.hortonworks.registries.schemaregistry.SchemaVersion;
import com.hortonworks.registries.schemaregistry.SchemaVersionInfo;
import com.hortonworks.registries.schemaregistry.avro.AvroSchemaProvider;
import com.hortonworks.registries.schemaregistry.client.SchemaRegistryClient;
       

public class TruckSchemaRegistryLoader {
	

	
	private static final Logger LOG = LoggerFactory.getLogger(TruckSchemaRegistryLoader.class);
	private String schemaRegistryUrl;
	SchemaRegistryClient schemaRegistryClient;
	
	protected ObjectMapper mapper;
	public RestTemplate restTemplate;	
	
	
	public TruckSchemaRegistryLoader(String schmemaRegistryUrl) {
		this.schemaRegistryUrl = schmemaRegistryUrl;
		this.schemaRegistryClient = new SchemaRegistryClient(createConfig(schmemaRegistryUrl));
		this.restTemplate = new RestTemplate();		
		
	}

	public static void main(String args[]) {
		
		String schmemaRegistryUrl = args[0];
		if(StringUtils.isEmpty(schmemaRegistryUrl)) 
			throw new RuntimeException("Schema Registry REST URL must be provided");
		
		TruckSchemaRegistryLoader registryLoader = new TruckSchemaRegistryLoader(schmemaRegistryUrl);
		registryLoader.loadSchemaRegistry();
		
	}
	
	/**
	 * Loads the schema registry with all data required for the CDF Trucking Reference Application
	 */
	public void loadSchemaRegistry() {
		try {

			/* Populate the 2 schemas for the raw kafka topics*/
			populateSchemaRegistryForRawTruckGeoEventInKafka();
			populateSchemaRegistryForRawTruckSpeedEventInKafka();			
			
			/* Populate the 2 schemas for the kafka topics that will represent the enriched data from kafka */
			populateSchemaRegistryForTruckGeoEventInKafka();
			populateSchemaRegistryForTruckSpeedEventInKafka();
			
			/* Populate the 2 schemas for the SMM Demo */
			populateSchemaRegistryForTruckGeoEventInKafkaForSMM();
			populateSchemaRegistryForTruckSpeedEventInKafkaForSMM();
			
			
			
			
		} catch (Exception e) {
			String errorMsg = "Error loading data into Schema Registry for truck events";
			LOG.error(errorMsg, e);
			throw new RuntimeException(e);
		}		
	}

	public void cleanupSchemaRegistry() throws Exception {
		
		deleteSchema(TruckSchemaConfig.KAFKA_RAW_TRUCK_GEO_EVENT_SCHEMA_NAME);
	
		
	}		

	
	
	private void deleteSchema(String schemaName) throws Exception {
		
		SchemaBranch masterBranch = getMasterBranch(schemaName);
		
		SchemaVersionInfo schemaVersionInfo = schemaRegistryClient.getLatestSchemaVersionInfo(schemaName);
		
		
		
		Map<String, String> mapParams = new HashMap<String, String>();
		mapParams.put("id", schemaVersionInfo.getVersion().toString());
		mapParams.put("schemaName", schemaName);
		String url = constructRESTUrl("/schemaregistry/schemas/{schemaName}/versions/{id}");
		
		restTemplate.exchange(url, HttpMethod.DELETE, null, Map.class, mapParams);

		
		
		
		
	}

	


	private void populateSchemaRegistryForRawTruckGeoEventInKafka() throws Exception {
		
		/* If schema exists, don't add */
		SchemaMetadataInfo metaInfo= schemaRegistryClient.getSchemaMetadataInfo(TruckSchemaConfig.KAFKA_RAW_TRUCK_GEO_EVENT_SCHEMA_NAME);
		if(metaInfo != null) {
			LOG.warn("Schema["+ TruckSchemaConfig.KAFKA_RAW_TRUCK_GEO_EVENT_SCHEMA_NAME + "] already exists. Not creating another one");
			return;
		}
		
		String schemaGroup = TruckSchemaConfig.KAFKA_SCHEMA_GROUP_NAME;
		String schemaName = TruckSchemaConfig.KAFKA_RAW_TRUCK_GEO_EVENT_SCHEMA_NAME;
		String schemaType = AvroSchemaProvider.TYPE;
		String description = "Raw Geo events from trucks in Kafka Topic";
		SchemaCompatibility compatiblity = SchemaCompatibility.BACKWARD;
		String schemaContentFileName = "/schema/truck-geo-event-log.avsc";
		
		registerSchemaMetaData(schemaGroup, schemaName, schemaType, description, compatiblity);
		addSchemaVersion(schemaName, schemaContentFileName, description);
		//mapSeDeserializers(schemaName);
	}		
	
	private void populateSchemaRegistryForRawTruckSpeedEventInKafka() throws Exception {
		
		/* If schema exists, don't add */
		SchemaMetadataInfo metaInfo= schemaRegistryClient.getSchemaMetadataInfo(TruckSchemaConfig.KAFKA_RAW_TRUCK_SPEED_EVENT_SCHEMA_NAME);
		if(metaInfo != null) {
			LOG.warn("Schema["+ TruckSchemaConfig.KAFKA_RAW_TRUCK_SPEED_EVENT_SCHEMA_NAME + "] already exists. Not creating another one");
			return;
		}
		
		String schemaGroup = TruckSchemaConfig.KAFKA_SCHEMA_GROUP_NAME;
		String schemaName = TruckSchemaConfig.KAFKA_RAW_TRUCK_SPEED_EVENT_SCHEMA_NAME;
		String schemaType = AvroSchemaProvider.TYPE;
		String description = "Raw Speed Events from trucks in Kafka Topic";
		SchemaCompatibility compatiblity = SchemaCompatibility.BACKWARD;
		String schemaContentFileName = "/schema/truck-speed-event-log.avsc";
		
		registerSchemaMetaData(schemaGroup, schemaName, schemaType, description, compatiblity);
		addSchemaVersion(schemaName, schemaContentFileName, description);
		//mapSeDeserializers(schemaName);
	}		
	
	private void populateSchemaRegistryForTruckGeoEventInKafka() throws Exception {
		/* If schema exists, don't add */
		SchemaMetadataInfo metaInfo= schemaRegistryClient.getSchemaMetadataInfo(TruckSchemaConfig.KAFKA_TRUCK_GEO_EVENT_SCHEMA_NAME);
		if(metaInfo != null) {
			LOG.warn("Schema["+ TruckSchemaConfig.KAFKA_TRUCK_GEO_EVENT_SCHEMA_NAME + "] already exists. Not creating another one");
			return;
		}
		
		String schemaGroup = TruckSchemaConfig.KAFKA_SCHEMA_GROUP_NAME;
		String schemaName = TruckSchemaConfig.KAFKA_TRUCK_GEO_EVENT_SCHEMA_NAME;
		String schemaType = AvroSchemaProvider.TYPE;
		String description = "Enriched Geo events from trucks in Kafka Topic";
		SchemaCompatibility compatiblity = SchemaCompatibility.BACKWARD;
		String schemaContentFileName = "/schema/truck-geo-event-kafka.avsc";
		
		registerSchemaMetaData(schemaGroup, schemaName, schemaType, description, compatiblity);
		addSchemaVersion(schemaName, schemaContentFileName, description);
		//mapSeDeserializers(schemaName);
	}	
	
	private void populateSchemaRegistryForTruckGeoEventInKafkaForSMM() throws Exception {
		/* If schema exists, don't add */
		SchemaMetadataInfo metaInfo= schemaRegistryClient.getSchemaMetadataInfo(TruckSchemaConfig.KAFKA_TRUCK_GEO_EVENT_FOR_SMM_SCHEMA_NAME);
		if(metaInfo != null) {
			LOG.warn("Schema["+ TruckSchemaConfig.KAFKA_TRUCK_GEO_EVENT_FOR_SMM_SCHEMA_NAME + "] already exists. Not creating another one");
			return;
		}
		
		String schemaGroup = TruckSchemaConfig.KAFKA_SCHEMA_GROUP_NAME;
		String schemaName = TruckSchemaConfig.KAFKA_TRUCK_GEO_EVENT_FOR_SMM_SCHEMA_NAME;
		String schemaType = AvroSchemaProvider.TYPE;
		String description = "Enriched Geo events from trucks in Kafka Topic";
		SchemaCompatibility compatiblity = SchemaCompatibility.BACKWARD;
		String schemaContentFileName = "/schema/truck-geo-event-kafka.avsc";
		
		registerSchemaMetaData(schemaGroup, schemaName, schemaType, description, compatiblity);
		addSchemaVersion(schemaName, schemaContentFileName, description);
		//mapSeDeserializers(schemaName);
	}		
	
	
	
	private void populateSchemaRegistryForTruckSpeedEventInKafka() throws Exception {

		/* If schema exists, don't add */
		SchemaMetadataInfo metaInfo= schemaRegistryClient.getSchemaMetadataInfo(TruckSchemaConfig.KAFKA_TRUCK_SPEED_EVENT_SCHEMA_NAME);
		if(metaInfo != null) {
			LOG.warn("Schema["+ TruckSchemaConfig.KAFKA_TRUCK_SPEED_EVENT_SCHEMA_NAME + "] already exists. Not creating another one");
			return;
		}
		
		String schemaGroup = TruckSchemaConfig.KAFKA_SCHEMA_GROUP_NAME;
		String schemaName = TruckSchemaConfig.KAFKA_TRUCK_SPEED_EVENT_SCHEMA_NAME;
		String schemaType = AvroSchemaProvider.TYPE;
		String description = "Enriched Speed Events from trucks in Kafka Topic";
		SchemaCompatibility compatiblity = SchemaCompatibility.BACKWARD;
		String schemaContentFileName = "/schema/truck-speed-event-kafka.avsc";
		
		registerSchemaMetaData(schemaGroup, schemaName, schemaType, description, compatiblity);
		addSchemaVersion(schemaName, schemaContentFileName, description);
		//mapSeDeserializers(schemaName);
	}	
	
	private void populateSchemaRegistryForTruckSpeedEventInKafkaForSMM() throws Exception {

		/* If schema exists, don't add */
		SchemaMetadataInfo metaInfo= schemaRegistryClient.getSchemaMetadataInfo(TruckSchemaConfig.KAFKA_TRUCK_SPEED_EVENT_FOR_SMM_SCHEMA_NAME);
		if(metaInfo != null) {
			LOG.warn("Schema["+ TruckSchemaConfig.KAFKA_TRUCK_SPEED_EVENT_FOR_SMM_SCHEMA_NAME + "] already exists. Not creating another one");
			return;
		}
		
		String schemaGroup = TruckSchemaConfig.KAFKA_SCHEMA_GROUP_NAME;
		String schemaName = TruckSchemaConfig.KAFKA_TRUCK_SPEED_EVENT_FOR_SMM_SCHEMA_NAME;
		String schemaType = AvroSchemaProvider.TYPE;
		String description = "Enriched Speed Events from trucks in Kafka Topic";
		SchemaCompatibility compatiblity = SchemaCompatibility.BACKWARD;
		String schemaContentFileName = "/schema/truck-speed-event-kafka.avsc";
		
		registerSchemaMetaData(schemaGroup, schemaName, schemaType, description, compatiblity);
		addSchemaVersion(schemaName, schemaContentFileName, description);
		//mapSeDeserializers(schemaName);
	}	
	
	
	/**
	 * Upload the serdes jar that has the serializer and deserailizer classes and then registers teh serailizer and deserializer classes
	 * with Schema Registry
	 * @param avroserdesJarName
	 * @throws Exception
	 */
//	public void uploadAndRegisterSeDeserializers(String avroserdesJarName) throws Exception {
//		 
//	    /* Uplaod the serializer jar to the schema registry */
//		InputStream serdesJarInputStream = TruckSchemaRegistryLoader.class.getResourceAsStream(avroserdesJarName);
//		String fileId = schemaRegistryClient.uploadFile(serdesJarInputStream);
//		 
//		 /* Register the Serializer and Deserializer classes in the uploaded jar with schema registry */
//		 this.serializerId = registerAvroSerializer(fileId);
//		 this.deserializerId =  registerAvroDeserializer(fileId);
//	}
	
	/**
	 * Maps the registered serailizer and deserializer with the passed in schema
	 * @param schemaName
	 * @throws Exception
	 */
//	public void mapSeDeserializers(String schemaName) throws Exception {
//
//		 //map schema
//	     schemaRegistryClient.mapSchemaWithSerDes(schemaName, serializerId);
//	     schemaRegistryClient.mapSchemaWithSerDes(schemaName, deserializerId);			     
//		 
//	}	
	
	/**
	 * Adds a new schema version to a Schema
	 * @param schemaName
	 * @param schemaContentFileName
	 * @throws Exception
	 */
	public void addSchemaVersion(String schemaName, String schemaContentFileName, String description) throws Exception {
		  
		String schema = getSchema(schemaContentFileName);
		LOG.info("Truck Geo Event Schema is: " + schema);
		
		SchemaVersion schemaVersion = new SchemaVersion(schema, description);
		SchemaIdVersion version = schemaRegistryClient.addSchemaVersion(schemaName, schemaVersion);
		LOG.info("Version Id of new schema is: " + version);
		
		
	}	

	
	
	/**
	 * Creates a New Schema Group and Schema Meta data construct in the Registry
	 * @param schemaGroup
	 * @param schemaName
	 * @param schemaType
	 * @param description
	 * @param compatiblity
	 * @throws Exception
	 */
	private void registerSchemaMetaData(String schemaGroup, String schemaName, String schemaType, 
		    String description, SchemaCompatibility compatiblity) throws Exception {
		  
		SchemaMetadata schemaMetadata = new SchemaMetadata.Builder(schemaName)
														  .type(schemaType)
														  .schemaGroup(schemaGroup)
														  .description(description)
														  .compatibility(compatiblity)
														  .build();
		// register the schemaGroup
	    long status = schemaRegistryClient.registerSchemaMetadata(schemaMetadata);
	    LOG.info("Status of registering schema is: " + status);
	}
	
		
	
	
	
//    private Long registerAvroSerializer(String fileId) {
//        String avroSerializerClassName = "org.apache.registries.schemaregistry.serdes.avro.AvroSnapshotSerializer";
//        SerDesInfo serializerInfo = new SerDesInfo.Builder()
//                .name(TruckSchemaConfig.AVRO_SERIALIZER_NAME)
//                .description("The Default Avro Serializer")
//                .fileId(fileId)
//                .className(avroSerializerClassName)
//                .buildSerializerInfo();
//        return schemaRegistryClient.addSerializer(serializerInfo);
//    }
//
//    private Long registerAvroDeserializer(String fileId) {
//        String avroDeserializerClassName = "org.apache.registries.schemaregistry.serdes.avro.AvroSnapshotDeserializer";
//        SerDesInfo deserializerInfo = new SerDesInfo.Builder()
//                .name("avro-deserializer")
//                .description("The Default Avro Deserializer")
//                .fileId(fileId)
//                .className(avroDeserializerClassName)
//                .buildDeserializerInfo();
//        return schemaRegistryClient.addDeserializer(deserializerInfo);
//    }      
    
    String getSchema(String schemaFileName) throws IOException {
        InputStream schemaResourceStream = TruckSchemaRegistryLoader.class.getResourceAsStream(schemaFileName);
        if (schemaResourceStream == null) {
            throw new IllegalArgumentException("Given schema file [" + schemaFileName + "] does not exist");
        }

        return IOUtils.toString(schemaResourceStream, "UTF-8");
    }      
	
    static Map<String, Object> createConfig(String schemaRegistryUrl) {
        Map<String, Object> config = new HashMap<>();
        config.put(SchemaRegistryClient.Configuration.SCHEMA_REGISTRY_URL.name(), schemaRegistryUrl);
        
        return config;
    }

	
    private void populateSchemaRegistryForTruckGeoEventInLog() throws Exception {
		String schemaGroup = TruckSchemaConfig.LOG_SCHEMA_GROUP_NAME;
		String schemaName = TruckSchemaConfig.LOG_TRUCK_GEO_EVENT_SCHEMA_NAME;
		String schemaType = AvroSchemaProvider.TYPE;
		String description = "Raw Geo events from trucks in Log file";
		SchemaCompatibility compatiblity = SchemaCompatibility.BACKWARD;
		String schemaContentFileName = "/schema/truck-geo-event-log.avsc";
		
		registerSchemaMetaData(schemaGroup, schemaName, schemaType, description, compatiblity);
		addSchemaVersion(schemaName, schemaContentFileName, description);
		//mapSeDeserializers(schemaName);
	}
	
	private void populateSchemaRegistryForTruckSpeedEventInLog() throws Exception {
		String schemaGroup = TruckSchemaConfig.LOG_SCHEMA_GROUP_NAME;
		String schemaName = TruckSchemaConfig.LOG_TRUCK_SPEED_EVENT_SCHEMA_NAME;
		String schemaType = AvroSchemaProvider.TYPE;
		String description = "Raw Speed Events from trucks in Log File";
		SchemaCompatibility compatiblity = SchemaCompatibility.BACKWARD;
		String schemaContentFileName = "/schema/truck-speed-event-log.avsc";
		
		registerSchemaMetaData(schemaGroup, schemaName, schemaType, description, compatiblity);
		addSchemaVersion(schemaName, schemaContentFileName, description);
		//mapSeDeserializers(schemaName);
	}	    

	private SchemaBranch getMasterBranch(String schemaName) throws Exception{
		SchemaBranch masterBranch = null;
		Collection<SchemaBranch> branches = schemaRegistryClient.getSchemaBranches(schemaName);
		if(branches == null || branches.isEmpty()) 
			return null;
		for(SchemaBranch branch: branches) {
			if(branch.getName().equals("MASTER")) {
				masterBranch = branch;
				break;
			}
		}
		return masterBranch;
	}	
    
	protected String constructRESTUrl(String actionUrl) {
		return schemaRegistryUrl + actionUrl;
	}	    

}
