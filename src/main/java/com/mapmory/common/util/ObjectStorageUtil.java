package com.mapmory.common.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
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
    
    @Value("${cdn.url}")
    private String cdnUrl;
	
    private static String REGION = "kr-standard";
    private static String ENDPOINT = "https://kr.object.ncloudstorage.com";
    
    private AmazonS3 getS3Client() {
		BasicAWSCredentials awsCreds = new BasicAWSCredentials(objectAccessKey, objectSecretKey);
	    
	    AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
	            .withEndpointConfiguration(new EndpointConfiguration(ENDPOINT, REGION))
	            .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
	            .withPathStyleAccessEnabled(true)
	            .build();
	    
		return s3Client;
	}
    
	public void uploadFileToS3(MultipartFile file, String uuidFileName, String folderName) throws Exception {
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
    
    public void deleteFile(String uuidFileName,String folderName) throws Exception {
        String key = folderName + "/" + uuidFileName;
        AmazonS3 s3Client = getS3Client();
        s3Client.deleteObject(bucketName, key);
    }
    
    //상품 주소 가리기
    public ByteArrayResource getImageResource(String uuid,String folderName) throws Exception {
        AmazonS3 s3Client = getS3Client();
        String key = folderName + "/" + uuid;
        S3Object s3Object = s3Client.getObject(bucketName, key);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        byte[] bytes = IOUtils.toByteArray(inputStream);
        return new ByteArrayResource(bytes, uuid);
    }
    
    public byte[] getImageBytes(String uuid, String folderName) throws Exception {
        String imageUrl = getImageUrl(uuid, folderName);
        
        URL url = new URL(imageUrl);
        InputStream inputStream = url.openStream();
        byte[] bytes = IOUtils.toByteArray(inputStream);
        
        return bytes;
    }

    public String getImageUrl(String uuid,String folderName) {
        return cdnUrl + folderName + "/" + uuid;
    }
    
    /**
     * @param objectStorageDirectoryPath  object storage 내 target directory path를 full name으로 명시한다.
     * @param downloadDirectoryPath  본인이 local server 내에 저장하고 싶은 경로 위치에 저장한다. root 경로는 project 최상위 경로이다.
     * @throws Exception
     */
    public void downloadFile(String objectStorageDirectoryPath, String downloadDirectoryPath) throws Exception {
    	
    	final AmazonS3 s3 = getS3Client();
    	
    	Map<String, List<String>> result = getObjectStorageSelectFileList(s3, objectStorageDirectoryPath);
       	List<String> objectStorageFilePaths = result.get("filePaths");
		List<String> fileNames = result.get("fileNames");

    	for(int i = 0; i < objectStorageFilePaths.size(); i++) {
    		
    		String downloadFilePath = downloadDirectoryPath +"/"+ fileNames.get(i);
    		
    		// System.out.println("downloadFilePath : "+downloadFilePath);
    		try {
    			
    			// System.out.println("filePath : " + objectStorageFilePaths.get(i).trim());
    			
    			S3Object s3Object = s3.getObject(bucketName, objectStorageFilePaths.get(i).trim());
    			S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent();
    			
    			OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadDirectoryPath+fileNames.get(i)));
    			byte[] bytesArray = new byte[4096];
    			int bytesRead = -1;
    			while( (bytesRead = s3ObjectInputStream.read(bytesArray)) != -1) {
    				outputStream.write(bytesArray, 0, bytesRead);
    			}
    			
    			outputStream.close();
    			s3ObjectInputStream.close();
    			
    		} catch (AmazonS3Exception e) {
    		    e.printStackTrace();
    		} catch(SdkClientException e) {
    		    e.printStackTrace();
    		}
    	}
    	
    }
    
    private Map<String, List<String>> getObjectStorageSelectFileList(AmazonS3 s3, String objectStorageDirectoryPath) throws Exception {
		
		List<String> filePaths = new ArrayList<>();
		List<String> fileNames = new ArrayList<>();

		try {
			
			ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
					.withBucketName(bucketName)
					.withPrefix(objectStorageDirectoryPath)
					.withMaxKeys(300);
			
			ObjectListing objectListing = s3.listObjects(listObjectsRequest);

			for(S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
				String filePath = objectSummary.getKey();
				long fileSize = objectSummary.getSize();
				
				// System.out.println("    name=" + filePath + ", size=" + fileSize + ", owner=" + objectSummary.getOwner().getId());

				if(filePath.contains(objectStorageDirectoryPath) && fileSize != 0L) {
					
					String[] tempStr =filePath.split("/");
					int last = tempStr.length - 1;
					
					// System.out.println("tempStr : " + tempStr);
					
					fileNames.add(tempStr[last]);
					filePaths.add(filePath);
				}
			}
			
			Map<String, List<String>> result = new HashMap<>();
			result.put("filePaths", filePaths);
			result.put("fileNames", fileNames);

			return result;
			
		} catch(AmazonS3Exception e) {
		    System.err.println(e.getErrorMessage());
		} catch(SdkClientException e) {
		    e.printStackTrace();
		}
		
		return null;
	}
    
}
