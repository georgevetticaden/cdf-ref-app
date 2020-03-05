package cloudera.cdf.csp.flink.refapp.trucking;

import java.util.Properties;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.formats.avro.registry.cloudera.SchemaRegistryDeserializationSchema;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.functions.co.ProcessJoinFunction;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.sink.SinkFunction.Context;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cloudera.cdf.csp.flink.refapp.trucking.aggregrator.DriverAverageSpeedAggregateFunctionNew;
import cloudera.cdf.csp.flink.refapp.trucking.aggregrator.DriverSpeedAvgValue;
import cloudera.cdf.csp.flink.refapp.trucking.dto.TruckGeoSpeedJoin;
import cloudera.cdf.csp.flink.util.Utils;
import cloudera.cdf.csp.schema.refapp.trucking.TruckGeoEventEnriched;
import cloudera.cdf.csp.schema.refapp.trucking.TruckSpeedEventEnriched;

/**
 * Cloudera Streaming Analytics (CSA) Reference application for Flink as part of Cloudera DataFlow Platform (CDF)
 * @author gvetticaden
 *
 */
public class TruckingStreamingAnalticsFlinkRefAppWithSchemaRegistry {

	private static final Logger LOG = LoggerFactory.getLogger(TruckingStreamingAnalticsFlinkRefAppWithSchemaRegistry.class);
	private static final String SOURCE_GEO_STREAM_TOPIC = "syndicate-geo-event-avro";	
	private static final String SOURCE_SPEED_STREAM_TOPIC = "syndicate-speed-event-avro";
	private static final String SINK_ALERTS_SPEEDING_DRIVER_TOPIC= "alerts-speeding-drivers";
	
	protected static final double HIGH_SPEED = 80;	
	
	public static void main(String[] args) throws Exception {
        final ParameterTool params = Utils.parseArgs(args);
  

    	StreamExecutionEnvironment see = createFlinkExecutionEnv();
    	
    	
        //create Geo Stream Source
    	DataStream<TruckGeoEventEnriched> geoStream = createGeoStreamSource(params, see);
    	//the following map transform is hack workaround for a bug with Flink/SR integration. Need to remove once bug if fixed
    	geoStream = geoStream.map(i -> i).keyBy(t -> t.getDriverId());
    
    	
    	/* create the Speed Stream Source */
    	DataStream<TruckSpeedEventEnriched> speedStream = createSpeedStreamSource(params, see);
    	//the following map transform is hack workaround for a bug with Flink/SR integration. Need to remove once bug if fixed
    	speedStream = speedStream.map(i -> i).keyBy(t -> t.getDriverId());
		
		/* join the streams */
    	DataStream<TruckGeoSpeedJoin> geoSpeedJoinedStream = joinStreams(geoStream,
				speedStream);
    	
    	    	
		/* Calculate average speed of driver */
		DataStream<DriverSpeedAvgValue> driverAvgSpeedStream = geoSpeedJoinedStream
					  .assignTimestampsAndWatermarks(createTimestampAndWatermarkAssigner2())
					  .keyBy(createKeySelectorForJoinStream())
					  .window(TumblingEventTimeWindows.of(Time.minutes(3)))
					  .aggregate(new DriverAverageSpeedAggregateFunctionNew());
							  
		/* Filter for Speeding Drivers */
		DataStream<DriverSpeedAvgValue> filteredSpeedingDrivers = filterStreamForSpeedingDrivers(driverAvgSpeedStream);
		
		/* Publish Speeding Drivers to Kafka Topic */
		DataStream<String> filteredSpeedingDriversString = filteredSpeedingDrivers.map(new MapFunction<DriverSpeedAvgValue, String>() {

			private static final long serialVersionUID = -6828758780853422014L;

			@Override
			public String map(DriverSpeedAvgValue value) throws Exception {
				return new ObjectMapper().writeValueAsString(value);
			}
		});		
		
		filteredSpeedingDriversString.print();
		filteredSpeedingDriversString.addSink(constructSpeedingDriversKafkaSink(params)).name("Kafka Speeding Drivers Alert");
		

    	see.execute("Trucking Streaming Anlaytics Flink App");

  
	}


	
	private static AssignerWithPeriodicWatermarks<TruckGeoSpeedJoin> createTimestampAndWatermarkAssigner2() {
		return new BoundedOutOfOrdernessTimestampExtractor<TruckGeoSpeedJoin> (Time.seconds(5)) {

			private static final long serialVersionUID = -7309986872515747306L;

			@Override
			public long extractTimestamp(TruckGeoSpeedJoin speedJoin) {
				long eventTime = speedJoin.getGeoeventtimelong();  
				return eventTime;
			}
		};
	}



