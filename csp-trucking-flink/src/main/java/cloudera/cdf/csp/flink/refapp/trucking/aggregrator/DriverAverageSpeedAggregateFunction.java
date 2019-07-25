package cloudera.cdf.csp.flink.refapp.trucking.aggregrator;

import java.util.Date;

import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DriverAverageSpeedAggregateFunction implements AggregateFunction<ObjectNode, DriverSpeedRunningCountAndSum, DriverSpeedAvgValue> {


	private static final long serialVersionUID = 8197909630331217700L;
	private static final Logger LOG = LoggerFactory.getLogger(DriverAverageSpeedAggregateFunction.class);
	
	@Override
	public DriverSpeedRunningCountAndSum createAccumulator() {
		return new DriverSpeedRunningCountAndSum();
	}

	@Override
	public DriverSpeedRunningCountAndSum add(ObjectNode geoSpeed,
			DriverSpeedRunningCountAndSum accumulator) {
		int speed = geoSpeed.get("speed").asInt();
		int driverId = geoSpeed.get("value").get("driverId").asInt();
		String driverName = geoSpeed.get("value").get("driverName").asText();
		String route = geoSpeed.get("value").get("route").asText();		
		
		DriverSpeedRunningCountAndSum runningCountAndSum = new DriverSpeedRunningCountAndSum(driverId, driverName, route, 
				accumulator.getRunningCount() + 1, accumulator.getRunningSum() + speed);
		
		return runningCountAndSum;
	}

	@Override
	public DriverSpeedAvgValue getResult(
			DriverSpeedRunningCountAndSum accumulator) {
		LOG.info("Entering getResult of Aggregrate Function..");
		
		double average = accumulator.getRunningSum() / accumulator.getRunningCount();
		long eventTimeLong = new Date().getTime();
		DriverSpeedAvgValue driverAvgSpeed = new DriverSpeedAvgValue(accumulator.getDriverId(), accumulator.getDriverName(), 
				accumulator.getRoute(), average, eventTimeLong);
		
		LOG.info("In getResult of AggegrationFunction, the final average is: " + driverAvgSpeed);
		return driverAvgSpeed;
	}

	@Override
	public DriverSpeedRunningCountAndSum merge(DriverSpeedRunningCountAndSum a,
			DriverSpeedRunningCountAndSum b) {
		return new DriverSpeedRunningCountAndSum(a.getDriverId(), a.getDriverName(), a.getRoute(), 
				a.getRunningCount() + b.getRunningCount(), a.getRunningSum() + b.getRunningSum());
	}

}
