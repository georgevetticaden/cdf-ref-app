package cloudera.cdf.refapp.trucking.simulator.producer.smm;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Header;

import cloudera.cdf.csp.schema.refapp.trucking.schemaregistry.TruckSchemaConfig;
import cloudera.cdf.refapp.trucking.simulator.domain.SecurityType;
import cloudera.cdf.refapp.trucking.simulator.domain.transport.EventSourceType;
import cloudera.cdf.refapp.trucking.simulator.domain.transport.MobileEyeEvent;
import cloudera.cdf.refapp.trucking.simulator.producer.BaseTruckEventCollector;
import cloudera.cdf.refapp.trucking.simulator.producer.SchemaKafkaHeader;

public class SMMTruckEventJSONGenerator extends BaseTruckEventCollector {


	
	private static final String SCHEMA_KAFKA_HEADER_KEY = "schema.name";
	private KafkaProducer<String, String> kafkaProducer;
	private EventSourceType eventSourceType;
	private String topicName;

	public SMMTruckEventJSONGenerator(String kafkaBrokerList, String producerName, String topicName, EventSourceType eventSource, SecurityType securityType) {
		
		this.topicName = topicName;
		this.eventSourceType = eventSource;
		
        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaBrokerList);

        props.put("acks", "1");
        
        props.put("key.serializer", 
                "org.apache.kafka.common.serialization.StringSerializer");
                
        props.put("value.serializer", 
                "org.apache.kafka.common.serialization.StringSerializer");   
        
        props.put(CommonClientConfigs.CLIENT_ID_CONFIG, producerName);
        
        System.out.println("Producer Name is: " + producerName);
        System.out.println("Topic name is" + topicName);
             
		 
        /* If talking to secure Kafka cluster, set right security protocol */
		if(SecurityType.SECURE.equals(securityType)) {
			
			/* Get the security protocl being used */
			String securityProtocol = System.getProperty("security.protocol");
			if(StringUtils.isEmpty(securityProtocol)) {
				String errMsg = "security.protocol in JVM is required";
				logger.error(errMsg);
				throw new RuntimeException(errMsg);
			}
		 	props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);
		 	props.put("sasl.kerberos.service.name", "kafka");
		 	
		 	/* If SASL_SSL, keystore location is rquired */
		 	String trustStoreLocation=  "";
		 	String trustStorePassword = "";
		 	if("SASL_SSL".equals(securityProtocol)) {
		 		trustStoreLocation = System.getProperty("ssl.truststore.location");
				trustStorePassword = System.getProperty("ssl.truststore.password");

				/* If Protocol is SASL_SSL, truststore is required but not truststore password for clients unless client mutual auth is enabled */
				if(StringUtils.isNotEmpty(trustStoreLocation) ) {
					props.put("ssl.truststore.location", trustStoreLocation); 	
				} else {
					String errMsg = "ssl.truststore.location in JVM is required if using security protocol SASL_SSL";
					logger.error(errMsg);
					throw new RuntimeException(errMsg);
				}
				
				if(StringUtils.isNotEmpty(trustStorePassword)) {
				 	props.put("ssl.truststore.password", trustStorePassword); 
				} 				
			 	
		 	} 
		 	
		 	/* Get the SASL Mechanism 
		 	 * This was introduced with LDAP Based Auth
		 	 * IF PLAIN, then it means LDAP based auth and if GSSAPI, then it means kerorized based auth
		 	 * if not set, then it defaults to what the broker is configured to do which is Kerborized based auth
		 	 */
		 	String saslMechanism = System.getProperty("sasl.mechanism");
		 	if(StringUtils.isNotEmpty(saslMechanism)) {
		 		props.put("sasl.mechanism", saslMechanism);
		 	}
		 	
