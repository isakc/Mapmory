package com.mapmory.controller.community;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.mapmory.common.domain.Search;
import com.mapmory.common.util.ImageFileUtil;
import com.mapmory.common.util.ObjectStorageUtil;
import com.mapmory.controller.timeline.TimelineController;
import com.mapmory.services.community.domain.Reply;
import com.mapmory.services.community.domain.Reply.ReplyBuilder;
import com.mapmory.services.community.service.CommunityService;
import com.mapmory.services.timeline.service.TimelineService;

@Controller
@RequestMapping("/community/*")
public class CommunityController {

	@Autowired
	@Qualifier("communityServiceImpl")
	private CommunityService communityService;

	@Autowired
	@Qualifier("timelineService")
	private TimelineService timelineService;
	
    @Autowired
    private ObjectStorageUtil objectStorageUtil;	
	
	@Value("${page.Unit}")
	int PageUnit;
	
	@Value("${page.Size}")
	int PageSize;
	
	@Value("${object.reply.folderName}")
	private String replyFolder;
	
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
	public String getDetailSharedRecord(Model model, Search search, @PathVariable int recordNo) throws Exception{
		model.addAttribute("record", timelineService.getDetailSharedRecord(recordNo));
		
	    Map<String, Object> replyData = communityService.getReplyList(search, recordNo);
	    model.addAttribute("replyList", replyData.get("list"));
	    model.addAttribute("totalCount", replyData.get("totalCount"));
		return "community/getDetailSharedRecord";
	}

	@GetMapping("/getReplyList/{recordNo}")
	public String getReplyList(Search search, @PathVariable int recordNo, Model model) throws Exception {
		if(search == null) {
		search = Search.builder()
				.currentPage(1)
				.limit(10)
				.build();
		}
		
	    Map<String, Object> replyData = communityService.getReplyList(search, recordNo);
	    model.addAttribute("replyList", replyData.get("list"));
	    model.addAttribute("totalCount", replyData.get("totalCount"));

	   
		return "community/getReplyList";
    }	
	
	@PostMapping("/addReply")
	public String addReply(@RequestParam("userId") String userId, @RequestParam("replyText") String replyText, 
							@RequestParam("replyImageName") MultipartFile replyImageName, @RequestParam("recordNo") int recordNo) throws Exception{
		
		System.out.println("/community/addReply : POST 시작");
		
        String uuid = ImageFileUtil.getProductImageUUIDFileName(replyImageName.getOriginalFilename());
        String originalFilename = replyImageName.getOriginalFilename();

        objectStorageUtil.uploadFileToS3(replyImageName, uuid, replyFolder); 
        
		Reply reply = Reply.builder()
				.recordNo(recordNo)
				.userId("user3")
				.replyText(replyText)
				.replyImageName(uuid)
				.build();
	
		communityService.addReply(reply);
		return "community/getReplyList/"+recordNo;
	}
	
	
}
