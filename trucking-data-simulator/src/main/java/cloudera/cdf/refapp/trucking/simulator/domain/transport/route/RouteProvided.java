package cloudera.cdf.refapp.trucking.simulator.domain.transport.route;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cloudera.cdf.refapp.trucking.simulator.domain.gps.Location;




public class RouteProvided implements Route {

	private static final Logger LOG = LoggerFactory.getLogger(RouteProvided.class);
	
	private List<Location> locations;
	private int locationIndex=0;
	private Integer routeId = -1;


	private boolean forward=true;
	private boolean routeEnded=false;

	private String routeName;

	
	public RouteProvided (int routeId, String routeName, List<Location> locations) {
		this.routeId = routeId;
		this.locations = locations;
		this.routeName = routeName;
	}

	public Location getStartingPoint() {
		return locations.get(0);
	}

	public Location getNextLocation() {
		Location location = null;
		if(locationIndex == locations.size()) {
			//go background if if we got the end
			LOG.info("Revering Direction..");
			locationIndex--;
			forward = false;
			routeEnded = true;
		} else if(locationIndex == -1) {
			//go forward
			LOG.info("Going Original Direction...");
			locationIndex++;
			forward=true;
			routeEnded = true;
		} else
			routeEnded = false;
		location = locations.get(locationIndex);
		nextLocationIndex(); 
		return location;
	}
	
	public void nextLocationIndex() {
		if(forward) {
			locationIndex++;
		} else {
			locationIndex--;
		}
	}

	public List<Location> getLocations() {
		return this.locations;
	}

	@Override
	public boolean routeEnded() {
		return routeEnded;
	}

	public int getRouteId() {
		return routeId;
	}
	
	public String getRouteName() {
		return this.routeName;
	}
	
	public void setRouteId(Integer routeId) {
		this.routeId = routeId;
	}	

}
