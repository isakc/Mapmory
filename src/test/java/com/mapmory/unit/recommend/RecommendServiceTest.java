package com.mapmory.unit.recommend;


import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mapmory.services.recommend.domain.Recommend;
import com.mapmory.services.recommend.service.impl.RecommendServiceImpl;
import com.mapmory.services.timeline.domain.ImageTag;
import com.mapmory.services.timeline.domain.Record;

@SpringBootTest
public class RecommendServiceTest {

	@Autowired
	@Qualifier("recommendServiceImpl")
	RecommendServiceImpl recommendServiceImpl;
	
	@Test
	public void addSearchData() throws Exception{
		

		
		System.out.println("\n===================================");
		List<ImageTag> image= new ArrayList<ImageTag>();
		image.add(ImageTag.builder().imageTagText("A.png").build());
		image.add(ImageTag.builder().imageTagText("B.jpg").build());
		List<ImageTag> hash= new ArrayList<ImageTag>();
		hash.add(ImageTag.builder().imageTagText("#test").build());
		hash.add(ImageTag.builder().imageTagText("#test2").build());
		System.out.println("image : "+image);
		System.out.println("hash : "+hash);
		Record record = Record.builder()
				.recordUserId("user1")
				.recordTitle("test 기록")
				.latitude(37.1232322)
				.longitude(127.1233322)
				.checkpointAddress("중구남방")
				.mediaName("가.mp4")
				.imageName(image)
				.hashtag(hash)
				.categoryNo(1)
				.recordText("기분이 나쁘진 않네요")
				.tempType(0)
				.updateCount(0)
				.d_DayDate(Date.valueOf("2024-06-02"))
				.timecapsuleType(0)
				.build();
		
		recommendServiceImpl.addSearchData(record);
		Recommend recommend = recommendServiceImpl.getRecordData(record, record.getRecordNo());
		recommend.setPositive(recommendServiceImpl.getPositive(record.getRecordText()));
		recommendServiceImpl.updateDataset(recommend);
		recommendServiceImpl.saveDatasetToCSV(recommend, "aitems-8982956307867");
		
		System.out.println(recommend.toString());

		
	}
	
	
//	@Test
	public void getRecommendTest() throws Exception {
//		ObjectMapper objectMapper = new ObjectMapper();
//		String[] result = recommendServiceImpl.getRecommendData("user1");
//		
//		for(String i : result) {
//			System.out.println(i);
//		}
	}
	
//	@Test
	public void getRecordList() throws Exception{
		
		List<String> result = recommendServiceImpl.getRecommendData("user1");
		System.out.println("==========getRecommendDataOver==========");
		System.out.println("result: "+result);
		Map<String, Object> map = recommendServiceImpl.getRecordList(result);	
		System.out.println(map);
	}
		
//	@Test
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
