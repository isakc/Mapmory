package com.mapmory.controller.recommend;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mapmory.services.recommend.domain.Recommend;
import com.mapmory.services.recommend.service.RecommendService;
import com.mapmory.services.timeline.domain.Record;

@RestController
@RequestMapping("/recommend/*")
public class RecommendRestController {
	
		@Autowired
		@Qualifier("recommendServiceImpl")
		private RecommendService recommendService;
	    
	    @PostMapping("/rest/test")
	    public String restTest() throws Exception {

	    	System.out.println("RecommendRestController /test run");
	    	
//	    	Record record = new Record();
//	    	
//	    	int recordNo = 1;
//	    	String userId = "user1";
//	    	String recordText = "오늘 적당하네요";
//	    	String recordTitle = "기록 제목1";
//	    	Recommend recommend = recommendService.getRecordData(record);
	    	
	    	
	    	
	    	//긍정도 값 받기
//	    	int positive = recommendService.getPositive(recordText);
//	    	recommend.setPositive(positive);
//	    	System.out.println("positive : "+positive);
//	    	
//	    	recommend.setUserId(userId);
//	    	recommend.setRecordTitle(recordTitle);
//	    	String category = recommendService.getRecordData();
//	    	recommend.setCategory(category);
//	    	System.out.println(category);
//	    	
//	    	
//	    	recommendService.updateDataset(recommend); //주기학습 데이터 업로드 
//	    	recommendService.saveDatasetToCSV(recommend,"aitems-8982956307867"); //추천 데이터 csv파일로 만들기
//	    	ResponseEntity<String> recommendData = recommendService.getRecommendData("user3"); //추천받기
//	    	System.out.println("recommendData"+recommendData);
//	    	
//	    	System.out.println(recommend.toString());
	    	
	    	return null;
	    	
	    }
	    
	    
	    @GetMapping("/rest/getSearchRecommend")
	    public String[] getSearchRecommend() throws Exception{
	    	
	    	String[] result = recommendService.getSearchData("user1");
	    	System.out.println(result);
	    	
	    	return result;
	    }
	    
	    @GetMapping("/rest/recommend")
		public Map<String, Object> getRecommendPlace(@RequestParam String userId) throws Exception{
			
			List<String> values = recommendService.getRecommendData(userId);
			Map<String, Object> map = recommendService.getRecordList(values);
			System.out.println(map);
			
			
			return map;
		
		}
	    
	    









}
