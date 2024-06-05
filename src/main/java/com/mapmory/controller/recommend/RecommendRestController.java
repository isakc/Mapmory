package com.mapmory.controller.recommend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mapmory.services.recommend.domain.Recommend;
import com.mapmory.services.recommend.service.RecommendService;

@RestController
@RequestMapping("/recommend/*")
public class RecommendRestController {
	
		@Autowired
		@Qualifier("recommendServiceImpl")
		private RecommendService recommendService;
	    
	    @PostMapping("/rest/test")
	    public String restTest() throws Exception {

	    	System.out.println("RecommendRestController /test run");
	    	
	    	int recordNo = 1;
	    	String userId = "user1";
	    	String recordText = "오늘 적당하네요";
	    	String recordTitle = "기록 제목1";
	    	Recommend recommend = recommendService.getRecordData(recordNo);
	    	
	    	
	    	
	    	//긍정도 값 받기
	    	int positive = recommendService.getPositive(recordText);
	    	recommend.setPositive(positive);
	    	System.out.println("positive : "+positive);
	    	
	    	recommend.setUserId(userId);
	    	recommend.setRecordTitle(recordTitle);
//	    	String category = recommendService.getRecordData();
//	    	recommend.setCategory(category);
//	    	System.out.println(category);
	    	
	    	
//	    	recommendService.updateDataset(); //주기학습 데이터 업로드 
	    	recommendService.saveDatasetToCSV(recommend,"aitems-8982956307867"); //추천 데이터 csv파일로 만들기
//	    	recommendService.getRecommendData(); //추천받기
	    	
//	    	System.out.println(recommend.toString());
	    	
	    	return null;
	    	
	    }
	    
	    @GetMapping("/modifyCsv")
	    public String modifyCsv(@RequestParam String bucketName, @RequestParam String fileName) throws Exception {
	        try {
	            recommendService.modifyCsvFile("aitems-8982956307867", "test.csv");
	            return "CSV file modified successfully!";
	        } catch (Exception e) {
	            e.printStackTrace();
	            return "Error modifying CSV file.";
	        }
	    }
	









}
