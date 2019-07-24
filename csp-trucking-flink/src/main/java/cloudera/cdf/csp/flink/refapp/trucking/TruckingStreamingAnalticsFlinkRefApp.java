package cloudera.cdf.csp.flink.refapp.trucking;

import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.ProcessJoinFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.util.serialization.JSONKeyValueDeserializationSchema;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TruckingStreamingAnalticsFlinkRefApp {

	private static final Logger LOG = LoggerFactory.getLogger(TruckingStreamingAnalticsFlinkRefApp.class);
	private static final String SOURCE_GEO_STREAM_TOPIC = "syndicate-geo-event-json";	
	private static final String SOURCE_SPEED_STREAM_TOPIC = "syndicate-speed-event-json";	
	
	public static void main(String[] args) throws Exception {
        final ParameterTool params = ParameterTool.fromArgs(args);
        
  
        if(StringUtils.isEmpty(params.get("bootstrap.servers")) ) {
            LOG.error("No  kafkaBootstrapUrl  specified. Please run 'TruckingStreamingAnalticsRefApp  --bootstrap.servers <kafkaBootstrapUrl>  '");
            return;
        }        

    	StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();
    	see.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
    	
        //create Geo Stream Source
    	FlinkKafkaConsumer<ObjectNode> geoStreamSource = constructGeoEventSource(params.getProperties());
    	geoStreamSource.setStartFromLatest();
    	DataStream<ObjectNode> geoStream = see.addSource(geoStreamSource, "TruckGeoStream");
    	
    	/* create the Speed Stream Source */
    	FlinkKafkaConsumer<ObjectNode> speedStreamSource = constructSpeedEventSource(params.getProperties());
    	speedStreamSource.setStartFromLatest();
    	DataStream<ObjectNode> speedStream = see.addSource(speedStreamSource, "SpeedGeoStream");
    	
    	//print the geoStream and speedStream
    	//geoStream.print();
    	//speedStream.print();
    	
		
		/* join the streams */
    	KeySelector<ObjectNode, Integer> keySelector = createKeySelector();
    	ProcessJoinFunction<ObjectNode, ObjectNode, ObjectNode > processJoinFunction = createProcessJoinFunction();
		DataStream<ObjectNode> geoSpeedJoinedStream = geoStream.keyBy(keySelector)
    			 .intervalJoin(speedStream.keyBy(keySelector))
    			 .between(Time.milliseconds(0), Time.milliseconds(1500))
    			 .process(processJoinFunction);
		
		geoSpeedJoinedStream.print();
    	
    	see.execute();
    	
        
	}



	private static ProcessJoinFunction<ObjectNode, ObjectNode, ObjectNode> createProcessJoinFunction() {
		return new ProcessJoinFunction<ObjectNode, ObjectNode, ObjectNode>() {

			private static final long serialVersionUID = 2797987756677620500L;

			@Override
			public void processElement(
					ObjectNode geoStream,
					ObjectNode speedStream,
					ProcessJoinFunction<ObjectNode, ObjectNode, ObjectNode>.Context context,
					Collector<ObjectNode> collector) throws Exception {
				//LOG.info("In Process element, geoStream is: " + geoStream + " and speedStream is: " + speedStream);
				geoStream.put("speed", speedStream.get("value").get("speed").asInt());
				collector.collect(geoStream);
				
			}
		};
	}



	private static KeySelector<ObjectNode, Integer> createKeySelector() {
		return new KeySelector<ObjectNode, Integer>() {

			private static final long serialVersionUID = 1344275833268319633L;

			@Override
			public Integer getKey(ObjectNode value) throws Exception {
				return value.get("key").asInt();
			}
		};
	}



	private static FlinkKafkaConsumer<ObjectNode> constructSpeedEventSource(
			Properties props) {
		//add additional Kafka properties
		props.put("group.id", "flink-truck-analytics-speed-consumer");
		
		//create json deserializer. TODO: Replace when SR Integration is complete
		JSONKeyValueDeserializationSchema jsonDeserializer = new JSONKeyValueDeserializationSchema(true);

		FlinkKafkaConsumer<ObjectNode> speedSource = new FlinkKafkaConsumer<ObjectNode>(SOURCE_SPEED_STREAM_TOPIC, jsonDeserializer, props);
		return speedSource;
	}

	private static FlinkKafkaConsumer<ObjectNode> constructGeoEventSource(Properties props) {
		
		//add additional Kafka properties
		props.put("group.id", "flink-truck-analytics-geo-consumer");
		
		//create json deserializer. TODO: Replace when SR Integration is complete
		JSONKeyValueDeserializationSchema jsonDeserializer = new JSONKeyValueDeserializationSchema(true);

		FlinkKafkaConsumer<ObjectNode> geoSource = new FlinkKafkaConsumer<ObjectNode>(SOURCE_GEO_STREAM_TOPIC, jsonDeserializer, props);
		return geoSource;
	}
}
