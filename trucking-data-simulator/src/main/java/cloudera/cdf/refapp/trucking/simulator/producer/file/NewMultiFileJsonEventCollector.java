package cloudera.cdf.refapp.trucking.simulator.producer.file;


import java.io.File;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;

import cloudera.cdf.refapp.trucking.simulator.domain.transport.EventSourceType;
import cloudera.cdf.refapp.trucking.simulator.domain.transport.MobileEyeEvent;
import cloudera.cdf.refapp.trucking.simulator.producer.BaseTruckEventCollector;


public class NewMultiFileJsonEventCollector extends BaseTruckEventCollector {


	private static final String LINE_BREAK = "\n";
	private static final String FILE_TYPE = ".json";
	private File truckEventsFile;
	private EventSourceType eventSourceType;
	private int numOfEventsPerFile;
	private int currentEventCountPerFile = 0;
	private int fileSuffix = 0;
	private String fileNamePrefix;
	
	
	
	StringBuffer eventBuffer;

	public NewMultiFileJsonEventCollector(String fileName, EventSourceType eventSource, int numOfEventsPerFile) {
	       this.fileNamePrefix = fileName;
	       this.truckEventsFile =  createFile(fileNamePrefix, fileSuffix);
		   this.eventBuffer = new StringBuffer();
	       this.eventSourceType = eventSource;
	       this.numOfEventsPerFile = numOfEventsPerFile;
		}


	private File createFile(String fileNamePrefix, int fileSuffix) {
		return  new File(fileNamePrefix+"-"+fileSuffix+FILE_TYPE);
	}	
	
	@Override
	public void onReceive(Object event) throws Exception {

		MobileEyeEvent mee = (MobileEyeEvent) event;
		
		logger.info("Count in Buffer is: " + currentEventCountPerFile + " and max count in file is: " + numOfEventsPerFile);
		
		
		if(currentEventCountPerFile > numOfEventsPerFile ) {
			
			writeBufferedEventsToFile();		
			
			fileSuffix++;
			currentEventCountPerFile = 0;
			eventBuffer = new StringBuffer();
			this.truckEventsFile = createFile(fileNamePrefix, fileSuffix);
			logger.info("Create new Streaming file["+this.truckEventsFile+"]");
		}
		
		if(eventSourceType == null || EventSourceType.ALL_STREAMS.equals(eventSourceType)) {
			sendTruckEventToFile(mee);	
			sendTruckSpeedEventToFile(mee);	
			
			currentEventCountPerFile = currentEventCountPerFile + 2;
		} else if(EventSourceType.GEO_EVENT_STREAM.equals(eventSourceType)) {
			sendTruckEventToFile(mee);	
			currentEventCountPerFile = currentEventCountPerFile + 1;
		} else if (EventSourceType.SPEED_STREAM.equals(eventSourceType)) {	
			sendTruckSpeedEventToFile(mee);
			currentEventCountPerFile = currentEventCountPerFile + 1;
		}
	}



	private void sendTruckEventToFile(MobileEyeEvent mee) {
		
		String eventToPass = createTruckGeoEventJsonString(mee) + LINE_BREAK;

		//logger.debug("Creating truck geo event["+eventToPass+"] for driver["+mee.getTruck().getDriver().getDriverId() + "] in truck [" + mee.getTruck() + "]");	
		
		eventBuffer.append(eventToPass);
					
		
	}

	private void sendTruckSpeedEventToFile(MobileEyeEvent mee) {
		
		String eventToPass = createTruckSpeedEventJsonString(mee) + LINE_BREAK;
		//logger.debug("Creating truck speed event["+eventToPass+"] for driver["+mee.getTruck().getDriver().getDriverId() + "] in truck [" + mee.getTruck() + "]");
		
		eventBuffer.append(eventToPass);
		
	}

	private void writeBufferedEventsToFile() {
		try {
			this.truckEventsFile =  createFile(fileNamePrefix, fileSuffix);	
			FileUtils.writeStringToFile(truckEventsFile, eventBuffer.toString(), Charset.defaultCharset(), true);
			logger.info("Writing the following contents to file["+truckEventsFile+"]: " + eventBuffer.toString());
		} catch (Exception e) {
			logger.error("Error file[ " + truckEventsFile + " ] ", e);
		}
	}
	
	

}
