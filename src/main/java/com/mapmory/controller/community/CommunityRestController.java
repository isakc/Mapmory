package com.mapmory.controller.community;

import java.util.List;
import java.util.Map;

import org.apache.catalina.startup.ClassLoaderFactory.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mapmory.common.domain.Search;
import com.mapmory.common.util.ContentFilterUtil;
import com.mapmory.common.util.ImageFileUtil;
import com.mapmory.common.util.ObjectStorageUtil;
import com.mapmory.services.community.dao.CommunityDao;
import com.mapmory.services.community.domain.CommunityLogs;
import com.mapmory.services.community.domain.Reply;
import com.mapmory.services.community.domain.Report;
import com.mapmory.services.community.service.CommunityService;

@RestController
@RequestMapping("/community/*")
public class CommunityRestController {

	@Autowired
	@Qualifier("communityServiceImpl")
	private CommunityService communityService;
	
	@Autowired
	private CommunityDao communityDao;
	
	@Autowired
	private ContentFilterUtil contentFilterUtil;
	
    @Autowired
    private ObjectStorageUtil objectStorageUtil;	
	
	public CommunityRestController() {
		System.out.println(this.getClass());
	}
	
	@Value("${page.Unit}")
	int PageUnit;
	
	@Value("${page.Size}")
	int PageSize;
	
	@Value("${object.reply.folderName}")
	private String replyFolder;
	
	@PostMapping("/rest/addReply")
	public ResponseEntity<Reply> addReply(@RequestParam(value = "replyImageName", required = false) MultipartFile replyImageName, 
							@RequestParam("recordNo") int recordNo,
							@RequestParam("replyText") String replyText, Search search) throws Exception {
		System.out.println("/rest/addReply : REST 시작");
		
		Reply reply = new Reply();
		reply.setRecordNo(recordNo);
		reply.setReplyText(replyText);
		reply.setUserId("user5");

		if (replyImageName != null && !replyImageName.isEmpty()) {
			if(contentFilterUtil.checkBadImage(replyImageName) == false) {
				System.out.println("이미지 검사 통과");
				String uuid = ImageFileUtil.getImageUUIDFileName(replyImageName.getOriginalFilename());
				objectStorageUtil.uploadFileToS3(replyImageName, uuid, replyFolder);		
				reply.setReplyImageName(uuid);
			} else {
			System.out.println("유해 이미지 차단");
			}
		} else {
			reply.setReplyImageName(""); 
		}

		communityService.addReply(reply);
		
		System.out.println("getReplyList :"+ communityService.getReplyList(search, recordNo));
		
	    Map<String, Object> replyMap = communityService.getReplyList(search, recordNo);
	    List<Object> replyList = (List<Object>) replyMap.get("list");
	    
	    if (replyList != null && !replyList.isEmpty()) {
	        Reply lastReply = (Reply) replyList.get(replyList.size() - 1);
	        System.out.println("마지막 댓글: " + lastReply);
	        return ResponseEntity.ok(lastReply);
	    } else {
	        System.out.println("댓글 목록이 비어 있습니다.");
	        return ResponseEntity.noContent().build(); 
	    }
	}  		

	@PostMapping("/rest/updateReply/{replyNo}")
	public ResponseEntity<Reply> updateReply(@PathVariable("replyNo") int replyNo, String userId, String updateReplyText, int recordNo, 
												@RequestParam(value = "replyImageName", required = false) MultipartFile updateReplyImageName) throws Exception {

		Reply reply = new Reply();
		reply.setReplyNo(replyNo);
		reply.setRecordNo(recordNo);
		reply.setReplyText(updateReplyText);
		reply.setUserId("user5");
		
	
		if (updateReplyImageName != null && !updateReplyImageName.isEmpty()) {
			if(contentFilterUtil.checkBadImage(updateReplyImageName) == false) {
				System.out.println("이미지 검사 통과");
				String uuid = ImageFileUtil.getImageUUIDFileName(updateReplyImageName.getOriginalFilename());
				objectStorageUtil.uploadFileToS3(updateReplyImageName, uuid, replyFolder);		
				reply.setReplyImageName(uuid);
			} else {
			System.out.println("유해 이미지 차단");
			}
		} else {
			reply.setReplyImageName(""); 
		}
	       communityService.updateReply(reply);
		return ResponseEntity.ok(reply);

	}       
	
	@DeleteMapping("/rest/deleteReply/{userId}/{replyNo}")
	public String deleteReply(@PathVariable String userId, @PathVariable int replyNo) throws Exception {
		
		int recordNo = 0;
		communityService.deleteCommunityLogs(userId, recordNo, replyNo);
		
		communityService.deleteReply(userId, replyNo);
		return "redirect: community/getReplyList";
	}	

	@PostMapping("/rest/getReplyTotalCount")
	public ResponseEntity<Integer> getReplyTotalCount(Search search, int recordNo) throws Exception {
		int replyCount = communityDao.getReplyTotalCount(search, recordNo);	
		return ResponseEntity.ok(replyCount);
	}
	
	@PostMapping("/rest/getReplyUserTotalCount")
	public ResponseEntity<Integer> getReplyUserTotalCount(Search search, String userId) throws Exception {
		int userReplyCount = communityDao.getReplyUserTotalCount(search, userId);
		return ResponseEntity.ok(userReplyCount);
	}	
	
	
	@PostMapping("/rest/addCommunityLogs")
	public ResponseEntity<CommunityLogs> addCommunityLogs(@RequestBody CommunityLogs communityLogs) throws Exception {
			communityService.addCommunityLogs(communityLogs);
		return ResponseEntity.ok(communityLogs);
	}
	
	@PostMapping("/rest/getReactionLikeTotalCount")
	public ResponseEntity<Integer> getReactionLikeTotalCount(Search search, @RequestBody CommunityLogs communityLogs, @RequestParam(required = false) Integer replyNo) throws Exception {
		int likeCount = communityDao.getReactionLikeTotalCount(communityLogs);
		return ResponseEntity.ok(likeCount);
	}	
	
	@PostMapping("/rest/getReactionDisLikeTotalCount")
	public ResponseEntity<Integer> getReactionDisLikeTotalCount(Search search, @RequestBody CommunityLogs communityLogs, @RequestParam(required = false) Integer replyNo ) throws Exception {
		int dislikeCount = communityDao.getReactionDisLikeTotalCount(communityLogs);
		return ResponseEntity.ok(dislikeCount);
	}	
	
	@PostMapping("/rest/doReport")
	public ResponseEntity<Report> doReport(@RequestBody Report report) throws Exception {
		communityService.doReport(report);
		return ResponseEntity.ok(report);

	}
	

	
	
	
	
	
}