		 	String configMessage = "Security Setttings are: security.protocol["+ securityProtocol + "], ssl.truststore.location["+ trustStoreLocation +"], sasl.mechanism["+ saslMechanism +"]";
		 	System.out.println(configMessage);
		 	logger.info(configMessage);
		}
 
        try {		
            kafkaProducer = new KafkaProducer<String, String>(props);        	
        } catch (Exception e) {
        	logger.error("Error creating producer" , e);
        }
        
      
	}
	
	@Override
	public void onReceive(Object event) throws Exception {
		MobileEyeEvent mee = (MobileEyeEvent) event;
		
		if(eventSourceType == null || EventSourceType.ALL_STREAMS.equals(eventSourceType)) {
			sendTruckEventToKafka(mee);	
			sendTruckSpeedEventToKafka(mee);	
		} else if(EventSourceType.GEO_EVENT_STREAM.equals(eventSourceType)) {
			sendTruckEventToKafka(mee);	
		} else if (EventSourceType.SPEED_STREAM.equals(eventSourceType)) {	
			sendTruckSpeedEventToKafka(mee);
		}
	

	}

	
	private void sendTruckSpeedEventToKafka(MobileEyeEvent mee) {
		String eventToPass = createTruckSpeedEventJsonString(mee);
		String driverId = String.valueOf(mee.getTruck().getDriver().getDriverId());
		//logger.debug("Creating truck geo event["+eventToPass+"] for driver["+mee.getTruck().getDriver().getDriverId() + "] in truck [" + mee.getTruck() + "]");	
		
		
		try {
			final Callback callback = new MyProducerCallback();
			Iterable<Header> kafkaHeaders = createKafkaHeaderWithSchema(TruckSchemaConfig.KAFKA_RAW_TRUCK_SPEED_EVENT_SCHEMA_NAME);
			ProducerRecord<String, String> data = new ProducerRecord<String, String>(this.topicName, null, driverId, eventToPass, kafkaHeaders);
			logger.debug("Truck Speed Kafka Record with Header is : " + data);
			kafkaProducer.send(data, callback);			
		} catch (Exception e) {
			logger.error("Error sending json geo event[" + eventToPass + "] to  Kafka topic["+this.topicName+"]", e);
		}		
		

	}

	private void sendTruckEventToKafka(MobileEyeEvent mee) {
		String eventToPass = createTruckGeoEventJsonString(mee);
		String driverId = String.valueOf(mee.getTruck().getDriver().getDriverId());
		//logger.debug("Creating  truck speed event["+eventToPass+"] for driver["+mee.getTruck().getDriver().getDriverId() + "] in truck [" + mee.getTruck() + "]");			
				
		try {
			final Callback callback = new MyProducerCallback();
			Iterable<Header> kafkaHeaders = createKafkaHeaderWithSchema(TruckSchemaConfig.KAFKA_RAW_TRUCK_GEO_EVENT_SCHEMA_NAME);
			ProducerRecord<String, String> data = new ProducerRecord<String, String>(this.topicName, null, driverId, eventToPass, kafkaHeaders);
			logger.debug("Truck Geo Kafka Record with Header is: " + data);
			kafkaProducer.send(data, callback);			
		} catch (Exception e) {
			logger.error("Error sending json speed event[" + eventToPass + "] to  Kafka topic["+this.topicName +"]", e);
		}		
		
		
	}
	
	private Iterable<Header> createKafkaHeaderWithSchema(
			String schema) {
		List<Header> headers = new ArrayList<Header>();
		Header schemaHeader = new SchemaKafkaHeader(SCHEMA_KAFKA_HEADER_KEY, schema);
		headers.add(schemaHeader);
		return headers;
	}	

	 private  class MyProducerCallback implements Callback {
	        @Override
	        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
	        	if(e != null) {
	        		if(recordMetadata == null) {
	        			logger.info("Exception thrown when sending message: " + e);
	        		} else {
	        			logger.info("Exception thrown when sending message: " + recordMetadata.toString() , e);
	        		}
	        	}
	        }
	}	

		
}
