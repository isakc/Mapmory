package com.mapmory.services.recommend.service;

import java.util.Map;

import com.mapmory.services.recommend.domain.Recommend;
import com.mapmory.services.timeline.domain.Record;

public interface RecommendService {

	public void addSearchData() throws Exception;
	
	public void getSearchData() throws Exception;
	
//	public int getPositive(Map<String, String> requestPayload) throws Exception;
	public int getPositive(String recordText) throws Exception;
	
	public Recommend getRecordData(int recordNo) throws Exception;
	
	public void updateDataset() throws Exception;
	
	public void getRecommendData() throws Exception;
}
