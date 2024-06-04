package com.mapmory.controller.recommend;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mapmory.services.recommend.domain.Recommend;
import com.mapmory.services.recommend.service.RecommendService;

@Controller
@RequestMapping("/recommend/*")
public class RecommendController {

	@Autowired
	@Qualifier("recommendServiceImpl")
	private RecommendService recommendService;
    
    @PostMapping("/test")
    public String test(@RequestBody Map<String, String> requestPayload) throws Exception{
    	
    	System.out.println("RecommendRestController /test run");
    	System.out.println("====");
    	System.out.println(requestPayload.getClass());
    	System.out.println("====");
    	
    	int recordNo = 1;
    	Recommend recommend = recommendService.getRecordData(recordNo);
    	
//    	int positive = recommendService.getPositive(requestPayload);
    	int positive = recommendService.getPositive("ASD");
    	recommend.setPositive(positive);
    	System.out.println("positive : "+positive);
    	
//    	String category = recommendService.getRecordData();
//    	recommend.setCategory(category);
//    	System.out.println(category);
    	
    	System.out.println(recommend.toString());
    	
    	return null;
    	
    }
	
}
