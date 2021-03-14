package org.cde.trucking.spark;

import java.io.File;
import java.sql.Timestamp;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;


public class S3Test {
	
	private static final Logger LOG = LoggerFactory.getLogger(S3Test.class);
	
	@Test
	public void testS3Connection() throws Exception {

		LOG.info("testing log..");
		
		String bucketName = "vett-spark-test";
	
		String accessKey = System.getProperty("accessKey");
		String accessPassword = System.getProperty("accessPassword");
		

		AWSCredentials credentials = new BasicAWSCredentials(
				  accessKey, 
				  accessPassword
				);	
		
		AmazonS3 s3client = AmazonS3ClientBuilder
				  .standard()
				  .withCredentials(new AWSStaticCredentialsProvider(credentials))
				  .withRegion(Regions.US_WEST_2)
				  .build();		
		
		
		LOG.info("Bucket["+ bucketName+"] exists:" + s3client.doesBucketExist(bucketName));
		

		ListObjectsRequest listRequest = new ListObjectsRequest().withBucketName(bucketName).withMarker("truck-telemetry-raw/");
		for(S3ObjectSummary os: s3client.listObjects(listRequest).getObjectSummaries()) {
		    LOG.info(os.getKey());
		    S3Object s3object = s3client.getObject(bucketName, os.getKey());
		    S3ObjectInputStream inputStream = s3object.getObjectContent();
		    String fileName = "/Users/gvetticaden/Downloads/s3/"+os.getKey();
		    FileUtils.copyInputStreamToFile(inputStream, new File(fileName));				
		}
		
				
	}

}