	private static DataStream<DriverSpeedAvgValue> filterStreamForSpeedingDrivers(DataStream<DriverSpeedAvgValue> driverSpeedAverageStream) {
		FilterFunction<DriverSpeedAvgValue> filter = new FilterFunction<DriverSpeedAvgValue>() {

			private static final long serialVersionUID = -6594353756761484254L;

			@Override
			public boolean filter(DriverSpeedAvgValue value) throws Exception {
				return value.getSpeed_avg() > HIGH_SPEED;
			}
		};
		DataStream<DriverSpeedAvgValue> highSpeedingDriversStreams = driverSpeedAverageStream.filter(filter).name("Filtering Stream for Speeding Drivers");
		return highSpeedingDriversStreams;
	}	


	private static DataStream<TruckGeoSpeedJoin> joinStreams(
			DataStream<TruckGeoEventEnriched> geoStream, DataStream<TruckSpeedEventEnriched> speedStream) {
    	ProcessJoinFunction<TruckGeoEventEnriched, TruckSpeedEventEnriched, TruckGeoSpeedJoin> processJoinFunction = createProcessJoinFunction();
		DataStream<TruckGeoSpeedJoin> geoSpeedJoinedStream = geoStream.keyBy(createKeySelectorForGeoStream())
    			 .intervalJoin(speedStream.keyBy(createKeySelectorForSpeedStream()))
    			 .between(Time.milliseconds(-500), Time.milliseconds(500))
    			 .process(processJoinFunction).name("Stream Join using Interval Join");
		return geoSpeedJoinedStream;
	}



	private static DataStream<TruckSpeedEventEnriched> createSpeedStreamSource(final ParameterTool params,
			StreamExecutionEnvironment see) {
		FlinkKafkaConsumer<TruckSpeedEventEnriched> speedStreamSource = constructSpeedEventSource(params);
    	speedStreamSource.setStartFromLatest();
    	DataStream<TruckSpeedEventEnriched> speedStream = see.addSource(speedStreamSource, "Kafka SpeedGeoStream");
		return speedStream;
	}



	private static DataStream<TruckGeoEventEnriched> createGeoStreamSource(final ParameterTool params,
			StreamExecutionEnvironment see) {
		FlinkKafkaConsumer<TruckGeoEventEnriched> geoStreamSource = constructGeoEventSource(params);
    	geoStreamSource.setStartFromLatest();
    	DataStream<TruckGeoEventEnriched> geoStream = see.addSource(geoStreamSource, "Kafka TruckGeoStream");
		return geoStream;
	}
	
//	private static DataStream<Tuple2<Integer, TruckGeoEventEnriched>> createGeoStreamSource(final ParameterTool params,
//			StreamExecutionEnvironment see) {
//		FlinkKafkaConsumer<Tuple2<Integer, TruckGeoEventEnriched>> geoStreamSource = constructGeoEventSource(params);
//    	geoStreamSource.setStartFromLatest();
//    	DataStream<Tuple2<Integer, TruckGeoEventEnriched>> geoStream = see.addSource(geoStreamSource, "Kafka TruckGeoStream");
//		return geoStream;
//	}	



	private static StreamExecutionEnvironment createFlinkExecutionEnv() {
		StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();
    	see.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
		return see;
	}

	private static ProcessJoinFunction<TruckGeoEventEnriched, TruckSpeedEventEnriched, TruckGeoSpeedJoin> createProcessJoinFunction() {
		return new ProcessJoinFunction<TruckGeoEventEnriched, TruckSpeedEventEnriched, TruckGeoSpeedJoin>() {

			private static final long serialVersionUID = 2797987756677620500L;

			@Override
			public void processElement(
					TruckGeoEventEnriched geoStream,
					TruckSpeedEventEnriched speedStream,
					ProcessJoinFunction<TruckGeoEventEnriched, TruckSpeedEventEnriched, TruckGeoSpeedJoin>.Context context,
					Collector<TruckGeoSpeedJoin> collector) throws Exception {
				TruckGeoSpeedJoin geoSpeedJoin = new TruckGeoSpeedJoin(geoStream, speedStream);
				collector.collect(geoSpeedJoin);
			}
		};
	}


	
	private static KeySelector<TruckGeoSpeedJoin, Integer> createKeySelectorForJoinStream() {
		return new KeySelector<TruckGeoSpeedJoin, Integer>() {

			private static final long serialVersionUID = 1344275833268319633L;

			@Override
			public Integer getKey(TruckGeoSpeedJoin value) throws Exception {
				return value.getDriverid();
			}
		};
	}		

