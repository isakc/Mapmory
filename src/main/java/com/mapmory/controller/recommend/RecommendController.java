package com.mapmory.controller.recommend;

import java.io.IOException;
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
import com.mapmory.services.timeline.domain.Record;
import com.mapmory.services.timeline.service.TimelineService;

@Controller
@RequestMapping("/recommend/*")
public class RecommendController {

	@Autowired
	@Qualifier("recommendServiceImpl")
	private RecommendService recommendService;
	
	@Autowired
	@Qualifier("timelineService")
	private TimelineService timelineService;
    
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
	
	@PostMapping("/updaterecommend")
	public String updateTimeline(Model model,@ModelAttribute(value="record") Record record) throws Exception,IOException {
		record.setUpdateCount(record.getUpdateCount()+1);
		if(record.getMediaName()!=null || record.getImageName()!=null || record.getRecordText()!=null) {
			record.setTempType(1);
		}else {
			record.setTempType(0);
		}
		System.out.println("record.getImageName() : "+record.getImageName());
		System.out.println("record.getHashtag() : "+record.getHashtag());
		timelineService.updateTimeline(record);
		model.addAttribute("record",timelineService.getDetailTimeline(record.getRecordNo()));
		
		///////추천 추가된 부분//////
		/* 맨 위에 추가할 것
		@Autowired
		@Qualifier("recommendServiceImpl")
		private RecommendService recommendService;
		*/
		recommendService.addSearchData(record);
		Recommend recommend = recommendService.getRecordData(record, record.getRecordNo());
		recommend.setPositive(recommendService.getPositive(record.getRecordText()));
		recommendService.updateDataset(recommend);
		recommendService.saveDatasetToCSV(recommend, "aitems-8982956307867");
		System.out.println(recommend.toString());
		
		return "timeline/getDetailTimeline";
	}
	

	
}
