package com.mapmory.unit.recommend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import com.mapmory.services.recommend.dao.RecommendDao;

@SpringBootTest
public class RecommendDaoTest {
	
	@Autowired
	@Qualifier("recommendDao")
	private RecommendDao recommendDao;
	
	@Test
	public void addSearchData() throws Exception{
		
		String userId = "user1";
		String keyword = "음식";
		
		recommendDao.addSearchData(userId, keyword);
		
		keyword = "여행";
		recommendDao.addSearchData(userId, keyword);
	}
	
	@Test
	public void getCategory() throws Exception {
		int recordNo = 1;
		recommendDao.getCategory(recordNo);
		recommendDao.getCategory(0);
	}
	
	
	
	

}
