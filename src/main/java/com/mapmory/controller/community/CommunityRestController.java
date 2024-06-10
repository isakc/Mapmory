package com.mapmory.controller.community;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mapmory.services.community.domain.Reply;
import com.mapmory.services.community.service.CommunityService;

@RestController
@RequestMapping("community/*")
public class CommunityRestController {

	@Autowired
	@Qualifier("communityServiceImpl")
	private CommunityService communityService;
	
	public CommunityRestController() {
		System.out.println(this.getClass());
	}
	
	@Value("${page.Unit}")
	int PageUnit;
	
	@Value("${page.Size}")
	int PageSize;
	
	@PostMapping("/rest/addReply")
	public String addReply(Reply reply, MultipartFile replyImageName, int recordNo) throws Exception {
		communityService.addReply(reply);
		return "redirect: community/getReplyList/"+recordNo;
		
	}

	@PostMapping("/rest/updateReply")
	public String updateReply(Reply reply, MultipartFile replyImageName, int recordNo) throws Exception {
		communityService.updateReply(reply);
		return "redirect: community/getReplyList/"+recordNo;
		
	}	
	
	@PostMapping("/rest/deleteReply")
	public String deleteReply(Reply reply, MultipartFile replyImageName, String userId, int recordNo) throws Exception {
		communityService.deleteReply(userId, recordNo);
		return "redirect: community/getReplyList/"+recordNo;
		
	}	
	
	
}
