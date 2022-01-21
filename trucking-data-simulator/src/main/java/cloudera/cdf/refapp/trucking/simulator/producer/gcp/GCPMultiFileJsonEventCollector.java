package cloudera.cdf.refapp.trucking.simulator.producer.gcp;


import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;


import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import cloudera.cdf.refapp.trucking.simulator.domain.transport.EventSourceType;
import cloudera.cdf.refapp.trucking.simulator.domain.transport.MobileEyeEvent;
import cloudera.cdf.refapp.trucking.simulator.producer.BaseTruckEventCollector;


public class GCPMultiFileJsonEventCollector extends BaseTruckEventCollector {


	private static final String LINE_BREAK = "\n";
	private static final String FILE_TYPE = ".json";
	private File truckEventsFile;
	private EventSourceType eventSourceType;
	private int numOfEventsPerFile;
	private int currentEventCountPerFile = 0;
	private int fileSuffix = 0;
	private String fileNamePrefix;
	
	private Storage googleCloudStorageClient;
	
	private String bucketName;
	
	StringBuffer eventBuffer;


	public GCPMultiFileJsonEventCollector(String fileName, EventSourceType eventSource, int numOfEventsPerFile, String bucketName) {
	       this.fileNamePrefix = fileName;
	       this.truckEventsFile =  createFile(fileNamePrefix, fileSuffix);
		   this.eventBuffer = new StringBuffer();
	       this.eventSourceType = eventSource;
	       this.numOfEventsPerFile = numOfEventsPerFile;
	       this.bucketName = bucketName;
	       createGCPClient();		
		}

	private void createGCPClient() {

		try {
		String authFile = System.getProperty("gcp.auth.key.file");
		String projectName = "gcp-gvetticaden-sko";
		Credentials credentials = GoogleCredentials
				  .fromStream(new FileInputStream(authFile));
				
		this.googleCloudStorageClient = StorageOptions.newBuilder().setCredentials(credentials)
				  .setProjectId(projectName).build().getService();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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
			uploadFileToGCP();			
			
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

	private void uploadFileToGCP() throws Exception {
		String bucketKey = truckEventsFile.getAbsolutePath().substring(1);
		logger.info("Uploading file with Key["+bucketKey+"] to Bucket["+bucketName+"]");

	
		BlobId blobId = BlobId.of(bucketName, bucketKey);
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
		
		this.googleCloudStorageClient.create(blobInfo, FileUtils.readFileToByteArray(truckEventsFile));		
		
	
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
