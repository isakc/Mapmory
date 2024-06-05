package com.mapmory.services.recommend.service.impl;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.mapmory.services.recommend.dao.RecommendDao;
import com.mapmory.services.recommend.domain.Recommend;
import com.mapmory.services.recommend.service.RecommendService;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;



@Service("recommendServiceImpl")
public class RecommendServiceImpl implements RecommendService {

	@Autowired
	private RecommendDao recommendDao;
	
	@Value("${clova.Client_ID}")
	private String clientId;
	
	@Value("${clova.Client_Secret}")
    private String clientSecret;
    
    @Value("${sentiment.url}")
    private String apiUrl;
    
    @Value("${aitems.url}")
    private String aitemsUrl; 
    
    @Value("${aitems.Access_Key}")
    private String aitems_Access_Key;
    
    @Value("${aitems.Secret_Key}")
    private String aitems_Secret_Key;
	
	@Override
	public void addSearchData() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getSearchData() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getPositive(String recordText) throws Exception {
	//public int getPositive(Map<String, String> requestPayload) throws Exception {

		RestTemplate restTemplate = new RestTemplate();
    	
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-NCP-APIGW-API-KEY-ID", clientId);
        headers.set("X-NCP-APIGW-API-KEY", clientSecret);
        
        
        Map<String,String> content = new HashMap<String,String>();
        
        content.put("content", recordText);
        
        System.out.println(content);
        System.out.println("================");
//        System.out.println(requestPayload);
        System.out.println("================");
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(content, headers);
        
        System.out.println("entity : "+entity);
        System.out.println(restTemplate.postForObject(apiUrl, entity, String.class).getClass());
        System.out.println(restTemplate.postForObject(apiUrl, entity, String.class));
        
        String text = restTemplate.postForObject(apiUrl, entity, String.class);
                
//        System.out.println(text.substring(text.indexOf(",\"positive")+12,text.indexOf(",\"neutral")));
        
        String positiveString = text.substring(text.indexOf(",\"positive")+12,text.indexOf(",\"neutral")).trim();
        double positiveDouble = Double.parseDouble(positiveString);
        int positive = (int) positiveDouble;

        return positive;
	}

	@Override
	public Recommend getRecordData(int recordNo) throws Exception {
		
		System.out.println("RecommendServiceImpl getRecordData()");

		Recommend recommend = new Recommend();
		
		//카테고리 이름받기
		recommend.setCategory(recommendDao.getCategoryName(recordNo));
		
		//해시태그 받아오기 시작
		List<String> hash = recommendDao.getHashTagNames(recordNo);
		System.out.println("hash : "+hash);
		String hashTags = "";
		for(String i : hash) {
			String hashTag = i.substring(1).trim();
			if( hashTags == "") {
				hashTags += hashTag;
			} else {
				hashTags += "|"+hashTag;
			}
		}
		recommend.setHashTag(hashTags);
		System.out.println("hashTags : " + hashTags);
		//해시태그 받아오기 끝
		
		//타임스탬프 epoch 시간으로 받아오기
		long timestamp = Instant.now().getEpochSecond();
		recommend.setTimeStamp(timestamp);
//		System.out.println(timestamp.getEpochSecond());
		
		
		System.out.println("RecommendServiceImpl getRecordData end");
		return recommend;
		
		
	}
	
