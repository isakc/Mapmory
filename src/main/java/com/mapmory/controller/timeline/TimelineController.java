package com.mapmory.controller.timeline;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mapmory.common.domain.Search;
import com.mapmory.services.timeline.domain.Record;
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
//		List<Record> recordList=new ArrayList<Record>();
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
//		for(Record record:timelineService.getTimelineList(search)) {
//			if(record != null)
//			recordList.add(record);
//		}
//		System.out.println("recordList : "+recordList);
//		model.addAttribute("list3",recordList);
		model.addAttribute("list3",timelineService.getTimelineList(search));
		search=Search.builder()
				.currentPage(3)
				.limit(3)
				.sharedType(0)
				.tempType(1)
				.timecapsuleType(0)
				.build();
		model.addAttribute("list4",timelineService.getTimelineList(search));
		search=Search.builder()
				.currentPage(3)
				.limit(3)
				.sharedType(1)
				.tempType(1)
				.timecapsuleType(0)
				.build();
		model.addAttribute("list5",timelineService.getTimelineList(search));
		search=Search.builder()
				.currentPage(1)
				.limit(5)
				.sharedType(0)
				.tempType(0)
				.timecapsuleType(1)
				.build();
		model.addAttribute("list6",timelineService.getTimelineList(search));
		//d_day보다 현재 날짜가 위에 있으면 갖고오는 조건식
		search=Search.builder()
				.currentPage(1)
				.limit(3)
				.sharedType(0)
				.tempType(1)
				.timecapsuleType(1)
				.build();
		model.addAttribute("list7",timelineService.getTimelineList(search));
		//대민 지원
		search=Search.builder()
				.userId(" user2 ").
				currentPage(1)
				.limit(5)
				.sharedType(1)
				.tempType(1)
				.timecapsuleType(0)
				.build();
		model.addAttribute("list8",timelineService.getTimelineList(search));
		//재용 지원
		search=Search.builder()
				.currentPage(1)
				.limit(5)
				.build();
		model.addAttribute("list9",timelineService.getSharedRecordList(search));
		search=Search.builder()
				.currentPage(2)
				.limit(5)
				.build();
		model.addAttribute("list10",timelineService.getSharedRecordList(search));
		search=Search.builder()
				.timecapsuleType(0)
				.selectDay1(LocalDateTime.parse("2024-06-04T00:00:00"))
				.selectDay2(LocalDateTime.parse("2024-06-05T00:00:00"))
				.build();
		model.addAttribute("list11",timelineService.getTimelineList(search));
		
		//성문 지원
		search=Search.builder()
	 			.latitude(33.450701)
	 			.longitude(126.570667)
	 			.radius(110)
	 			.limit(10)
	 			.userId("user1")
	 			.followType(1)
	 			.currentPage(1)
				.build();
		model.addAttribute("list12",timelineService.getMapRecordList(search));
		search=Search.builder()
				.latitude(33.450701)
	 			.longitude(126.570667)
	 			.radius(110)
	 			.limit(10)
	 			.userId("user1")
	 			.sharedType(1)
	 			.currentPage(1)
				.build();
		model.addAttribute("list13",timelineService.getMapRecordList(search));
		search=Search.builder()
				.latitude(37.56789)
	 			.longitude(127.345678)
	 			.radius(110)
	 			.limit(10)
	 			.userId("user1")
	 			.currentPage(1)
				.build();
		model.addAttribute("list14",timelineService.getMapRecordList(search));
		search=Search.builder()
				.latitude(33.450701)
	 			.longitude(126.570667)
	 			.radius(110)
	 			.limit(5)
	 			.currentPage(1)
				.build();
		model.addAttribute("list15",timelineService.getMapRecordList(search));
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
