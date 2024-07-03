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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.mapmory.services.recommend.dao.RecommendDao;
import com.mapmory.services.recommend.domain.Recommend;
import com.mapmory.services.recommend.dto.RecommendPlaceDTO;
import com.mapmory.services.recommend.service.RecommendService;
import com.mapmory.services.timeline.dao.TimelineDao;
import com.mapmory.services.timeline.domain.ImageTag;
import com.mapmory.services.timeline.domain.Record;
import com.mapmory.services.timeline.domain.Record2;
import com.mapmory.services.timeline.service.TimelineService;
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
    
    @Value("${kakaomap.rest.apiKey}")
    private String kakaoMapRestKey;
    
	@Override
	public void addSearchData(Record record) throws Exception {

		
		String userId = record.getRecordUserId();
		String category = recommendDao.getCategory(record.getCategoryNo());
		List<ImageTag> hashTag = record.getHashtag();
		
		System.out.println("userId : "+userId+", category : "+category+", hashTag : "+hashTag);
		
		for(ImageTag i : hashTag) {
			recommendDao.addSearchData(userId, i.getImageTagText());
		}
		
		if(category != null) {
			recommendDao.addSearchData(userId, category);
		}
		

//		
//		String userId = record.getRecordUserId();
//		String category = recommendDao.getCategory(record.getCategoryNo());
//		List<String> hashTag = record.getHashtag();
//		
//		System.out.println("userId : "+userId+", category : "+category+", hashTag : "+hashTag);
//		
//		if(category != null) {
//			recommendDao.addSearchData(userId, category);
//		}
//		

//		if(hashTag != null) {
//			for(String i : hashTag) {
//				recommendDao.addSearchData(userId, i);
//			}
//		}
		
	}

	@Override
	public String[] getSearchData(String userId) throws Exception {
		List<String> data = recommendDao.getSearchData(userId);		
		Random rand = new Random();
		int dataLength = data.size();
		int[] randInt = new int[5];
		String[] result;
		
		System.out.println("data"+data);
		System.out.println(dataLength);
		
		if(dataLength != 0 ) {
			if(dataLength > 5) {
				System.out.println("6개 이상");
				result = new String[5];
				for(int k = 0; k < 5; k++) {
					randInt[k] = rand.nextInt(dataLength);
					for(int i = 0; i < 5; i++) {
						if(randInt[k] == randInt[i] && k!=i && k !=0) {
							randInt[k] = rand.nextInt(dataLength);
							k--;
							continue;
						}					
					}
				}
				
				for(int j = 0; j < 5; j++) {
					result[j] = data.get(randInt[j]);
				}
				
			} else {
				System.out.println("5개 이하");
				result = new String[dataLength];
				int k = 0;
				for(String i : data) {
					result[k] = i;
					System.out.println("result["+k+"] : " + result[k]);
					k++;
				}
				System.out.println(result);
				
			}
			
			for(String rs : result) {
				System.out.println("rs :"+rs);
			}
			return result;	
		}else {
			result = new String[1];
			result[0] = "아직 추천해 드릴 수 있는 검색어가 존재하지 않아요!";
			return result;
		}		
		
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
//        System.out.println("================");
//        System.out.println(requestPayload);
//        System.out.println("================");
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
	public Recommend getRecordData(Record record, int recordNo) throws Exception {
		System.out.println("RecommendServiceImpl getRecordData()");
		Recommend recommend = new Recommend();
		
		int categoryNo = record.getCategoryNo();
		
		//해시태그 추천시스템에 맞게 저장
		List<ImageTag> hash = record.getHashtag();
		if(hash != null) {
			System.out.println("hash : "+hash);
			String hashTags = "";
			for(ImageTag i : hash) {
				String hashTag = i.getImageTagText().substring(1).trim();
				if( hashTags == "") {
					hashTags += hashTag;
				} else {
					hashTags += "|"+hashTag;
				}
				
			}
			recommend.setHashTag(hashTags);
			System.out.println("hashTags : " + hashTags);
		}		
		
		//카테고리 이름저장
		recommend.setCategory(recommendDao.getCategory(categoryNo));
		
		//타임스탬프 epoch 시간으로 받아오기
		long timestamp = Instant.now().getEpochSecond();
		recommend.setTimeStamp(timestamp);
//		System.out.println(timestamp.getEpochSecond());
		
		recommend.setUserId(record.getRecordUserId());
		recommend.setRecordNo(recordNo);
		recommend.setRecordTitle(record.getRecordTitle());
		
		System.out.println(recommend.toString());
		System.out.println("RecommendServiceImpl getRecordData end");
		return recommend;



		
		
	}
	
	
	@Override
	public void updateDataset(Recommend recommend) throws Exception {
		
		String userId = recommend.getUserId();
		String recordNo = String.valueOf(recommend.getRecordNo());
		String recordTitle = recommend.getRecordTitle();
		String category = recommend.getCategory();
		String hashTag = recommend.getHashTag();
		String positive = String.valueOf(recommend.getPositive());
		String timeStamp = String.valueOf(recommend.getTimeStamp());
		
		/*
		 * String userDataSetId = "/api/v1/services/siq3vlubqhb/datasets/i2u8qte5hn6";
		 * String itemDataSetId = "/api/v1/services/siq3vlubqhb/datasets/m6yz1ig2475";
		 * String interactionDataSetId =
		 * "/api/v1/services/siq3vlubqhb/datasets/92ivcomdz3w";
		 */
		String userDataSetId =  "/api/v1/services/qz98xfu6n8y/datasets/i2u8qte5hn6";
		String itemDataSetId = "/api/v1/services/qz98xfu6n8y/datasets/m6yz1ig2475";
		String interactionDataSetId = "/api/v1/services/qz98xfu6n8y/datasets/92ivcomdz3w";
		String timestamp = String.valueOf(System.currentTimeMillis());
		System.out.println(timestamp);
//		String uri= "/api/v1/services/siq3vlubqhb/datasets/m6yz1ig2475";
		
//		String url = "https://aitems.apigw.ntruss.com"+uri;
		
		// 공통으로 사용하는 부분 /////////////
		RestTemplate restTemplate = new RestTemplate();	    
        HttpHeaders headers = new HttpHeaders();
        headers.add("accept", "application/json");
        headers.add("x-ncp-iam-access-key", aitems_Access_Key);     
        headers.add("x-ncp-apigw-signature-v2", "signatureKey");
        headers.add("x-ncp-apigw-timestamp", timestamp);
		
		///////////////////////////////
		
        ///////user 데이터 json object로 만들기 /////////
        JSONObject contentUser = new JSONObject();
        contentUser.put("type", "user");
        JSONArray valueUser = new JSONArray();
        JSONObject user = new JSONObject();
        user.put("USER_ID", userId);
        user.put("ITEM_ID", recordNo);
        user.put("POSITIVE", positive);
        user.put("TIMESTAMP", timeStamp);
        
        valueUser.add(user);
        contentUser.put("value", valueUser);
        System.out.println(contentUser);
		
        ///////item 데이터 json object로 만들기 /////////
        JSONObject contentItem = new JSONObject();
        contentItem.put("type", "item"); // "user", "item", "interaction" 중 하나
        JSONArray valueItem = new JSONArray();
        JSONObject item = new JSONObject();
        item.put("ITEM_ID", recordNo);
        item.put("TITLE", recordTitle);
        item.put("CATEGORY", category);
        
        valueItem.add(item);
        contentItem.put("value", valueItem);
        System.out.println(contentItem);      
        
        ///////interaction 데이터 json object로 만들기///////
        JSONObject contentInteraction = new JSONObject();
        contentInteraction.put("type", "interaction");
        JSONArray valueInteraction = new JSONArray();
        JSONObject interaction = new JSONObject();
        interaction.put("USER_ID", userId);
        interaction.put("ITEM_ID", recordNo);
        interaction.put("HASHTAG", hashTag);
        interaction.put("TIMESTAMP", timeStamp);
        
        valueInteraction.add(interaction);
        contentInteraction.put("value", valueInteraction);
        System.out.println(contentInteraction);
        
        // 공통 아닌 부분 /////////////// user post
        String url = "https://aitems.apigw.ntruss.com"+userDataSetId;
		String signatureKey = makeSignature(timestamp, userDataSetId,"POST");		
		System.out.println("signatureKey : "+signatureKey);
		headers.set("x-ncp-apigw-signature-v2", signatureKey);

        HttpEntity<JSONObject> userEntity = new HttpEntity<JSONObject>(contentUser, headers);
        System.out.println("entity : "+userEntity);
        try {
        	System.out.println(url);
        	System.out.println(headers);
            String response = restTemplate.postForObject(url, userEntity, String.class);
            System.out.println(response+" : user");
        } catch (HttpClientErrorException e) {
            System.out.println("Error: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
        }
		/////////////////////////////
        
        // 공통 아닌 부분 /////////////// item post
        url = "https://aitems.apigw.ntruss.com"+itemDataSetId;
		signatureKey = makeSignature(timestamp, itemDataSetId,"POST");		
		System.out.println("signatureKey : "+signatureKey);
		headers.set("x-ncp-apigw-signature-v2", signatureKey);

        HttpEntity<JSONObject> itemEntity = new HttpEntity<JSONObject>(contentItem, headers);
        System.out.println("entity : "+itemEntity);
        try {
        	System.out.println(url);
            String response = restTemplate.postForObject(url, itemEntity, String.class);
            System.out.println(response+" : item");
        } catch (HttpClientErrorException e) {
            System.out.println("Error: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
        }
		/////////////////////////////
		
        // 공통 아닌 부분 /////////////// interaction post
        url = "https://aitems.apigw.ntruss.com"+interactionDataSetId;
		signatureKey = makeSignature(timestamp, interactionDataSetId,"POST");		
		System.out.println("signatureKey : "+signatureKey);
		headers.set("x-ncp-apigw-signature-v2", signatureKey);

        HttpEntity<JSONObject> interactionEntity = new HttpEntity<JSONObject>(contentInteraction, headers);
        System.out.println("entity : "+interactionEntity);
        try {
        	System.out.println(url);
            String response = restTemplate.postForObject(url, interactionEntity, String.class);
            System.out.println(response+" : interaction");
        } catch (HttpClientErrorException e) {
            System.out.println("Error: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
        }
		/////////////////////////////
        
        
//        String response = restTemplate.postForObject(url, entity, String.class);
//        System.out.println(response);
        
        
        
	}

	//  추천데이터 받아오기
	@Override
	public List<String> getRecommendData(String userId) throws Exception {
		String uri= "/api/v1/services/qz98xfu6n8y/infers/lookup?type=personalRecommend&targetId="+userId;
//		String uri= "/api/v1/services/siq3vlubqhb/infers/lookup?type=personalRecommend&targetId="+userId;
//		String url = "https://aitems.apigw.ntruss.com/api/v1/services/{r0g5crs583y}/datasets/{m6yz1ig2475}";
		String url = "https://aitems.apigw.ntruss.com"+uri;
		String timestamp = String.valueOf(System.currentTimeMillis());
		String signatureKey = makeSignature(timestamp, uri, "GET");
		System.out.println(timestamp);
		System.out.println("signatureKey : "+signatureKey);
		System.out.println(url);
		
		
	
		RestTemplate restTemplate = new RestTemplate();	    
        HttpHeaders headers = new HttpHeaders();
        headers.add("accept", "application/json");
        headers.add("x-ncp-iam-access-key", aitems_Access_Key);
        headers.add("x-ncp-apigw-signature-v2", signatureKey);
        headers.add("x-ncp-apigw-timestamp", timestamp);
        
        HttpEntity<Object> entity = new HttpEntity<Object>(headers);
        
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		System.out.println(response);
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		String stringResult = response.getBody();
		
		JsonNode rootNode = objectMapper.readTree(stringResult);
		JsonNode valuesNode = rootNode.path("values");
		List<String> values = objectMapper.convertValue(valuesNode, List.class);
		
		for(String i : values) {
			System.out.println("values : " +i);
		}
		
		return values;
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
	
	

	
	
	
	
	
	
	
	//api 호출할 때 필요한 x-ncp-apigw-signature-v2 값 얻어오기 https://api.ncloud-docs.com/docs/ko/common-ncpapi
		public String makeSignature(String timestamp, String url, String method) throws Exception {
			
			
		
			String space = " ";					// one space
			String newLine = "\n";					// new line
//			String method = "GET";					// method
//			String url = "/api/v1/services/siq3vlubqhb/datasets/"+datasets;	// url (include query string)
//			String timestamp = time;			// current timestamp (epoch)
			String accessKey = aitems_Access_Key;			// access key id (from portal or Sub Account)
			String secretKey = aitems_Secret_Key;
			System.out.println("===============makeSignature=============");
			System.out.println("timestamp : "+timestamp+", url : "+url+", method : "+method);
			System.out.println(url);
			System.out.println(accessKey);
			System.out.println(secretKey);
			System.out.println(timestamp);
			System.out.println("=========================================");

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

//		@Override
//		public Map<String, Object> getRecordList(List<String> recordNo) throws Exception {
//
//			for(String i : recordNo) {
//				System.out.println(i);
//			}
//			
//			List<HashMap<String,Record>> list = recommendDao.getRecordList(recordNo);
//			System.out.println("++++++++++getRecordListOver++++++++++++++");
//			System.out.println("getRecordList: "+list);
//			Map<String, Object> map = new HashMap<String, Object>();
//			map.put("record",list);
//			
//			return map;
//		}
		
		@Override
		public List<RecommendPlaceDTO> getRecordList(List<String> recordNo) throws Exception {

			List<Record> recordList = recommendDao.getRecordList(recordNo);
			List<RecommendPlaceDTO> recommendPlaceList = new ArrayList<RecommendPlaceDTO>();	
			String[] categoryCodeGroup = {"FD6", "CE7", "AD5", "AT4", "CT1", "SC4"}; //음식점, 카페, 숙박, 관광명소, 문화시설, 학교
			
			Collections.shuffle(recordList); // 랜덤으로 섞음
			
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", "KakaoAK "+kakaoMapRestKey);
	        HttpEntity<Object> entity = new HttpEntity<Object>(headers);
	        
			StringBuilder url = new StringBuilder();
			ObjectMapper objectMapper = new ObjectMapper();
		    JsonNode rootNode;
		    int totalCount;
		    String resultJson;
		    JsonNode documents;
		    
		    outerLoop:
			for (Record record : recordList) {
				url.setLength(0);
				
				url.append("https://dapi.kakao.com/v2/local/search/keyword?")
				.append("query=").append(record.getCheckpointAddress())
				//.append("&page=").append( (int) (Math.random() * 3) + 1 ) //3페이지 중 랜덤
				.append("&category_group_code=");
				
				for(int i=0 ; i<categoryCodeGroup.length; i++) {
					url.append(categoryCodeGroup[i]);
					
					if(i != categoryCodeGroup.length -1) {
						url.append(",");
					}
				}
				
				resultJson = restTemplate.exchange(url.toString(), HttpMethod.GET, entity, String.class).getBody(); // REST
				rootNode = objectMapper.readTree(resultJson);
				totalCount = rootNode.path("meta").path("total_count").asInt();
				
				if(totalCount != 0) {
				    documents = rootNode.path("documents");
				    JsonNode item = documents.get(0); //1페이지 첫번째꺼 가져오기
				    //JsonNode item = documents.get((int) (Math.random() * documents.size() )); //그 페이지 중 랜덤 가져오기
				    
					RecommendPlaceDTO recommendPlaceDTO = RecommendPlaceDTO.builder()
							.placeName(item.path("place_name").asText())
							.distance(item.path("distance").asText())
							.placeUrl(item.path("place_url").asText())
							.categoryName(item.path("category_name").asText())
							.addressName(item.path("address_name").asText())
							.phone(item.path("phone").asText())
							.latitude(item.path("y").asDouble())
							.longitude(item.path("x").asDouble())
							.markerType(3)
							.build();

					recommendPlaceList.add(recommendPlaceDTO);

					if (recommendPlaceList.size() == 10) {
						break outerLoop;
					}
				    
				}//만약 데이터가 있으면 추가
			} // recordList만큼 반복
			
			return recommendPlaceList;
		}// getRecordList: 추천 장소 리스트 얻음 
}

	


