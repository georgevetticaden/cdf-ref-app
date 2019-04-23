package cloudera.cdf.refapp.trucking.simulator.domain.transport.route;

import java.util.List;

import cloudera.cdf.refapp.trucking.simulator.domain.gps.Location;


public interface Route {
	List<Location> getLocations();
	Location getNextLocation();
	Location getStartingPoint();
	boolean routeEnded();
	int getRouteId();
	String getRouteName();
	
}