package cloudera.cdf.refapp.trucking.simulator.producer.s3;


import java.io.File;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import cloudera.cdf.refapp.trucking.simulator.domain.transport.EventSourceType;
import cloudera.cdf.refapp.trucking.simulator.domain.transport.MobileEyeEvent;
import cloudera.cdf.refapp.trucking.simulator.producer.BaseTruckEventCollector;


public class S3MultiFileJsonEventCollector extends BaseTruckEventCollector {


	private static final String LINE_BREAK = "\n";
	private static final String FILE_TYPE = ".json";
	private File truckEventsFile;
	private EventSourceType eventSourceType;
	private int numOfEventsPerFile;
	private int currentEventCountPerFile = 0;
	private int fileSuffix = 0;
	private String fileNamePrefix;
	
	private AmazonS3 s3client;
	private String bucketName;
	private String bucketKeyPrefix;


	private StringBuffer eventBuffer;
	
	private static final String rootFolderForSimulatedData = "/Users/gvetticaden/df-functions-data-generator/vett-data-lake-1-oregon/vett-naaf/truck-telemetry-raw";
	

	public S3MultiFileJsonEventCollector(String s3FileNamePrefix, EventSourceType eventSource, int numOfEventsPerFile, String bucketName, String bucketKeyPrefixPath) {
	       this.fileNamePrefix = s3FileNamePrefix;
		   this.eventBuffer = new StringBuffer();
	       this.eventSourceType = eventSource;
	       this.numOfEventsPerFile = numOfEventsPerFile;
	       
	       this.bucketName = bucketName;
	       this.bucketKeyPrefix = bucketKeyPrefixPath;
	       createAWSClient();		
		}

	private void createAWSClient() {
		String accessKey = System.getProperty("accessKey");
		String accessPassword = System.getProperty("accessPassword");
		

		AWSCredentials credentials = new BasicAWSCredentials(
				  accessKey, 
				  accessPassword
				);	
		s3client = AmazonS3ClientBuilder
				  .standard()
				  .withCredentials(new AWSStaticCredentialsProvider(credentials))
				  .withRegion(Regions.US_WEST_2)
				  .build();
		
		if (!s3client.doesBucketExist(bucketName)) {
			throw new RuntimeException("Bucket["+bucketName+"] doesn't exist");
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
			uploadFileToS3();			
			
			fileSuffix++;
			currentEventCountPerFile = 0;
			eventBuffer = new StringBuffer();
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

	private void uploadFileToS3() {

		String shortFileName = truckEventsFile.getName();
		String bucketKey = bucketKeyPrefix + '/'+shortFileName;
		logger.info("Uploading file with Key["+bucketKey+"] to Bucket["+bucketName+"]");
		s3client.putObject(
				  bucketName, 
				  bucketKey, 
				  truckEventsFile
				);
			
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
		    this.truckEventsFile =  createFile(rootFolderForSimulatedData+ "/" + fileNamePrefix, fileSuffix);
			FileUtils.writeStringToFile(truckEventsFile, eventBuffer.toString(), Charset.defaultCharset(), true);
			logger.info("Writing the following contents to file["+truckEventsFile+"]: " + eventBuffer.toString());
		} catch (Exception e) {
			logger.error("Error file[ " + truckEventsFile + " ] ", e);
		}
	}
	
	

}
