package cloudera.cdf.csp.flink.refapp.trucking;

import java.sql.Timestamp;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.functions.co.ProcessJoinFunction;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.util.serialization.JSONKeyValueDeserializationSchema;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cloudera.cdf.csp.flink.refapp.trucking.aggregrator.DriverAverageSpeedAggregateFunction;
import cloudera.cdf.csp.flink.refapp.trucking.aggregrator.DriverSpeedAvgValue;

/**
 * Cloudera Streaming Analytics (CSA) Reference application for Flink as part of Cloudera DataFlow Platform (CDF)
 * @author gvetticaden
 *
 */
public class TruckingStreamingAnalticsFlinkRefApp {

	private static final Logger LOG = LoggerFactory.getLogger(TruckingStreamingAnalticsFlinkRefApp.class);
	private static final String SOURCE_GEO_STREAM_TOPIC = "syndicate-geo-event-json";	
	private static final String SOURCE_SPEED_STREAM_TOPIC = "syndicate-speed-event-json";	
	
	public static void main(String[] args) throws Exception {
        final ParameterTool params = ParameterTool.fromArgs(args);
  
        validateArgs(params);        

    	StreamExecutionEnvironment see = createFlinkExecutionEnv();
    	
        //create Geo Stream Source
    	DataStream<ObjectNode> geoStream = createGeoStreamSource(params, see);
    	
    	/* create the Speed Stream Source */
    	DataStream<ObjectNode> speedStream = createSpeedStreamSource(params, see);
    	
		
		/* join the streams */
    	DataStream<ObjectNode> geoSpeedJoinedStream = joinStreams(geoStream,
				speedStream);
		
		/* filter the the joinStream */
		DataStream<ObjectNode> filteredStream =  filterStream(geoSpeedJoinedStream);
		
			
		/* Calculate average speed of driver */
		KeySelector<ObjectNode, Integer> keySelector = createKeySelector();
		DataStream<DriverSpeedAvgValue> driverAvgSpeedStream = filteredStream
					  .assignTimestampsAndWatermarks(createTimestampAndWatermarkAssigner2())
					  .keyBy(keySelector)
					  .window(TumblingEventTimeWindows.of(Time.minutes(3)))
					  .aggregate(new DriverAverageSpeedAggregateFunction());
							  
		
    	//print the driverAverageSpeedStream
		driverAvgSpeedStream.print();		
    	
    	see.execute();
	}


	
	private static AssignerWithPeriodicWatermarks<ObjectNode> createTimestampAndWatermarkAssigner2() {
		return new BoundedOutOfOrdernessTimestampExtractor<ObjectNode> (Time.seconds(5)) {

			private static final long serialVersionUID = -7309986872515747306L;

			@Override
			public long extractTimestamp(ObjectNode element) {
				long eventTime =  element.get("value").get("eventTimeLong").longValue();
				return eventTime;
			}
		};
	}

	private static DataStream<ObjectNode> filterStream(DataStream<ObjectNode> geoSpeedJoinedStream) {
		FilterFunction<ObjectNode> filter = new FilterFunction<ObjectNode>() {

			private static final long serialVersionUID = -8965164867642656170L;

			@Override
			public boolean filter(ObjectNode joinedStream) throws Exception {
				String eventType = joinedStream.get("value").get("eventType").asText();
				return !"Normal".equals(eventType);
			}
		};
		DataStream<ObjectNode> filteredGeoStream = geoSpeedJoinedStream.filter(filter).name("Filtered Stream for Violation Events");
		return filteredGeoStream;
	}



	private static DataStream<ObjectNode> joinStreams(
			DataStream<ObjectNode> geoStream, DataStream<ObjectNode> speedStream) {
		KeySelector<ObjectNode, Integer> keySelector = createKeySelector();
    	ProcessJoinFunction<ObjectNode, ObjectNode, ObjectNode > processJoinFunction = createProcessJoinFunction();
		DataStream<ObjectNode> geoSpeedJoinedStream = geoStream.keyBy(keySelector)
    			 .intervalJoin(speedStream.keyBy(keySelector))
    			 .between(Time.milliseconds(0), Time.milliseconds(1500))
    			 .process(processJoinFunction).name("Stream Join using Interval Join");
		return geoSpeedJoinedStream;
	}



	private static DataStream<ObjectNode> createSpeedStreamSource(final ParameterTool params,
			StreamExecutionEnvironment see) {
		FlinkKafkaConsumer<ObjectNode> speedStreamSource = constructSpeedEventSource(params.getProperties());
    	speedStreamSource.setStartFromLatest();
    	DataStream<ObjectNode> speedStream = see.addSource(speedStreamSource, "SpeedGeoStream");
		return speedStream;
	}



	private static DataStream<ObjectNode> createGeoStreamSource(final ParameterTool params,
			StreamExecutionEnvironment see) {
		FlinkKafkaConsumer<ObjectNode> geoStreamSource = constructGeoEventSource(params.getProperties());
    	geoStreamSource.setStartFromLatest();
    	DataStream<ObjectNode> geoStream = see.addSource(geoStreamSource, "TruckGeoStream");
		return geoStream;
	}



	private static StreamExecutionEnvironment createFlinkExecutionEnv() {
		StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();
    	see.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
		return see;
	}



	private static void validateArgs(final ParameterTool params) {
		if(StringUtils.isEmpty(params.get("bootstrap.servers")) ) {
			String errMsg = "No  kafkaBootstrapUrl  specified. Please run 'TruckingStreamingAnalticsRefApp  --bootstrap.servers <kafkaBootstrapUrl>  '";
            LOG.error(errMsg);
            throw new RuntimeException(errMsg);
        }
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
