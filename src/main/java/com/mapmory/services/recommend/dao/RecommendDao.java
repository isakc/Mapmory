package com.mapmory.services.recommend.dao;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RecommendDao {

	public void addSearchData() throws Exception;
	
	public void getSearchData() throws Exception;
	
}
