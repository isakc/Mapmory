package com.mapmory.services.recommend.service;

import java.util.Map;

public interface RecommendService {

	public void addSearchData() throws Exception;
	
	public void getSearchData() throws Exception;
	
	public int getPositive(Map<String, String> requestPayload) throws Exception;
}
