package com.mapmory.controller.recommend;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mapmory.services.recommend.domain.Recommend;
import com.mapmory.services.recommend.service.RecommendService;

@Controller
@RequestMapping("/recommend/*")
public class RecommendController {

	@Autowired
	@Qualifier("recommendServiceImpl")
	private RecommendService recommendService;
    
//    @PostMapping("/test")
//    public String test(@RequestBody Map<String, String> requestPayload) throws Exception{
//    	
//    	System.out.println("RecommendRestController /test run");
//    	System.out.println("====");
//    	System.out.println(requestPayload.getClass());
//    	System.out.println("====");
    	
//    	int recordNo = 1;
//    	Recommend recommend = recommendService.getRecordData(recordNo);
//    	
////    	int positive = recommendService.getPositive(requestPayload);
//    	int positive = recommendService.getPositive("ASD");
//    	recommend.setPositive(positive);
//    	System.out.println("positive : "+positive);
//    	
////    	String category = recommendService.getRecordData();
////    	recommend.setCategory(category);
////    	System.out.println(category);
//    	
//    	System.out.println(recommend.toString());
//    	
//    	return null;
    	
//    }
	
	@GetMapping("/recommend")
	public void getRecommendPlace(@RequestParam String userId) throws Exception{
		
		List<String> values = recommendService.getRecommendData(userId);
		Map<String, Object> map = recommendService.getRecordList(values);
		System.out.println(map);
	
	}
	
}
