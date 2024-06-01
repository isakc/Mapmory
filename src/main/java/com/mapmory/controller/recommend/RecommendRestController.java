package com.mapmory.controller.recommend;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/recommend/*")
public class RecommendRestController {
	
//	 @Autowired
//	    private RestTemplate restTemplate;

	    private final String clientId = "ls4tulf8dd";
	    private final String clientSecret = "Smk4fHJycgdRjHgjffJG59UWd0A3vbV57SODGqIN";
	    private final String apiUrl = "https://naveropenapi.apigw.ntruss.com/sentiment-analysis/v1/analyze";

	    @PostMapping("/analyze-sentiment")
	    public String analyzeSentiment(@RequestBody Map<String, String> requestPayload) {
	    	
	    	RestTemplate restTemplate = new RestTemplate();
	    	
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        headers.set("X-NCP-APIGW-API-KEY-ID", clientId);
	        headers.set("X-NCP-APIGW-API-KEY", clientSecret);

	        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestPayload, headers);
	        
	        System.out.println(entity);
	        System.out.println(restTemplate.postForObject(apiUrl, entity, String.class).getClass());
	        System.out.println(restTemplate.postForObject(apiUrl, entity, String.class));

	        return restTemplate.postForObject(apiUrl, entity, String.class);
	    }
	    
	    @PostMapping("/test")
	    public String test(@RequestBody Map<String, String> requestPayload) {
	    	
	    	RestTemplate restTemplate = new RestTemplate();
	    	
	    	HttpHeaders headers =new HttpHeaders();
	    	headers.setContentType(MediaType.APPLICATION_JSON);
	    	
	    	HttpEntity<Map<String,String>> entity = new HttpEntity<>(requestPayload, headers);
	    	
	    	System.out.println(entity);
	    	
	    	return restTemplate.postForObject("http://192.168.0.45:8001/recommend/analyze-sentiment", entity, String.class);
	    	
	    }

}
