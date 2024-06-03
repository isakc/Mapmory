package com.mapmory.services.recommend.service.impl;

import java.util.Map;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.mapmory.services.recommend.service.RecommendService;

@Service("recommendServiceImpl")
public class RecommendServiceImpl implements RecommendService {

	
	private final String clientId = "ls4tulf8dd";
    private final String clientSecret = "Smk4fHJycgdRjHgjffJG59UWd0A3vbV57SODGqIN";
    private final String apiUrl = "https://naveropenapi.apigw.ntruss.com/sentiment-analysis/v1/analyze";
	
	@Override
	public void addSearchData() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getSearchData() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getPositive(Map<String, String> requestPayload) throws Exception {
		// TODO Auto-generated method stub
		RestTemplate restTemplate = new RestTemplate();
    	
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-NCP-APIGW-API-KEY-ID", clientId);
        headers.set("X-NCP-APIGW-API-KEY", clientSecret);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestPayload, headers);
        
//        System.out.println(entity);
//        System.out.println(restTemplate.postForObject(apiUrl, entity, String.class).getClass());
//        System.out.println(restTemplate.postForObject(apiUrl, entity, String.class));
        
        String text = restTemplate.postForObject(apiUrl, entity, String.class);
                
//        System.out.println(text.substring(text.indexOf(",\"positive")+12,text.indexOf(",\"neutral")));
        
        String positiveString = text.substring(text.indexOf(",\"positive")+12,text.indexOf(",\"neutral")).trim();
        double positiveDouble = Double.parseDouble(positiveString);
        int positive = (int) positiveDouble;

        return positive;
	}

}
