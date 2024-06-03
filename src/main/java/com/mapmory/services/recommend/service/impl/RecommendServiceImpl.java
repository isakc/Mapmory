package com.mapmory.services.recommend.service.impl;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.mapmory.services.recommend.dao.RecommendDao;
import com.mapmory.services.recommend.domain.Recommend;
import com.mapmory.services.recommend.service.RecommendService;
import com.mapmory.services.timeline.domain.Record;

@Service("recommendServiceImpl")
public class RecommendServiceImpl implements RecommendService {

	@Autowired
	private RecommendDao recommendDao;
	
	private final String clientId = "ls4tulf8dd";
    private final String clientSecret = "Smk4fHJycgdRjHgjffJG59UWd0A3vbV57SODGqIN";
    private final String apiUrl = "https://naveropenapi.apigw.ntruss.com/sentiment-analysis/v1/analyze";
    private final String aitemsUrl = "https://aitems.apigw.ntruss.com/api/v1/services/r0g5crs583y/datasets/92ivcomdz3w";
	
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
		
		String url = "https://aitems.apigw.ntruss.com/api/v1/services/r0g5crs583y/datasets/m6yz1ig2475";
		// TODO Auto-generated method stub
		String xncpapigwsignaturev2 = makeSignature();
		System.out.println(xncpapigwsignaturev2);
		
		//String timestamp 만들기
		long timestamplong = Instant.now().getEpochSecond();
		String timestamp = "";
		timestamp += timestamplong;
		
		System.out.println(timestamp);
		
		RestTemplate restTemplate = new RestTemplate();
    	
        HttpHeaders headers = new HttpHeaders();
        headers.add("accept", "application/json");
        headers.add("x-ncp-iam-access-key", "9A1E4C7B9C2973E90333");
        headers.add("x-ncp-apigw-signature-v2", xncpapigwsignaturev2);
        headers.add("x-ncp-apigw-timestamp", timestamp);
        
        Map<String,Object> content = new HashMap<String,Object>();
        
        content.put("type","item");
        
        Map<String,String>[] value = new LinkedHashMap[1];
        
        value[0] = new LinkedHashMap<String,String>();
        value[0].put("ITEM_ID", "201");
        value[0].put("TITLE", "테스트 기록 제목201");
        value[0].put("CATEGORY", "여행");
        
        
        System.out.println(value[0]);
        
        content.put("value", value);
        
        System.out.println(content);
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(content, headers);
        
        System.out.println(entity);
        
        String response = restTemplate.postForObject(url, entity, String.class);
        System.out.println(response);
	}

	@Override
	public void getRecommendData() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	//api 호출할 때 필요한 x-ncp-apigw-signature-v2 값 얻어오기 https://api.ncloud-docs.com/docs/ko/common-ncpapi
	public String makeSignature() throws IllegalStateException, UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException {
		
		long timestamplong = Instant.now().getEpochSecond();
		String timestamp = "";
		timestamp += timestamplong;
		System.out.println(timestamp);
		
		String space = " ";					// one space
		String newLine = "\n";					// new line
		String method = "POST";					// method
		String url = "/api/v1/services/r0g5crs583y/datasets/m6yz1ig2475";	// url (include query string)
//		String timestamp = "{timestamp}";			// current timestamp (epoch)
		String accessKey = "9A1E4C7B9C2973E90333";			// access key id (from portal or Sub Account)
		String secretKey = "C28BA9C4E17B6311B2F8CA1553CD7E21B0EF90D8";

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
		String encodeBase64String = Base64.getEncoder().encodeToString(rawHmac);

	  return encodeBase64String;
	}

	

}
