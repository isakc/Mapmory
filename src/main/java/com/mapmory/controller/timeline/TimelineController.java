package com.mapmory.controller.timeline;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mapmory.services.timeline.service.TimelineService;


@Controller("timelineController")
@RequestMapping("/timeline/*")
public class TimelineController {
	@Autowired
	@Qualifier("timelineService")
	private TimelineService timelineService;
	
	@GetMapping({"getDetailTimeline2"})
	public void getDetailTimeline2(Model model) throws Exception,IOException {
		model.addAttribute("record2",timelineService.getDetailTimeline2(1));
	}
	
	@GetMapping({"getDetailTimeline"})
	public void getDetailTimeline(Model model) throws Exception,IOException {
		model.addAttribute("record",timelineService.getDetailTimeline(1));
	}
	
	public void updateTimeline(Model model) throws Exception,IOException{
//		model.addAttribute("record",timelineService.getDetailTimeline(1));
	}
}
