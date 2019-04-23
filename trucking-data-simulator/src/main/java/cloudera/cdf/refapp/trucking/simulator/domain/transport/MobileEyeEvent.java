package cloudera.cdf.refapp.trucking.simulator.domain.transport;

import cloudera.cdf.refapp.trucking.simulator.domain.Event;
import cloudera.cdf.refapp.trucking.simulator.domain.gps.Location;


public class MobileEyeEvent extends Event {
	private MobileEyeEventTypeEnum eventType;
	private Truck truck;
	private Location location;
	private long correlationId;
	
	private int truckSpeed;
	



	public int getTruckSpeed() {
		return truckSpeed;
	}

	public void setTruckSpeed(int truckSpeed) {
		this.truckSpeed = truckSpeed;
	}

	public MobileEyeEvent(long correlationId, Location location, MobileEyeEventTypeEnum eventType,
			Truck truck, int truckSpeed) {
		this.location = location;
		this.eventType = eventType;
		this.truck = truck;
		this.correlationId = correlationId;
		this.truckSpeed= truckSpeed;
	}

	public MobileEyeEventTypeEnum getEventType() {
		return eventType;
	}

	public void setEventType(MobileEyeEventTypeEnum eventType) {
		this.eventType = eventType;
	}

	public Location getLocation() {
		return location;
	}
	
	public Truck getTruck() {
		return this.truck;
	}

	@Override
	public String toString() {
		return truck.toString() + "|" + eventType.toString() + "|"
				+ location.getLatitude() + "|" + location.getLongitude() + "|" + correlationId ;
	}

	public long getCorrelationId() {
		return correlationId;
	}
	
	
}
