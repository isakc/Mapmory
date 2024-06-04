package com.mapmory.common.util;

import org.springframework.beans.factory.annotation.Value;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class ObjectStorageUtil {
	
    @Value("${aitems.Access_Key}")
    private String aitems_Access_Key;
    
    @Value("${aitems.Secret_Key}")
    private String aitems_Secret_Key;
	
	public AmazonS3 getS3Client() {
		BasicAWSCredentials awsCreds = new BasicAWSCredentials(aitems_Access_Key, aitems_Secret_Key);
	    
		String REGION = "kr-standard";
	    String ENDPOINT = "https://kr.object.ncloudstorage.com";
		
	    AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
	            .withEndpointConfiguration(new EndpointConfiguration(ENDPOINT, REGION))
	            .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
	            .withPathStyleAccessEnabled(true)
	            .build();
	    
		return s3Client;
	}
    

	
}
