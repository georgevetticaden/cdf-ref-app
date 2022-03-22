package cloudera.cdf.refapp.trucking.simulator;

import java.io.File;

import org.junit.Test;

import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.common.StorageSharedKeyCredential;
import com.azure.storage.file.datalake.DataLakeDirectoryClient;
import com.azure.storage.file.datalake.DataLakeFileClient;
import com.azure.storage.file.datalake.DataLakeServiceClient;
import com.azure.storage.file.datalake.DataLakeServiceClientBuilder;
import com.azure.storage.file.datalake.models.FileSystemItem;
import com.azure.storage.file.datalake.models.PathItem;

public class ADLSTest {
	
	
	@Test
	public void testConnection() {
		
		String accountName = System.getProperty("azure.storage.account.name");
		String accountKey = System.getProperty("azure.storage.account.key");
		
		 
		//create FileSystemClient
		DataLakeServiceClient aDLS2Client = createADLS2Client(accountName,
				accountKey);		
		
		PagedIterable<FileSystemItem> fileSystems = aDLS2Client.listFileSystems();
		
		for(FileSystemItem fs: fileSystems) {
			System.out.println("File System Name: " + fs.getName());
		}
			
	}


	@Test
	public void writeFile() {
		
		String accountName = System.getProperty("azure.storage.account.name");
		String accountKey = System.getProperty("azure.storage.account.key");
		/* This maps to the container on Azure Storage */
		String fileSystemName = "truck-telemetry-raw";
		
		/* Seem like you can't write directly to container so need a directory. Hence why we have stage */
		String directory = "stage";
		
		String fileName = "truck-sensor-readings-0.json";
		
	    
		
		DataLakeDirectoryClient aDLSDirectory = createADLS2Client(accountName, accountKey)
												.getFileSystemClient(fileSystemName)
												.getDirectoryClient(directory);
						
		PagedIterable<PathItem> paths = aDLSDirectory.listPaths();
		for(PathItem pathItem: paths) {
			System.out.println(pathItem.getName());
		}	    
		
		DataLakeFileClient fileClient = aDLSDirectory.createFile(fileName, true);
	    
	    fileClient.uploadFromFile("/vett-data-lake-1-oregon/vett-naaf/truck-telemetry-raw/truck-sensor-readings-0.json", true);
				
	}
	
	
	private DataLakeServiceClient createADLS2Client(String accountName,
			String accountKey) {
		StorageSharedKeyCredential sharedKeyCredential =
			        new StorageSharedKeyCredential(accountName, accountKey);


		 
		DataLakeServiceClientBuilder builder = new DataLakeServiceClientBuilder();

		builder.credential(sharedKeyCredential);
		builder.endpoint("https://" + accountName + ".dfs.core.windows.net");
		DataLakeServiceClient dataLakeServiceClient = builder.buildClient();
		return dataLakeServiceClient;
	}	

	
	@Test
	public void fileTest() {
		File file  = new File ("/vett-data-lake-1-oregon/vett-naaf/truck-telemetry-raw/truck-sensor-readings-0.json");		
		System.out.println(file.getName());
		System.out.println(file.getAbsolutePath());
	}
}
