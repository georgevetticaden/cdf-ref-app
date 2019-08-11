package cloudera.cdf.csp.smm.refapp.consumer.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cloudera.cdf.csp.smm.refapp.consumer.AbstractConsumeLoop;

import com.hortonworks.registries.schemaregistry.client.SchemaRegistryClient;
import com.hortonworks.registries.schemaregistry.serdes.avro.kafka.KafkaAvroDeserializer;

public class LoggerAvroEventConsumer extends AbstractConsumeLoop<String, GenericRecord> {
	
	
	private static final Logger logger = LoggerFactory.getLogger(LoggerAvroEventConsumer.class);	

	public LoggerAvroEventConsumer(Properties configs, List<String> topics) {
		super(configs, topics);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void process(ConsumerRecord<String, GenericRecord> record) {
		
		logger.info("C : {}, Record received partition : {}, key : {}, value : {}, offset : {}",
				clientId, record.partition(), record.key(), record.value(), record.offset());
		sleep(5000);
		
	}



	public static void main(String[] args) throws InterruptedException {

		ArgumentParser parser = argParser();

		try {
			Namespace result = parser.parseArgs(args);
			List<String> topics = Arrays.asList(result.getString("topics").split(","));
			Properties configs = getConsumerConfigs(result);
			configureSRDeserializers(configs, result);

			final LoggerAvroEventConsumer consumer = new LoggerAvroEventConsumer(configs, topics);
			consumer.run();
			
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				
				@Override
				public void run() {
					logger.info("Shutting down consumer..");
					consumer.close();
				}
			}));
			
		} catch (ArgumentParserException e) {
			if(args.length == 0)
				parser.printHelp();
			else 
				parser.handleError(e);
			System.exit(0);
		}
	}	
	
	
	private static void configureSRDeserializers(Properties props, Namespace result) {
        // key deserializer
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        // schema registry config
        props.putAll(Collections.singletonMap(SchemaRegistryClient.Configuration.SCHEMA_REGISTRY_URL.name(), result.getString("schema.registry.url")));

        // current props are passed to KafkaAvroDeserializer instance by invoking #configure(Map, boolean) method.
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
		
	}	
	

}
