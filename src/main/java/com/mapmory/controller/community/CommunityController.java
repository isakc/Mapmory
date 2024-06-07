package com.mapmory.controller.community;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.mapmory.services.community.domain.Reply;
import com.mapmory.services.community.service.CommunityService;

@Controller
@RequestMapping("community/*")
public class CommunityController {

	@Autowired
	@Qualifier("communityServiceImpl")
	private CommunityService communityService;
	
	@Value("${pageUnit}")
	int PageUnit;
	
	@Value("${pageSize}")
	int PageSize;
	
	@PostMapping("/addReply")
	public String addReply(MultipartHttpServletRequest request)  throws Exception{
		
		System.out.println("/community/addReply : POST 시작");
		
		MultipartFile file = request.getFile("replyImageName");
		int recordNo = (Integer.parseInt(request.getParameter("recordNo")));
		String userId = request.getParameter("userId");
		String replyText = request.getParameter("replyText");
		String replyImageName = null;
	
		
		return null;
	}
	
	
}
