package cloudera.cdf.csp.flink.refapp.trucking.aggregrator;

import java.util.Date;

import org.apache.flink.api.common.functions.AggregateFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cloudera.cdf.csp.flink.refapp.trucking.dto.TruckGeoSpeedJoin;

public class DriverAverageSpeedAggregateFunctionNew implements AggregateFunction<TruckGeoSpeedJoin, DriverSpeedRunningCountAndSum, DriverSpeedAvgValue> {


	private static final long serialVersionUID = 8197909630331217700L;
	private static final Logger LOG = LoggerFactory.getLogger(DriverAverageSpeedAggregateFunctionNew.class);
	
	@Override
	public DriverSpeedRunningCountAndSum createAccumulator() {
		return new DriverSpeedRunningCountAndSum();
	}

	@Override
	public DriverSpeedRunningCountAndSum add(TruckGeoSpeedJoin geoSpeed,
			DriverSpeedRunningCountAndSum accumulator) {
		int speed = geoSpeed.getSpeed();
		int driverId = geoSpeed.getDriverid();
		String driverName = geoSpeed.getDrivername();
		String route = geoSpeed.getRoute();	
		
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
