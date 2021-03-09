package cloudera.cdf.refapp.trucking.simulator.producer.file;


import java.io.File;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;

import cloudera.cdf.refapp.trucking.simulator.domain.transport.EventSourceType;
import cloudera.cdf.refapp.trucking.simulator.domain.transport.MobileEyeEvent;
import cloudera.cdf.refapp.trucking.simulator.producer.BaseTruckEventCollector;


public class FileJsonEventCollector extends BaseTruckEventCollector {


	private static final String LINE_BREAK = "\n";
	private File truckEventsFile;
	private EventSourceType eventSourceType;

	public FileJsonEventCollector(String fileName) {
       this.truckEventsFile = new File(fileName);
      
	}
	
	public FileJsonEventCollector(String fileName, EventSourceType eventSource) {
	       this.truckEventsFile = new File(fileName);
	       this.eventSourceType = eventSource;
	      
		}	
	
	@Override
	public void onReceive(Object event) throws Exception {

		MobileEyeEvent mee = (MobileEyeEvent) event;
		
		if(eventSourceType == null || EventSourceType.ALL_STREAMS.equals(eventSourceType)) {
			sendTruckEventToFile(mee);	
			sendTruckSpeedEventToFile(mee);	
		} else if(EventSourceType.GEO_EVENT_STREAM.equals(eventSourceType)) {
			sendTruckEventToFile(mee);	
		} else if (EventSourceType.SPEED_STREAM.equals(eventSourceType)) {	
			sendTruckSpeedEventToFile(mee);
		}
	}

	private void sendTruckEventToFile(MobileEyeEvent mee) {
		
		String eventToPass = createTruckGeoEventJsonString(mee) +"|" + LINE_BREAK;

		logger.debug("Creating truck geo event["+eventToPass+"] for driver["+mee.getTruck().getDriver().getDriverId() + "] in truck [" + mee.getTruck() + "]");	
					
		try {
			FileUtils.writeStringToFile(truckEventsFile, eventToPass, Charset.defaultCharset(), true);
		} catch (Exception e) {
			logger.error("Error sending event[" + eventToPass + "] to file[ " + truckEventsFile + " ] ", e);
		}		
		
	}

	private void sendTruckSpeedEventToFile(MobileEyeEvent mee) {
		
		String eventToPass = createTruckSpeedEventJsonString(mee) + "|" + LINE_BREAK;
		logger.debug("Creating truck speed event["+eventToPass+"] for driver["+mee.getTruck().getDriver().getDriverId() + "] in truck [" + mee.getTruck() + "]");
						
		
		try {
			FileUtils.writeStringToFile(truckEventsFile, eventToPass, Charset.defaultCharset(), true);
		} catch (Exception e) {
			logger.error("Error sending event[" + eventToPass + "] to file[ " + truckEventsFile + " ] ", e);
		}	
	}

}
