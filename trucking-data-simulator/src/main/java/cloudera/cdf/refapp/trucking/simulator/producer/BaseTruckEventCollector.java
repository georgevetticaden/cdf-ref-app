package cloudera.cdf.refapp.trucking.simulator.producer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import cloudera.cdf.refapp.trucking.simulator.domain.AbstractEventCollector;
import cloudera.cdf.refapp.trucking.simulator.domain.transport.MobileEyeEvent;

public abstract class BaseTruckEventCollector extends AbstractEventCollector {

	
	protected String createTruckGeoEvent(MobileEyeEvent mee) {
		long eventTimeLong = new Date().getTime();        
         
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        
        String eventTime = dateFormatGmt.format(new Date(eventTimeLong));
        
		String eventToPass =  eventTime + "|" + eventTimeLong + "|truck_geo_event|"  + mee.toString();
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
	
//	protected String createTruckSpeedEvent(MobileEyeEvent mee) {
//		
//        long eventTimeLong = new Date().getTime();
//        String eventTime = new Timestamp(eventTimeLong).toString();
//        
//        
//		String eventToPass = eventTime + "|" +eventTimeLong + "|truck_speed_event|" + mee.getTruck().toString() + "|" + mee.getTruckSpeed();
//		return eventToPass;
//	}
	

	
//	protected String createTruckGeoEvent(MobileEyeEvent mee) {
//        long eventTimeLong = new Date().getTime();
//        String eventTime = new Timestamp(eventTimeLong).toString();
//        
//		String eventToPass =  eventTime + "|" + eventTimeLong + "|truck_geo_event|"  + mee.toString();
//		return eventToPass;
//	}	

	

}
