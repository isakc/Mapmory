package com.mapmory.common.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;

@Component
public class ObjectStorageUtil {
	
    @Value("${object.Access.Key}")
    private String objectAccessKey;
    
    @Value("${object.Secret.Key}")
    private String objectSecretKey;
    
    @Value("${object.bucketName}")
    private String bucketName;
    
    @Value("${object.folderName}")
    private String folderName;
    
    @Value("${cdn.url}")
    private String cdnUrl;
	
	public AmazonS3 getS3Client() {
		BasicAWSCredentials awsCreds = new BasicAWSCredentials(objectAccessKey, objectSecretKey);
	    
		String REGION = "kr-standard";
	    String ENDPOINT = "https://kr.object.ncloudstorage.com";
		
	    AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
	            .withEndpointConfiguration(new EndpointConfiguration(ENDPOINT, REGION))
	            .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
	            .withPathStyleAccessEnabled(true)
	            .build();
	    
		return s3Client;
	}
    
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
}
