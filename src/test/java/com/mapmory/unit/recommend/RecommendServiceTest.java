package com.mapmory.unit.recommend;


import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.mapmory.services.recommend.domain.Recommend;
import com.mapmory.services.recommend.service.impl.RecommendServiceImpl;
import com.mapmory.services.timeline.domain.Record;

@SpringBootTest
public class RecommendServiceTest {

	@Autowired
	@Qualifier("recommendServiceImpl")
	RecommendServiceImpl recommendServiceImpl;
	
//	@Test
	public void addSearchData() throws Exception{
		
//		
//		List<Map<String,Object>> imageTag = new ArrayList<Map<String,Object>>();
//		Map<String, Object> map =  new HashMap<String,Object>();
//		map.put("imageTagType", 0);
//		map.put("imageTagText", "#멋진풍경");
//		
//		Map<String, Object> map2 =  new HashMap<String,Object>();
//		map2.put("imageTagType", 0);
//		map2.put("imageTagText", "#인생샷");
//		
//		imageTag.add(map);
//		imageTag.add(map2);
//
//		Record record = Record.builder()
//				.recordUserId("user3")
//				.imageTag(imageTag)
//				.categoryNo(4)
//				.build();
//		
//		recommendServiceImpl.addSearchData(record);
//		
		
	}
	
	//@Test
	public void getSearchData() throws Exception {
		
		String userId = "user1";
		String[] data = recommendServiceImpl.getSearchData(userId);
		
		userId = null;
		recommendServiceImpl.getSearchData(userId);
	}
	
	@Test
	public void getRecordData() throws Exception{
//		List<Map<String,Object>> imageTag = new ArrayList<Map<String,Object>>();
//		Map<String, Object> map =  new HashMap<String,Object>();
//		map.put("imageTagType", 0);
//		map.put("imageTagText", "#멋진풍경");
//		
//		Map<String, Object> map2 =  new HashMap<String,Object>();
//		map2.put("imageTagType", 0);
//		map2.put("imageTagText", "#인생샷");
//		
//		imageTag.add(map);
//		imageTag.add(map2);
//
//		int recordNo = 1;
//		
//		Record record = Record.builder()
//				.recordUserId("user1")
//				.imageTag(imageTag)
//				.categoryNo(4)
//				.recordTitle("테스트 타이틀")
//				.build();
//		
//		recommendServiceImpl.getRecordData(record, recordNo);
	}
	
}
