package com.mapmory.controller.community;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.mapmory.common.domain.Search;
import com.mapmory.controller.timeline.TimelineController;
import com.mapmory.services.community.domain.Reply;
import com.mapmory.services.community.service.CommunityService;
import com.mapmory.services.timeline.service.TimelineService;

@Controller
@RequestMapping("community/*")
public class CommunityController {

	@Autowired
	@Qualifier("communityServiceImpl")
	private CommunityService communityService;
	
	@Autowired
	@Qualifier("timelineService")
	private TimelineService timelineService;
	
	@Value("${page.Unit}")
	int PageUnit;
	
	@Value("${page.Size}")
	int PageSize;
	
	TimelineController timelineController = new TimelineController();
	
	
	@GetMapping("/getSharedRecordList")
	public String getSharedRecordList(Search search, Model model) throws Exception {
		search = Search.builder()
				.currentPage(1)
				.limit(10)
				.build();
		model.addAttribute("sharedRecordlist",timelineService.getSharedRecordList(search));	
		return "community/getSharedRecordList";
	}
	
	@GetMapping("/getDetailSharedRecord/{recordNo}")
	public String getDetailTimeline(Model model, @PathVariable int recordNo) throws Exception {
		model.addAttribute("record",timelineService.getDetailTimeline(recordNo));			
		return "community/getDetailSharedRecord";
		
	}
	
	
//	@PostMapping("/addReply")
//	public String addReply(MultipartHttpServletRequest request)  throws Exception{
//		
//		System.out.println("/community/addReply : POST 시작");
//		
//		MultipartFile file = request.getFile("replyImageName");
//		int recordNo = (Integer.parseInt(request.getParameter("recordNo")));
//		String userId = request.getParameter("userId");
//		String replyText = request.getParameter("replyText");
//		String replyImageName = null;
//	
//		
//		return null;
//	}
	
	
}