	private static KeySelector<TruckGeoEventEnriched, Integer> createKeySelectorForGeoStream() {
		return new KeySelector<TruckGeoEventEnriched, Integer>() {

			private static final long serialVersionUID = 692727801129087793L;

			@Override
			public Integer getKey(TruckGeoEventEnriched value) throws Exception {
				return value.getDriverId(); 
			}

		};
	}		

	private static KeySelector<TruckSpeedEventEnriched, Integer> createKeySelectorForSpeedStream() {
		return new KeySelector<TruckSpeedEventEnriched, Integer>() {
			
			private static final long serialVersionUID = 3628670716077010223L;

			@Override
			public Integer getKey(TruckSpeedEventEnriched value) throws Exception {
				return value.getDriverId(); 
			}

		};
	}		
	

	private static FlinkKafkaConsumer<TruckSpeedEventEnriched> constructSpeedEventSource(ParameterTool params) {
		
		//Create new properties object and add additional props
		Properties speedEventSourceProps = new Properties();
		speedEventSourceProps.putAll(Utils.readKafkaProperties(params));
		speedEventSourceProps.put("group.id", "flink-truck-speed-consumer");
		

		KafkaDeserializationSchema<TruckSpeedEventEnriched> schema = SchemaRegistryDeserializationSchema
				.builder(TruckSpeedEventEnriched.class)
				.setConfig(Utils.readSchemaRegistryPropertiesNonSecure(params))
				.build();			

		FlinkKafkaConsumer<TruckSpeedEventEnriched> speedSource = new FlinkKafkaConsumer<TruckSpeedEventEnriched>(SOURCE_SPEED_STREAM_TOPIC, schema, speedEventSourceProps);
		return speedSource;
	}

//	private static FlinkKafkaConsumer<Tuple2<Integer, TruckGeoEventEnriched>> constructGeoEventSource(ParameterTool params) {
//		
//		//Create new properties object and add additional props
//		Properties geoEventSourceProps = new Properties();
//		geoEventSourceProps.putAll(Utils.readKafkaProperties(params));
//		geoEventSourceProps.put("group.id", "flink-truck-geo-consumer");
//		
//		
//		KafkaDeserializationSchema<Tuple2<Integer, TruckGeoEventEnriched>> schema = SchemaRegistryDeserializationSchema
//				.builder(TruckGeoEventEnriched.class)
//				.readKey(Integer.class)
//				.setConfig(Utils.readSchemaRegistryPropertiesNonSecure(params))
//				.build();		
//
//		FlinkKafkaConsumer<Tuple2<Integer, TruckGeoEventEnriched>> geoSource = new FlinkKafkaConsumer<Tuple2<Integer, TruckGeoEventEnriched>>(SOURCE_GEO_STREAM_TOPIC, schema, geoEventSourceProps);
//
//		//FlinkKafkaConsumer<TruckGeoEventEnriched> geoSource = new FlinkKafkaConsumer<TruckGeoEventEnriched>(SOURCE_GEO_STREAM_TOPIC, schema, geoEventSourceProps);
//		return geoSource;
//	}
	
	private static FlinkKafkaConsumer<TruckGeoEventEnriched> constructGeoEventSource(ParameterTool params) {
		
		//Create new properties object and add additional props
		Properties geoEventSourceProps = new Properties();
		geoEventSourceProps.putAll(Utils.readKafkaProperties(params));
		geoEventSourceProps.put("group.id", "flink-truck-geo-consumer");
		
		
		KafkaDeserializationSchema<TruckGeoEventEnriched> schema = SchemaRegistryDeserializationSchema
				.builder(TruckGeoEventEnriched.class)
				.setConfig(Utils.readSchemaRegistryPropertiesNonSecure(params))
				.build();		

		FlinkKafkaConsumer<TruckGeoEventEnriched> geoSource = new FlinkKafkaConsumer<TruckGeoEventEnriched>(SOURCE_GEO_STREAM_TOPIC, schema, geoEventSourceProps);
		return geoSource;
	}	
	
	public static  FlinkKafkaProducer<String> constructSpeedingDriversKafkaSink(ParameterTool params) {
		
		//Create new properties object and add additional props
		Properties speedingDriversSinkProps = new Properties();	
		speedingDriversSinkProps.putAll(Utils.readKafkaProperties(params));
		speedingDriversSinkProps.put("client.id", "flink-truck-alerts-producer");

		FlinkKafkaProducer<String> flinkKafkaProducer = new FlinkKafkaProducer<String>(SINK_ALERTS_SPEEDING_DRIVER_TOPIC, new SimpleStringSchema(), speedingDriversSinkProps);
		return flinkKafkaProducer;
	}
	
	/* Not using for now */
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
}
