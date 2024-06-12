package com.mapmory.controller.community;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mapmory.common.domain.Search;
import com.mapmory.common.util.ContentFilterUtil;
import com.mapmory.common.util.ImageFileUtil;
import com.mapmory.common.util.ObjectStorageUtil;
import com.mapmory.services.community.domain.Reply;
import com.mapmory.services.community.service.CommunityService;

@RestController
@RequestMapping("/community/*")
public class CommunityRestController {

	@Autowired
	@Qualifier("communityServiceImpl")
	private CommunityService communityService;
	
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

	@PutMapping("/rest/updateReply/{userId}/{replyNo}")
	public ResponseEntity<Reply> updateReply(@ModelAttribute Reply reply, @RequestParam(value = "replyImageName", required = false) MultipartFile replyImageName,
												@RequestParam("userId") String userId, @RequestParam("replyNo") int replyNo, @RequestParam("replyText") String replyText, Search search) throws Exception {

	    try {
	        // 이미지가 업데이트되었는지 확인하고 업데이트된 경우 새로운 이미지를 업로드합니다.
	        if (replyImageName != null && !replyImageName.isEmpty()) {
	            if (!contentFilterUtil.checkBadImage(replyImageName)) {
	                String uuid = ImageFileUtil.getImageUUIDFileName(replyImageName.getOriginalFilename());
	                objectStorageUtil.uploadFileToS3(replyImageName, uuid, replyFolder);
	                reply.setReplyImageName(uuid);
	            } else {
	                // 유해 이미지가 감지된 경우 예외를 던집니다.
	                throw new Exception("유해 이미지가 감지되어 댓글을 수정할 수 없습니다.");
	            }
	        }

	        reply.setReplyNo(replyNo);
	        reply.setReplyText(replyText);

	        // 댓글을 업데이트합니다.
	        communityService.updateReply(reply);

	        // 수정된 댓글을 응답합니다.
	        return ResponseEntity.ok(reply);
	    } catch (Exception e) {
	        // 예외가 발생한 경우 예외 메시지를 응답합니다.
	    	return ResponseEntity.noContent().build();
	    }
	}	
	
	@DeleteMapping("/rest/deleteReply/{userId}/{replyNo}")
	public String deleteReply(@PathVariable String userId, @PathVariable int replyNo) throws Exception {
		communityService.deleteReply(userId, replyNo);
		return "redirect: community/getReplyList";
		
	}	
	
	
}