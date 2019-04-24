package cloudera.cdf.csp.kafkastreams.refapp.trucking.aggregrator;

import org.apache.kafka.streams.kstream.Aggregator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cloudera.cdf.csp.kafkastreams.refapp.trucking.dto.TruckGeoSpeedJoin;

public class DriverAvgSpeedAgregrator implements Aggregator<String, TruckGeoSpeedJoin, DriverSpeedRunningCountAndSum> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DriverAvgSpeedAgregrator.class);

	@Override
	public DriverSpeedRunningCountAndSum apply(String key, TruckGeoSpeedJoin value,
			DriverSpeedRunningCountAndSum aggregate) {
			
		DriverSpeedRunningCountAndSum newAggregation =  new DriverSpeedRunningCountAndSum(value.getDriverid(), value.getDrivername(), value.getRoute(),
									  	aggregate.getRunningCount() + 1, aggregate.getRunningSum() + value.getSpeed());
		
		//LOGGER.debug("New Avg Aggregtion Value: " + newAggregation.toString());
		return newAggregation;
	}

}
