package cloudera.cdf.refapp.trucking.simulator.producer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.fasterxml.jackson.databind.ObjectMapper;

import cloudera.cdf.refapp.trucking.simulator.domain.AbstractEventCollector;
import cloudera.cdf.refapp.trucking.simulator.domain.transport.MobileEyeEvent;
import cloudera.cdf.refapp.trucking.simulator.producer.pojo.TruckGeoEvent;
import cloudera.cdf.refapp.trucking.simulator.producer.pojo.TruckSpeedEvent;

public abstract class BaseTruckEventCollector extends AbstractEventCollector {

	
	protected String createTruckGeoEvent(MobileEyeEvent mee) {
		long eventTimeLong = new Date().getTime();        
         
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        
        String eventTime = dateFormatGmt.format(new Date(eventTimeLong));
        
		String eventToPass =  eventTime + "|" + eventTimeLong + "|truck_geo_event|"  + mee.toString();
		return eventToPass;
	}	
	
	protected String createTruckGeoEventJsonString(MobileEyeEvent mee) {
		
		
		long eventTimeLong = new Date().getTime();        
         
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        
        String eventTime = dateFormatGmt.format(new Date(eventTimeLong));
        
        TruckGeoEvent geoEvent = new TruckGeoEvent();
        geoEvent.setEventTimeLong(eventTimeLong);
        geoEvent.setEventTime(eventTime);
        geoEvent.setEventSource("truck_geo_event");
        geoEvent.setTruckId( mee.getTruck().getTruckId());
        geoEvent.setDriverId(mee.getTruck().getDriver().getDriverId());
        geoEvent.setDriverName(mee.getTruck().getDriver().getDriverName());
        geoEvent.setRouteId(mee.getTruck().getDriver().getRoute().getRouteId());
        geoEvent.setRoute(mee.getTruck().getDriver().getRoute().getRouteName());
        geoEvent.setEventType(mee.getEventType().toString());
        geoEvent.setLatitude(mee.getLocation().getLatitude());
        geoEvent.setLongitude(mee.getLocation().getLongitude());
        geoEvent.setCorrelationId(mee.getCorrelationId());
        
		String eventToPass = "";
		try {
			eventToPass =  new ObjectMapper().writeValueAsString(geoEvent);
		} catch (Exception e) {
			logger.error("error converting object to json" , e);
		}
		return eventToPass;
		
	}	
	
	protected String createTruckSpeedEventJsonString(MobileEyeEvent mee) {
		
		long eventTimeLong = new Date().getTime();        
        
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        
        String eventTime = dateFormatGmt.format(new Date(eventTimeLong));
        
        TruckSpeedEvent speedEvent = new TruckSpeedEvent();
        speedEvent.setEventTimeLong(eventTimeLong);
        speedEvent.setEventTime(eventTime);
        speedEvent.setEventSource("truck_speed_event");
        speedEvent.setTruckId( mee.getTruck().getTruckId());
        speedEvent.setDriverId(mee.getTruck().getDriver().getDriverId());
        speedEvent.setDriverName(mee.getTruck().getDriver().getDriverName());
        speedEvent.setRouteId(mee.getTruck().getDriver().getRoute().getRouteId());
        speedEvent.setRoute(mee.getTruck().getDriver().getRoute().getRouteName());
        speedEvent.setSpeed(mee.getTruckSpeed());
        
		String eventToPass = "";
		try {
			eventToPass =  new ObjectMapper().writeValueAsString(speedEvent);
		} catch (Exception e) {
			logger.error("error converting object to json" , e);
		}
		return eventToPass;        

	}		
	
	protected String createTruckSpeedEvent(MobileEyeEvent mee) {
		
		long eventTimeLong = new Date().getTime();        
        
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        
        String eventTime = dateFormatGmt.format(new Date(eventTimeLong));
        
		String eventToPass = eventTime + "|" +eventTimeLong + "|truck_speed_event|" + mee.getTruck().toString() + "|" + mee.getTruckSpeed();
		return eventToPass;
	}	

	

}
