package cloudera.cdf.csp.kafkastreams.refapp.trucking.serde;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import cloudera.cdf.csp.kafkastreams.refapp.trucking.aggregrator.DriverSpeedAvgValue;

public class DriverSpeedAvgValueSerde implements Serde<DriverSpeedAvgValue> {

	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Serializer<DriverSpeedAvgValue> serializer() {
		return new JsonPOJOSerializer<DriverSpeedAvgValue>();
	}

	@Override
	public Deserializer<DriverSpeedAvgValue> deserializer() {
		return new JsonPOJODeserializer<DriverSpeedAvgValue>(DriverSpeedAvgValue.class);
	}

}
