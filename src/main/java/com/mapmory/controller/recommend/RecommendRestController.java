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
	    
	    @PostMapping("/test")
	    public String test(@RequestBody Map<String, String> requestPayload) throws Exception {

	    	int positive = recommendService.getPositive(requestPayload);
	    	
	    	System.out.println(positive);
	    	
	    	return null;
	    	
	    }

}
