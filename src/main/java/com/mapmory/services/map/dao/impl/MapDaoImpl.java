package com.mapmory.services.map.dao.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.mapmory.services.map.dao.MapDao;

@Repository
public class MapDaoImpl implements MapDao {
	
	///// Field /////
	@Value("${tmap.apiKey}")
	private String tmapAPIKey;
	
	///// Constructor /////
	
	///// Method /////

	@Override
	public String getRoute(Object searchRouter, String requestUrl) throws Exception {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("appKey", tmapAPIKey);
		
		String requestJson = new Gson().toJson(searchRouter);
		HttpEntity<String> requestEntity = new HttpEntity<>(requestJson, headers);
		String resultJson = restTemplate.postForObject(requestUrl, requestEntity, String.class);
		
		return resultJson;
		
	}// getRoute: 보행자, 자동차, 대중교통 경로 요청
}
