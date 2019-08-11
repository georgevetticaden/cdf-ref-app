package cloudera.cdf.csp.smm.refapp.consumer.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cloudera.cdf.csp.smm.refapp.consumer.AbstractConsumeLoop;

public class LoggerStringEventConsumer extends AbstractConsumeLoop<String, GenericRecord> {
	
	
	private static final Logger logger = LoggerFactory.getLogger(LoggerStringEventConsumer.class);	

	public LoggerStringEventConsumer(Properties configs, List<String> topics) {
		super(configs, topics);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void process(ConsumerRecord<String, GenericRecord> record) {
		
		logger.info("C : {}, Record received partition : {}, key : {}, value : {}, offset : {}",
				clientId, record.partition(), record.key(), record.value(), record.offset());
		//sleep(5000);
		
	}



	public static void main(String[] args) throws InterruptedException {

		ArgumentParser parser = argParser();

		try {
			Namespace result = parser.parseArgs(args);
			List<String> topics = Arrays.asList(result.getString("topics").split(","));
			Properties configs = getConsumerConfigs(result);

			final LoggerStringEventConsumer consumer = new LoggerStringEventConsumer(configs, topics);
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
	
	
	

}
