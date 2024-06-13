package com.mapmory.controller.chatting;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.mapmory.common.domain.SessionData;
import com.mapmory.services.user.service.LoginService;

@CrossOrigin(origins = {"http://192.168.0.45:3000","http://localhost:3000"},allowCredentials = "true")
@RestController
@RequestMapping("/chat/*")
public class ChattingRestController {
	
	@Value("${mongoURL}")
	String mongoURL;
	
	@Value("${aitems.Access_Key}")
    private String aitems_Access_Key;
    
    @Value("${aitems.Secret_Key}")
    private String aitems_Secret_Key;
	
	@Autowired
	private LoginService loginService;
	
	public ChattingRestController() {
		System.out.println(this.getClass());
	}
	
	//mongoDB 접속 String 전달
	@GetMapping("json/getMongo")
	public String getMongo() throws Exception {	
		return mongoURL;
	}
	
	//UserId 받아오기
	@GetMapping("json/getUser")
	public String getUser(HttpServletRequest request) throws Exception{
		System.out.println("json/getUser");
		try {
			SessionData sessionData =loginService.getSession(request);
			String userId = sessionData.getUserId();
			System.out.println("+_+_+_+_+_+"+userId+"_+_+_+_+_+_+");
			return userId;
		} catch (Exception e) {
			System.out.println(e);
			
		}
		return null;
	}
	
 	@PostMapping("json/addChatImage")
    public List<String> addChatImage(MultipartHttpServletRequest request,
                               @RequestParam("files") List<MultipartFile> files,
                               @RequestParam("chatId") String chatId) throws Exception {

 		List<String> resultSrc = new ArrayList<String>();
 		
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(aitems_Access_Key, aitems_Secret_Key);

        String REGION = "kr-standard";
        String ENDPOINT = "https://kr.object.ncloudstorage.com";

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new EndpointConfiguration(ENDPOINT, REGION))
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withPathStyleAccessEnabled(true)
                .build();

        String folderName = chatId + "/";

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(0L);
        objectMetadata.setContentType("application/x-directory");
        PutObjectRequest putObjectRequest = new PutObjectRequest("chatting", folderName, new ByteArrayInputStream(new byte[0]), objectMetadata);

        try {
            s3Client.putObject(putObjectRequest);
            System.out.format("Folder %s has been created.\n", folderName);
        } catch (AmazonS3Exception e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }

        for (MultipartFile multipartFile : files) {
            if (multipartFile != null && !multipartFile.isEmpty()) {
                String originalFileName = multipartFile.getOriginalFilename();
                UUID uuid = UUID.randomUUID();
                String fileName = uuid.toString() + "_" + originalFileName;

                try (ByteArrayInputStream inputStream = new ByteArrayInputStream(multipartFile.getBytes())) {
                    PutObjectRequest result = new PutObjectRequest("chatting", folderName + fileName, inputStream, new ObjectMetadata());
                    s3Client.putObject(result);
                    System.out.println(ENDPOINT + "/chatting/" + folderName + fileName);
                    String imgSrc = "https://chatImage.cdn.ntruss.com/" + folderName + fileName;
                    resultSrc.add(imgSrc);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return resultSrc;
    }
	
	
	
	
}
