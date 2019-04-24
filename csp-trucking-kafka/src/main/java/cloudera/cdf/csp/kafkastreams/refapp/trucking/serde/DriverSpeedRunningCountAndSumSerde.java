package cloudera.cdf.csp.kafkastreams.refapp.trucking.serde;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import cloudera.cdf.csp.kafkastreams.refapp.trucking.aggregrator.DriverSpeedRunningCountAndSum;

public class DriverSpeedRunningCountAndSumSerde implements Serde<DriverSpeedRunningCountAndSum> {

	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Serializer<DriverSpeedRunningCountAndSum> serializer() {
		return new JsonPOJOSerializer<DriverSpeedRunningCountAndSum>();
	}

	@Override
	public Deserializer<DriverSpeedRunningCountAndSum> deserializer() {
		return new JsonPOJODeserializer<DriverSpeedRunningCountAndSum>(DriverSpeedRunningCountAndSum.class);
	}



}
