package cloudera.cdf.refapp.trucking.simulator.domain.transport;

import cloudera.cdf.refapp.trucking.simulator.domain.interfaces.DomainObject;
import cloudera.cdf.refapp.trucking.simulator.domain.transport.route.Route;


public class Driver implements DomainObject {
	private static final long serialVersionUID = 6113264533619087412L;
	
	private int driverId;
	private int riskFactor;
	private Route route;
	
	private int routeTraversalCount = 0;

	private String driverName;

	private boolean speedyDriver;


	public Driver(int driverId, String driverName, int riskFactor) {
		this.driverId = driverId;
		this.riskFactor = riskFactor;
		this.setDriverName(driverName);

	}
	
	public Driver(int driverId, String driverName, int riskFactor, boolean speedyDriver) {
		this.driverId = driverId;
		this.riskFactor = riskFactor;
		this.setDriverName(driverName);
		this.speedyDriver = speedyDriver;
	}	

	public void provideRoute(Route route) {
		this.route = route;
		routeTraversalCount = 1;
	}
	
	public int getDriverId() {
		return driverId;
	}

	public void setDriverId(int driverId) {
		this.driverId = driverId;
	}

	public int getRiskFactor() {
		return riskFactor;
	}

	public void setRiskFactor(int riskFactor) {
		this.riskFactor = riskFactor;
	}

	@Override
	public String toString() {
		return this.driverId + "|" + this.riskFactor;
	}

	public Route getRoute() {
		return route;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	
	public void incrementRootTraversalCount() {
		this.routeTraversalCount++;
	}
	
	public int getRouteTraversalCount() {
		return this.routeTraversalCount;
	}
	
	public boolean isSpeedyDriver() {
		return speedyDriver;
	}

	public void setSpeedyDriver(boolean speedyDriver) {
		this.speedyDriver = speedyDriver;
	}
	

}
