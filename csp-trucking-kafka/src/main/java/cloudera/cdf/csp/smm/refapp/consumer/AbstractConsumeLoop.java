package cloudera.cdf.csp.smm.refapp.consumer;

import static net.sourceforge.argparse4j.impl.Arguments.store;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hortonworks.registries.schemaregistry.client.SchemaRegistryClient;
import com.hortonworks.registries.schemaregistry.serdes.avro.kafka.KafkaAvroDeserializer;


public abstract class AbstractConsumeLoop<K extends Serializable, V> implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(AbstractConsumeLoop.class);

	protected  final KafkaConsumer<K, V> consumer;
	protected  final List<String> topics;
	protected final String clientId;
	

	private CountDownLatch shutdownLatch = new CountDownLatch(1);
	
	public AbstractConsumeLoop(Properties configs, List<String> topics) {
		this.clientId = configs.getProperty(ConsumerConfig.CLIENT_ID_CONFIG);
		this.topics = topics;
		this.consumer = new KafkaConsumer<>(configs);
	}
	
	public abstract void process(ConsumerRecord<K, V> record);	

	@Override
	public void run() {
		try {
			logger.info("Starting the Consumer : {}", clientId);

			ConsumerRebalanceListener listener = new ConsumerRebalanceListener() {
				@Override
				public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
					logger.info("C : {}, Revoked partitionns : {}", clientId, partitions);
				}
				
				@Override
				public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
					logger.info("C : {}, Assigned partitions : {}", clientId, partitions);
				}
			};
			consumer.subscribe(topics, listener);

			logger.info("C : {}, Started to process records", clientId);
			while(true) {
				ConsumerRecords<K, V> records = consumer.poll(Long.MAX_VALUE);
				
				if(records.isEmpty()) {
					logger.info("C : {}, Found no records", clientId);
					continue;
				}
			
				logger.info("C : {} Total No. of records received : {}", clientId, records.count());
				for (ConsumerRecord<K, V> record : records) {
					process(record);
				}
				
				
				// `enable.auto.commit` set to true by default which means consumers will commit based on configured interval so client doesn't ahve to commit
				// returned on the last poll(long) for all the subscribed list of topics and partitions
			}
		} catch (WakeupException e) {
			// Ignore, we're closing
		} finally {
			consumer.close();
			shutdownLatch.countDown();
			logger.info("C : {}, consumer exited", clientId);
		}
	}
	
	protected void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			logger.error("Error", e);
		}
	}
	
	public void close() {
		try {
			consumer.wakeup();
			shutdownLatch.await();
		} catch (InterruptedException e) {
			logger.error("Error", e);
		}
	}



	protected static Properties getConsumerConfigs(Namespace result) {
		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, result.getString("bootstrap.servers"));
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, result.getString("auto.offset.reset"));
		props.put(ConsumerConfig.GROUP_ID_CONFIG, result.getString("groupId"));
		props.put(ConsumerConfig.CLIENT_ID_CONFIG, result.getString("clientId"));
		props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, result.getString("max.partition.fetch.bytes"));

		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
		
		 // key deserializer
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());	
        //value deserializer
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        
        
        /* If talking to secure Kafka cluster, set security protocol as "SASL_PLAINTEXT */
        if("SASL_PLAINTEXT".equals(result.get("security.protocol"))) {
		 	props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");  
		 	props.put("sasl.kerberos.service.name", "kafka");        	
        } else if("SASL_SSL".equals(result.get("security.protocol"))) {
    		props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
    		props.put("sasl.kerberos.service.name", "kafka");       
    		props.put("ssl.truststore.location", result.get("ssl.truststore.location"));
    		props.put("ssl.truststore.password", result.get("ssl.truststore.password"));
        }        

		
		return props;
	}


	/**
	 * Get the command-line argument parser.
	 */
	protected static ArgumentParser argParser() {
		ArgumentParser parser = ArgumentParsers
				.newArgumentParser("basic-consumer-loop")
				.defaultHelp(true)
				.description("This example demonstrates kafka consumer auto subscription capabilities");

		parser.addArgument("--bootstrap.servers").action(store())
				.required(true)
				.type(String.class)
				.help("comma separated broker list");
		
		parser.addArgument("--schema.registry.url").action(store())
				.required(true)
				.type(String.class)
				.help("Schema Registry url...");		

		parser.addArgument("--topics").action(store())
				.required(true)
				.type(String.class)
				.help("consume messages from topics. Comma separated list e.g. t1,t2");

		parser.addArgument("--groupId").action(store())
				.required(true)
				.type(String.class)
				.help("Group Identifier");

		parser.addArgument("--clientId").action(store())
				.required(true)
				.type(String.class)
				.help("Client Identifier");

		parser.addArgument("--auto.offset.reset").action(store())
				.required(false)
				.setDefault("earliest")
				.type(String.class)
				.choices("earliest", "latest")
				.help("What to do when there is no initial offset in Kafka");

		parser.addArgument("--max.partition.fetch.bytes").action(store())
				.required(false)
				.setDefault("1024")
				.type(String.class)
				.help("The maximum amount of data per-partition the server will return");
		
		parser.addArgument("--security.protocol").action(store())
				.required(false)
				.setDefault("PLAINTEXT")
				.type(String.class)
				.help("Either PLAINTEXT or SASL_PLAINTEXT");
		
		parser.addArgument("--ssl.truststore.location").action(store())
		.required(false)
		.setDefault(" ")
		.type(String.class)
		.help("Location of Kafka Trust STore when SASL_SSL is enabled");		
		
		
		parser.addArgument("--ssl.truststore.password").action(store())
		.required(false)
		.setDefault(" ")
		.type(String.class)
		.help("TrustStore password");		
		
		parser.addArgument("--pause.period").action(store())
		.required(false)
		.setDefault(0)
		.type(Integer.class)
		.help("The pause period for demos in ms");			
		


		return parser;
	}
}
