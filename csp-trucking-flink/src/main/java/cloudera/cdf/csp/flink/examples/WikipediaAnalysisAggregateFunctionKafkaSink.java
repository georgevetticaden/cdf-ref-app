package cloudera.cdf.csp.flink.examples;

import java.util.Properties;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
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
	private static final String KAFKA_TOPIC_FLINK_SINK = "flink-test-sink";
	private static final String KAFKA_BROKER_LIST = "ec2-54-213-148-193.us-west-2.compute.amazonaws.com:6667,ec2-18-236-246-247.us-west-2.compute.amazonaws.com:6667,ec2-52-43-50-103.us-west-2.compute.amazonaws.com:6667";
	private static final String KAFKA_PRODUCER_NAME = "flink-app-wikipedia-analysis";
	
    public static void main(String[] args) throws Exception {

    	LOG.info("starting wiki job..");
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
        .addSink(new FlinkKafkaProducer(KAFKA_TOPIC_FLINK_SINK,  new SimpleStringSchema(), createKafkaProducerConfig()));
    	
    	
    	result.print();

    	see.execute();    	
    	LOG.info("finished executing..");

    }

	private static Properties createKafkaProducerConfig() {
        Properties props = new Properties();
        props.put("bootstrap.servers", KAFKA_BROKER_LIST);

        props.put("acks", "1");
        
//        props.put("key.serializer", 
//                "org.apache.kafka.common.serialization.StringSerializer");
//                
//        props.put("value.serializer", 
//                "org.apache.kafka.common.serialization.StringSerializer");   
        
        props.put(CommonClientConfigs.CLIENT_ID_CONFIG, KAFKA_PRODUCER_NAME);
        return props;
	}
    
    
    
}
