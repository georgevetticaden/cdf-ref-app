package cloudera.cdf.refapp.trucking.simulator;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobListOption;
import com.google.cloud.storage.StorageOptions;

public class GCPTest {
	
	
	@Test
	public void testConnection() throws Exception {
		
		String authFile = System.getProperty("gcp.auth.key.file");
		String projectName = "gcp-gvetticaden-sko";
		Credentials credentials = GoogleCredentials
				  .fromStream(new FileInputStream(authFile));
				
		Storage storage = StorageOptions.newBuilder().setCredentials(credentials)
				  .setProjectId(projectName).build().getService();	
				
		String bucketName = "vett-new-pm-cdp-oregon";
		String prefix= "vett-data-lake-1-oregon/vett-naaf/truck-telemetry-raw/";

	    
		Page<Blob> bucketData =  storage.list(bucketName, BlobListOption.prefix(prefix), BlobListOption.currentDirectory());
		
		
		for(Blob blob : bucketData.iterateAll()) {
			System.out.println(blob.getName());
		}
				
	}
	
	@Test
	public void writeFile() throws Exception {
		
		String authFile = System.getProperty("gcp.auth.key.file");
		String projectName = "gcp-gvetticaden-sko";
		Credentials credentials = GoogleCredentials
				  .fromStream(new FileInputStream(authFile));
				
		Storage storage = StorageOptions.newBuilder().setCredentials(credentials)
				  .setProjectId(projectName).build().getService();
		
		String bucketName = "vett-new-pm-cdp-oregon";		
		String objectName = "vett-data-lake-1-oregon/vett-naaf/truck-telemetry-raw/test.json";
		BlobId blobId = BlobId.of(bucketName, objectName);
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
		
		File file  = new File ("/vett-data-lake-1-oregon/vett-naaf/truck-telemetry-raw/truck-sensor-readings-0.json");
		storage.create(blobInfo, FileUtils.readFileToByteArray(file));
		
	
	}

}