	@Override
	public void updateDataset() throws Exception {
		
		String uri= "/api/v1/services/8j0xi1tr7n1/datasets/m6yz1ig2475";
//		String url = "https://aitems.apigw.ntruss.com/api/v1/services/{r0g5crs583y}/datasets/{m6yz1ig2475}";
		String url = "https://aitems.apigw.ntruss.com"+uri;
		String timestamp = String.valueOf(System.currentTimeMillis());
		String signatureKey = makeSignature(timestamp, uri,"POST");
		System.out.println(timestamp);
		System.out.println("signatureKey : "+signatureKey);
		
		
	
		RestTemplate restTemplate = new RestTemplate();	    
        HttpHeaders headers = new HttpHeaders();
        headers.add("accept", "application/json");
        headers.add("x-ncp-iam-access-key", aitems_Access_Key);
        headers.add("x-ncp-apigw-signature-v2", signatureKey);
        headers.add("x-ncp-apigw-timestamp", timestamp);
        
//        JSONObject content = new JSONObject();
//        content.put("type","item");
//        
//        JSONArray value = new JSONArray();
//        JSONObject item = new JSONObject();
//        item.put("ITEM_ID", "201");
//        item.put("TITLE", "테스트 기록 제목201");
//        item.put("CATEGORY", "여행");
//        
//        value.put(item);
//        content.put("value", value);
//        
//        System.out.println(content);
        
        JSONObject content = new JSONObject();
        content.put("type", "item"); // "user", "item", "interaction" 중 하나

        JSONArray valueArray = new JSONArray();
        JSONObject item1 = new JSONObject();
        item1.put("ITEM_ID", "201");
        item1.put("TITLE", "테스트 기록 제목201");
        item1.put("CATEGORY", "여행");

        JSONObject item2 = new JSONObject();
        item2.put("ITEM_ID", "202");
        item2.put("TITLE", "테스트 기록 제목202");
        item2.put("CATEGORY", "여행");

        JSONObject item3 = new JSONObject();
        item3.put("ITEM_ID", "203");
        item3.put("TITLE", "테스트 기록 제목203");
        item3.put("CATEGORY", "여행");

        valueArray.add(item1);
        valueArray.add(item2);
        valueArray.add(item3);

        content.put("value", valueArray);
        


        System.out.println(content);
        
        HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(content, headers);
        
        System.out.println("entity : "+entity);
        
//        String response = restTemplate.postForObject(url, entity, String.class);
//        System.out.println(response);
        
        try {
            String response = restTemplate.postForObject(url, entity, String.class);
            System.out.println(response+"complete");
        } catch (HttpClientErrorException e) {
            System.out.println("Error: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
        }
        
	}

	@Override
	public void getRecommendData() throws Exception {
		String uri= "/api/v1/services/r0g5crs583y/infers/lookup?type=personalRecommend&targetId=user1";
//		String url = "https://aitems.apigw.ntruss.com/api/v1/services/{r0g5crs583y}/datasets/{m6yz1ig2475}";
		String url = "https://aitems.apigw.ntruss.com"+uri;
		String timestamp = String.valueOf(System.currentTimeMillis());
		String signatureKey = makeSignature(timestamp, uri, "GET");
		System.out.println(timestamp);
		System.out.println("signatureKey : "+signatureKey);
		
		
	
		RestTemplate restTemplate = new RestTemplate();	    
        HttpHeaders headers = new HttpHeaders();
        headers.add("accept", "application/json");
        headers.add("x-ncp-iam-access-key", aitems_Access_Key);
        headers.add("x-ncp-apigw-signature-v2", signatureKey);
        headers.add("x-ncp-apigw-timestamp", timestamp);
        
        HttpEntity<Object> entity = new HttpEntity<Object>(headers);
        
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		System.out.println(response);
	}
	
	

	@Override
	public void saveDatasetToCSV(Recommend recommend, String bucketName) throws Exception {
		
//      Recommend(userId=user1, recordNo=0, recordTitle=기록 제목1, category=음식, hashTag=맛집|나가|중국집|3000원|굿, positive=96, timeStamp=1717551122)
		String userId = recommend.getUserId();
		String recordNo = String.valueOf(recommend.getRecordNo());
		String recordTitle = recommend.getRecordTitle();
		String category = recommend.getCategory();
		String hashTag = recommend.getHashTag();
		String positive = String.valueOf(recommend.getPositive());
		String timeStamp = String.valueOf(recommend.getTimeStamp());
		
		
		BasicAWSCredentials awsCreds = new BasicAWSCredentials(aitems_Access_Key, aitems_Secret_Key);
        
		String REGION = "kr-standard";
	    String ENDPOINT = "https://kr.object.ncloudstorage.com";
		
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new EndpointConfiguration(ENDPOINT, REGION))
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withPathStyleAccessEnabled(true)
                .build();
        
        //user.csv 
        //CSV 파일 다운로드
        S3Object s3Object = s3Client.getObject(bucketName, "dataset/user.csv");
        InputStream inputStream = s3Object.getObjectContent();
        CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream));
        
        //CSV 파일 읽어오기
        List<String[]> allData = csvReader.readAll();
        csvReader.close();
        
        String[] userData = { userId , recordNo, positive, timeStamp};
        
        allData.add(userData);
        
        // Step 4: 수정된 CSV 파일을 로컬에 저장
        String tempFileName = "temp_" + "user.csv";
        FileWriter outputFile = new FileWriter(tempFileName);
        CSVWriter writer = new CSVWriter(outputFile);
        writer.writeAll(allData);
        writer.close();

        // Step 5: 수정된 CSV 파일을 Object Storage에 업로드
        s3Client.putObject(bucketName, "dataset/user.csv", new File(tempFileName));

        // Step 6: 로컬 임시 파일 삭제
        new File(tempFileName).delete();
        System.out.println("user.csv 수정 완료");
        
        //item.csv 
        //CSV 파일 다운로드
        s3Object = s3Client.getObject(bucketName, "dataset/item.csv");
        inputStream = s3Object.getObjectContent();
        csvReader = new CSVReader(new InputStreamReader(inputStream));
        
        //CSV 파일 읽어오기
        allData = csvReader.readAll();
        csvReader.close();
        
        String[] itemData = { recordNo, recordTitle, category};
        
        allData.add(itemData);
        
        // 수정된 CSV 파일을 로컬에 저장
        tempFileName = "temp_" + "item.csv";
        outputFile = new FileWriter(tempFileName);
        writer = new CSVWriter(outputFile);
        writer.writeAll(allData);
        writer.close();

        // 수정된 CSV 파일을 Object Storage에 업로드
        s3Client.putObject(bucketName, "dataset/item.csv", new File(tempFileName));

        // 로컬 임시 파일 삭제
        new File(tempFileName).delete();
        System.out.println("itme.csv 수정 완료");
        
        //interaction.csv 
        //CSV 파일 다운로드
        s3Object = s3Client.getObject(bucketName, "dataset/interaction.csv");
        inputStream = s3Object.getObjectContent();
        csvReader = new CSVReader(new InputStreamReader(inputStream));
        
        //CSV 파일 읽어오기
        allData = csvReader.readAll();
        csvReader.close();
        
        String[] interactionData = { userId, recordNo, hashTag, timeStamp};
        
        allData.add(interactionData);
        
        //수정된 CSV 파일을 로컬에 저장
        tempFileName = "temp_" + "interaction.csv";
        outputFile = new FileWriter(tempFileName);
        writer = new CSVWriter(outputFile);
        writer.writeAll(allData);
        writer.close();

        //수정된 CSV 파일을 Object Storage에 업로드
        s3Client.putObject(bucketName, "dataset/interaction.csv", new File(tempFileName));

        //로컬 임시 파일 삭제
        new File(tempFileName).delete();
        System.out.println("interaction.csv 수정 완료");
	}
	
	
	public void modifyCsvFile(String bucketName, String fileName) throws Exception {
		
		BasicAWSCredentials awsCreds = new BasicAWSCredentials(aitems_Access_Key, aitems_Secret_Key);
        
		String REGION = "kr-standard";
	    String ENDPOINT = "https://kr.object.ncloudstorage.com";
		
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new EndpointConfiguration(ENDPOINT, REGION))
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withPathStyleAccessEnabled(true)
                .build();

        // Step 1: CSV 파일 다운로드
        S3Object s3Object = s3Client.getObject(bucketName, fileName);
        InputStream inputStream = s3Object.getObjectContent();
        CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream));

        // Step 2: CSV 파일 읽기
        List<String[]> allData = csvReader.readAll();
        csvReader.close();

        // Step 3: CSV 파일 수정 (예: 첫 번째 행 수정)
        allData.get(1)[2] = "Updated Value"; // 예시로 두 번째 열 값을 업데이트

        // Step 4: 수정된 CSV 파일을 로컬에 저장
        String tempFileName = "temp_" + fileName;
        FileWriter outputFile = new FileWriter(tempFileName);
        CSVWriter writer = new CSVWriter(outputFile);
        writer.writeAll(allData);
        writer.close();

        // Step 5: 수정된 CSV 파일을 Object Storage에 업로드
        s3Client.putObject(bucketName, fileName, new File(tempFileName));

        // Step 6: 로컬 임시 파일 삭제
        new File(tempFileName).delete();
    }
	
	
	
	
	
	
	
	
	//api 호출할 때 필요한 x-ncp-apigw-signature-v2 값 얻어오기 https://api.ncloud-docs.com/docs/ko/common-ncpapi
		public String makeSignature(String timestamp, String url, String method) throws Exception {
			
			
		
			String space = " ";					// one space
			String newLine = "\n";					// new line
//			String method = "GET";					// method
//			String url = "/api/v1/services/r0g5crs583y/datasets/m6yz1ig2475";	// url (include query string)
//			String timestamp = time;			// current timestamp (epoch)
			String accessKey = aitems_Access_Key;			// access key id (from portal or Sub Account)
			String secretKey = aitems_Secret_Key;
			
			System.out.println(url);
			System.out.println(accessKey);
			System.out.println(secretKey);
			System.out.println(timestamp);

			String message = new StringBuilder()
				.append(method)
				.append(space)
				.append(url)
				.append(newLine)
				.append(timestamp)
				.append(newLine)
				.append(accessKey)
				.toString();
			
			SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(signingKey);

			byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
			String encodeBase64String = Base64.encodeBase64String(rawHmac);

		  return encodeBase64String;

		
		}
}

	


