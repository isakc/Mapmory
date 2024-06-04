package com.mapmory.controller.timeline;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mapmory.services.timeline.domain.Search;
import com.mapmory.services.timeline.service.TimelineService;


@Controller("timelineController")
@RequestMapping("/timeline/*")
public class TimelineController {
	@Autowired
	@Qualifier("timelineService")
	private TimelineService timelineService;
	
	@GetMapping({"getDetailTimeline"})
	public void getDetailTimeline(Model model) throws Exception,IOException {
		model.addAttribute("record",timelineService.getDetailTimeline(1));
	}
	
	@GetMapping({"getDetailTimeline2"})
	public void getTimelineList(Model model) throws Exception,IOException {
		Search search=Search.builder()
				.currentPage(1)
				.limit(3)
				.build();
		model.addAttribute("list",timelineService.getTimelineList(search));
		search=Search.builder()
				.currentPage(1)
				.limit(3)
				.sharedType(1)
				.tempType(1)
				.timecapsuleType(0)
				.build();
		model.addAttribute("list2",timelineService.getTimelineList(search));
		search=Search.builder()
				.currentPage(1)
				.limit(3)
				.sharedType(0)
				.tempType(0)
				.timecapsuleType(0)
				.build();
		model.addAttribute("list3",timelineService.getTimelineList(search));
		search=Search.builder()
				.currentPage(1)
				.limit(3)
				.sharedType(0)
				.tempType(1)
				.timecapsuleType(0)
				.build();
		model.addAttribute("list4",timelineService.getTimelineList(search));
		search=Search.builder()
				.currentPage(1)
				.limit(3)
				.sharedType(1)
				.tempType(1)
				.timecapsuleType(0)
				.build();
		model.addAttribute("list5",timelineService.getTimelineList(search));
		search=Search.builder()
				.currentPage(1)
				.limit(3)
				.sharedType(0)
				.tempType(0)
				.timecapsuleType(1)
				.build();
		model.addAttribute("list6",timelineService.getTimelineList(search));
		search=Search.builder()
				.currentPage(1)
				.limit(3)
				.sharedType(0)
				.tempType(1)
				.timecapsuleType(1)
				.build();
		model.addAttribute("list7",timelineService.getTimelineList(search));
	}
	
	public void updateTimeline(Model model) throws Exception,IOException{
//		model.addAttribute("record",timelineService.getDetailTimeline(1));
	}
	
	
	//아래 미사용
//	@GetMapping({"getDetailTimeline2"})
//	public void getDetailTimeline2(Model model) throws Exception,IOException {
//		model.addAttribute("record",timelineService.getDetailTimeline2(1));
//	}
}
