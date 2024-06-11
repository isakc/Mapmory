package com.mapmory.services.recommend.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mapmory.services.timeline.domain.Record;

@Mapper
public interface RecommendDao {

	public void addSearchData(String userId, String keyword) throws Exception;
	
	public List<String> getSearchData(String userId) throws Exception;
	
	public String getCategory(int categoryNo) throws Exception;
	
	public String getCategoryName(int recordNo) throws Exception;
	
	public List<String> getHashTagNames(int recordNo) throws Exception;
	
	public List<HashMap<String,Record>> getRecordList(List<String> recordNo) throws Exception;
	
}
