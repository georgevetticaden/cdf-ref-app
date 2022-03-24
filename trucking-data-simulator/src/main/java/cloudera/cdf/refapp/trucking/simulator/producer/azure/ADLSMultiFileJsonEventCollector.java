package cloudera.cdf.refapp.trucking.simulator.producer.azure;


import java.io.File;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;

import cloudera.cdf.refapp.trucking.simulator.domain.transport.EventSourceType;
import cloudera.cdf.refapp.trucking.simulator.domain.transport.MobileEyeEvent;
import cloudera.cdf.refapp.trucking.simulator.producer.BaseTruckEventCollector;

import com.azure.storage.common.StorageSharedKeyCredential;
import com.azure.storage.file.datalake.DataLakeDirectoryClient;
import com.azure.storage.file.datalake.DataLakeFileClient;
import com.azure.storage.file.datalake.DataLakeServiceClientBuilder;


public class ADLSMultiFileJsonEventCollector extends BaseTruckEventCollector {


	private static final String LINE_BREAK = "\n";
	private static final String FILE_TYPE = ".json";
	private File truckEventsFile;
	private EventSourceType eventSourceType;
	private int numOfEventsPerFile;
	private int currentEventCountPerFile = 0;
	private int fileSuffix = 0;
	private String fileNamePrefix;
		
	private DataLakeDirectoryClient azureDataLakeDirectoryClient;
	
	//Azure Container/FileSystem Name
	private String azureContainerName;
	//The directory where files will be written under the container/filesystem
	private String stagingDirectoryName;
	
	StringBuffer eventBuffer;
	

	//private static final String rootFolderForSimulatedData = "/vett-data-lake-1-oregon/vett-naaf/truck-telemetry-raw";

	private static final String rootFolderForSimulatedData = "/Users/gvetticaden/df-functions-data-generator/vett-data-lake-1-oregon/vett-naaf/truck-telemetry-raw";

	public ADLSMultiFileJsonEventCollector(String fileName, EventSourceType eventSource, int numOfEventsPerFile, String containerName, String directoryName) {
	       this.fileNamePrefix = fileName;
		   this.eventBuffer = new StringBuffer();
	       this.eventSourceType = eventSource;
	       this.numOfEventsPerFile = numOfEventsPerFile;
	       this.azureContainerName = containerName;
	       this.stagingDirectoryName = directoryName;
	       crerateAzureDataLakeDirectoryClient();		
		}

	private void crerateAzureDataLakeDirectoryClient() {
		
		String accountName = System.getProperty("azure.storage.account.name");
		String accountKey = System.getProperty("azure.storage.account.key");

		StorageSharedKeyCredential sharedKeyCredential =
		        new StorageSharedKeyCredential(accountName, accountKey);	 
		DataLakeServiceClientBuilder builder = new DataLakeServiceClientBuilder();
		builder.credential(sharedKeyCredential);
		builder.endpoint("https://" + accountName + ".dfs.core.windows.net");

		/* Create the Azure DataLakeDirectoryClient  */
		this.azureDataLakeDirectoryClient = builder.buildClient()
				.getFileSystemClient(this.azureContainerName)
				.getDirectoryClient(this.stagingDirectoryName);		

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
			uploadFileToAzureDataLake();			
			
			fileSuffix++;
			currentEventCountPerFile = 0;
			eventBuffer = new StringBuffer();
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

	private void uploadFileToAzureDataLake() throws Exception {
		String fileName = truckEventsFile.getName();
		String fullPath = truckEventsFile.getAbsolutePath();
		
		logger.info("Uploading file["+fullPath+"] to Azure Container/FileSystem["+this.azureContainerName+"] in directory["+this.stagingDirectoryName+"] with name["+fileName+"]");
		
		DataLakeFileClient fileClient = this.azureDataLakeDirectoryClient.createFile(fileName, true);
	    
	    fileClient.uploadFromFile(fullPath, true);				
	
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
		    this.truckEventsFile =  createFile(rootFolderForSimulatedData+fileNamePrefix, fileSuffix);

			FileUtils.writeStringToFile(truckEventsFile, eventBuffer.toString(), Charset.defaultCharset(), true);
			logger.info("Writing the following contents to file["+truckEventsFile+"]: " + eventBuffer.toString());
		} catch (Exception e) {
			logger.error("Error file[ " + truckEventsFile + " ] ", e);
		}
	}
	
	

}
