package com.mapmory.services.recommend.service;

import java.util.List;
import java.util.Map;

import com.mapmory.services.recommend.domain.Recommend;
import com.mapmory.services.timeline.domain.Record;

public interface RecommendService {

	public void addSearchData(Record record) throws Exception; // 기록하기  3
	
	public String[] getSearchData(String userId) throws Exception; //REST컨트롤러
	
//	public int getPositive(Map<String, String> requestPayload) throws Exception;
	public int getPositive(String recordText) throws Exception; // 기록하기   1
	
	public Recommend getRecordData(Record record,int recordNo) throws Exception; //기록하기  2
	
	public void saveDatasetToCSV(Recommend recommend, String bucketName) throws Exception; //기록하기  4
	
	public void updateDataset(Recommend recommend) throws Exception; // 기록하기  5
	
	public List<String> getRecommendData(String userId) throws Exception; // 컨트롤러
	
	public Map<String, Object> getRecordList(List<String> recordNo) throws Exception; // 컨트롤러
	

}