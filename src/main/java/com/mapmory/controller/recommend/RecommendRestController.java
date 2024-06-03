package com.mapmory.controller.recommend;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

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
	    	
	    	
	    	
//	    	int positive = recommendService.getPositive(requestPayload);
	    	int positive = recommendService.getPositive(recordText);
	    	recommend.setPositive(positive);
	    	System.out.println("positive : "+positive);
	    	
	    	recommend.setUserId(userId);
	    	recommend.setRecordTitle(recordTitle);
//	    	String category = recommendService.getRecordData();
//	    	recommend.setCategory(category);
//	    	System.out.println(category);
	    	
	    	recommendService.updateDataset();
	    	
	    	System.out.println(recommend.toString());
	    	
	    	return null;
	    	
	    }

}
