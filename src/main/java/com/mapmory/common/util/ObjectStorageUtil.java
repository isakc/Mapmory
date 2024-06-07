package com.mapmory.common.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.policy.Resource;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;

@Component
public class ObjectStorageUtil {
	
    @Value("${object.Access.Key}")
    private String objectAccessKey;
    
    @Value("${object.Secret.Key}")
    private String objectSecretKey;
    
    @Value("${object.bucketName}")
    private String bucketName;
	
	@Value("${test.object.Access.Key}")
	private String accessKey;
	
	@Value("${test.object.Secret.Key}")
	private String secretKey;

	@Value("${test.object.bucketName}")
	private String testBucketName;
    
    @Value("${object.folderName}")
    private String folderName;
    
    @Value("${cdn.url}")
    private String cdnUrl;
	
    private static String REGION = "kr-standard";
    private static String ENDPOINT = "https://kr.object.ncloudstorage.com";
    
	public void uploadFileToS3(MultipartFile file, String uuidFileName) throws Exception {
	    File localFile = convertMultiPartToFile(file);
	    String key = folderName + "/" + uuidFileName; // 버킷 내 경로 설정 (UUID 값 사용)
	    AmazonS3 s3Client = getS3Client();
	    PutObjectResult result = s3Client.putObject(new PutObjectRequest(bucketName, key, localFile));
	}
    
    private File convertMultiPartToFile(MultipartFile file) throws Exception {
        String tempDir = System.getProperty("java.io.tmpdir");
        File convFile = new File(tempDir + "/" + file.getOriginalFilename());
        Files.copy(file.getInputStream(), Paths.get(convFile.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
        return convFile;
    }
    
    public void deleteFile(String uuidFileName) throws Exception {
        String key = folderName + "/" + uuidFileName;
        AmazonS3 s3Client = getS3Client();
        s3Client.deleteObject(bucketName, key);
    }
    
    //상품 주소 가리기
    public ByteArrayResource getImageResource(String uuid) throws Exception {
        AmazonS3 s3Client = getS3Client();
        String key = folderName + "/" + uuid;
        S3Object s3Object = s3Client.getObject(bucketName, key);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        byte[] bytes = IOUtils.toByteArray(inputStream);
        return new ByteArrayResource(bytes, uuid);
    }

    public String getImageUrl(ByteArrayResource imageResource) {
        return cdnUrl + "productImage/" + imageResource.getDescription();
    }
    
    public void downloadFile(String directoryPath) throws Exception {
    	
    	
    }
    
    private AmazonS3 getS3Client() {
		BasicAWSCredentials awsCreds = new BasicAWSCredentials(objectAccessKey, objectSecretKey);
	    
		
		
	    AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
	            .withEndpointConfiguration(new EndpointConfiguration(ENDPOINT, REGION))
	            .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
	            .withPathStyleAccessEnabled(true)
	            .build();
	    
		return s3Client;
	}
    
    public List<String> getObjectStorageSelectFileList(String directoryPath) throws Exception {
		
    	System.out.println("directoryPath : "+directoryPath);
    	
		// final AmazonS3 s3 = getS3Client();
    	// test 이후 변경할 것
    	final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
				.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(ENDPOINT, REGION))
				.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
				.build();
		
		List<String> result = new ArrayList<>();

		try {
			
			ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
					.withBucketName(testBucketName)
					// .withDelimiter("/")
					.withMaxKeys(300);
			
			ObjectListing objectListing = s3.listObjects(listObjectsRequest);
			
			System.out.println("File List:");
			for(S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
				// System.out.println("    name=" + objectSummary.getKey() + ", size=" + objectSummary.getSize() + ", owner=" + objectSummary.getOwner().getId());
				
				String fileName = objectSummary.getKey();
				long fileSize = objectSummary.getSize();
				
				if(fileName.contains(directoryPath+"/") && fileSize != 0L) {
					
					String[] tempStr =fileName.split("/");
					int last = tempStr.length - 1;
					result.add(tempStr[last].split("\\.")[0]);
				}
			}
			
			return result;
			
		} catch(AmazonS3Exception e) {
		    System.err.println(e.getErrorMessage());
		} catch(SdkClientException e) {
		    e.printStackTrace();
		}
		
		return null;
	}
}
