package cloudera.cdf.csp.flink.examples;

import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.wikiedits.WikipediaEditEvent;
import org.apache.flink.streaming.connectors.wikiedits.WikipediaEditsSource;
import org.apache.kafka.clients.CommonClientConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WikipediaAnalysisAggregateFunctionKafkaSink {

	private static final Logger LOG = LoggerFactory.getLogger(WikipediaAnalysisAggregateFunctionKafkaSink.class);

    public static void main(String[] args) throws Exception {

    	LOG.info("starting wiki job..");
    	

        final ParameterTool params = ParameterTool.fromArgs(args);
        
        final String kafkaTopicName = params.get("kafka-topic-name");
        final String kafkaBootstrapUrl = params.get("kafka-bootstrap-url");
        final String kafkaProducerName = params.get("kafka-producer-name");
            
        if(StringUtils.isEmpty(kafkaTopicName) || StringUtils.isEmpty(kafkaBootstrapUrl) || StringUtils.isEmpty(kafkaProducerName)) {
            System.err.println("No kafkaTopicName or kafkaBootstrapUrl or kafkaProducerName specified. Please run 'WikipediaAnalysisAggregateFunctionKafkaSink --kafka-topic-name <kafkaTopicName --kafka-bootstrap-url <kafkaBootstrapUrl> --kafka-producer-name <kafkaProducerName> '");
            return;
        }
        
    	StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();
    	
    	DataStream<WikipediaEditEvent> edits = see.addSource(new WikipediaEditsSource());

    	KeyedStream<WikipediaEditEvent, String> keyedEdits = edits
    		    .keyBy(new KeySelector<WikipediaEditEvent, String>() {
    		        @Override
    		        public String getKey(WikipediaEditEvent event) {
    		            return event.getUser();
    		        }
    		    });    	

    	DataStream<Tuple2<String, Long>> result = keyedEdits
    		    .timeWindow(Time.seconds(5))
    		    .aggregate(new SumAggregrateFunction());      
    	

		result.map(new MapFunction<Tuple2<String,Long>, String>() {
            @Override
            public String map(Tuple2<String, Long> tuple) {
                return tuple.toString();
            }
        })
        .addSink(new FlinkKafkaProducer(kafkaTopicName, new SimpleStringSchema(), createKafkaProducerConfig(kafkaBootstrapUrl, kafkaProducerName)));
    	
    	
    	result.print();

    	see.execute();    	
    	LOG.info("finished executing..");

    }

	private static Properties createKafkaProducerConfig(String brokerUrl, String producerName) {
        Properties props = new Properties();
        props.put("bootstrap.servers", brokerUrl);

        props.put("acks", "1");
        
//        props.put("key.serializer", 
//                "org.apache.kafka.common.serialization.StringSerializer");
//                
//        props.put("value.serializer", 
//                "org.apache.kafka.common.serialization.StringSerializer");   
        
        props.put(CommonClientConfigs.CLIENT_ID_CONFIG, producerName);
        return props;
	}
    
    
    
}
