package com.mapmory.controller.community;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mapmory.common.domain.Page;
import com.mapmory.common.domain.Search;
import com.mapmory.common.domain.SessionData;
import com.mapmory.common.util.ContentFilterUtil;
import com.mapmory.common.util.ImageFileUtil;
import com.mapmory.common.util.ObjectStorageUtil;
import com.mapmory.common.util.RedisUtil;
import com.mapmory.services.community.dao.CommunityDao;
import com.mapmory.services.community.domain.CommunityLogs;
import com.mapmory.services.community.domain.Reply;
import com.mapmory.services.community.domain.Report;
import com.mapmory.services.community.service.CommunityService;
import com.mapmory.services.timeline.dto.SharedRecordDto;
import com.mapmory.services.timeline.service.TimelineService;
import com.mapmory.services.user.domain.FollowBlock;
import com.mapmory.services.user.service.UserService;

import retrofit2.http.Path;

@RestController
@RequestMapping("/community/*")
public class CommunityRestController {

	@Autowired
	@Qualifier("communityServiceImpl")
	private CommunityService communityService;
	
	@Autowired
	@Qualifier("timelineService")
	private TimelineService timelineService;
	
	@Autowired
	@Qualifier("userServiceImpl") 
	private UserService userService;
	
	@Autowired
	private CommunityDao communityDao;
	
	@Autowired
	private ContentFilterUtil contentFilterUtil;
	
    @Autowired
    private ObjectStorageUtil objectStorageUtil;
    
    @Autowired
    private RedisUtil<SessionData> redisUtil;
	
	public CommunityRestController() {
		System.out.println(this.getClass());
	}
	
	@Value("${page.Unit}")
	int pageUnit;
	
	@Value("${page.Size}")
	int pageSize;
	
	@Value("${object.reply.folderName}")
	private String replyFolder;
	
	@Value("${object.timeline.image}")
	private String recordImage;
	
	//공유 기록 목록 무한스크롤 리스트 호출
    @GetMapping("/rest/getSharedRecordList")
    public ResponseEntity<?> getSharedRecordList(Search search, @RequestParam(required = true) int currentPage,  
    												HttpServletRequest request) throws Exception{
        
    	System.out.println("REST 시작");
    	
        String userId = redisUtil.getSession(request).getUserId();
        
        currentPage = (search.getCurrentPage() != 0) ? search.getCurrentPage() : currentPage;
        int pageSize = (search.getPageSize() != 0) ? search.getPageSize() : 10;
        
        // pageSize를 search 객체에 설정
        search.setPageSize(pageSize);
        
        int offset = (currentPage - 1) * pageSize;
        search.setLimit(pageSize);
        search.setOffset(offset);
        
        System.out.println("현재 페이지: " + currentPage);
        System.out.println("페이지 사이즈: " + pageSize);
        System.out.println("계산된 offset 값: " + offset);
        System.out.println("Search 객체: " + search);
        
	    List<SharedRecordDto> list = timelineService.getSharedRecordList(search);
        
        return ResponseEntity.ok(List.of("list" , list));
    }

	//댓글 추가
	@PostMapping("/rest/addReply")
	public ResponseEntity<Reply> addReply(@RequestParam(value = "replyImageName", required = false) MultipartFile replyImageName, 
							@RequestParam("recordNo") int recordNo, HttpServletRequest request, @RequestParam("userId") String userId,
							@RequestParam("replyText") String replyText, Search search) throws Exception {
		userId = redisUtil.getSession(request).getUserId();
		
		System.out.println("/rest/addReply : REST 시작");
		
		System.out.println(userId);
		
		Reply reply = new Reply();
		reply.setRecordNo(recordNo);
		reply.setReplyText(replyText);
		reply.setUserId(userId);
		
		System.out.println(reply);
		

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

	//이미지 불러오기
	@GetMapping("/rest/replyImage/{uuid}")
	public byte[] getImage(@PathVariable String uuid) throws Exception {
		
		byte[] imageName = objectStorageUtil.getImageBytes(uuid, replyFolder);

		return imageName;
		
	}

	//기록별 댓글 목록 조회
	@GetMapping("/rest/getReplyList/{recordNo}")
	public ResponseEntity<Map<String, Object>> getReplyList(Search search, @PathVariable int recordNo) throws Exception {
		if(search == null) {
		search = Search.builder()
				.currentPage(1)
				.limit(10)
				.build();
		}
		Map<String, Object> replyData = communityService.getReplyList(search, recordNo);
		return ResponseEntity.ok(replyData);
	}
	
	//사용자별 댓글 목록 조회
	@GetMapping("/rest/getReplyList/user/{userId}")
	public ResponseEntity<Map<String, Object>> getReplyListByUser(Search search, @PathVariable String userId, @RequestParam Integer currentPage, HttpServletRequest request) throws Exception {
		
		if(search == null) {
			search = Search.builder()
					.userId(userId)
					.currentPage(currentPage)
					.limit(10)
					.build();
		}
		
		Map<String, Object> replyData = communityService.getUserReplyList(search, userId);
		System.out.println("replyData :: "+ replyData);
		return ResponseEntity.ok(replyData);	
	}

	//좋아요한 댓글 목록
	@GetMapping("/rest/getReplyLikeList/{userId}")
	public ResponseEntity<Map<String, Object>> getReplyLikeList(Search search, @PathVariable String userId, @RequestParam Integer currentPage) throws Exception {
		
		System.out.println("userId :: " + userId);
		if(search == null) {
			search = Search.builder()
					.userId(userId)
					.currentPage(currentPage)
					.limit(10)
					.build();
		}
		Map<String,Object> replyLikeList = communityService.getReplyLikeList(search, userId);
		System.out.println("좋아요 댓글 목록 :: "+replyLikeList);
		return ResponseEntity.ok(replyLikeList);
	}
	
	//사용자별 커뮤니티 로그 기록
	@GetMapping("/rest/getCommunityLogsList/{userId}")
	public ResponseEntity<Map<String, Object>> getCommunityLogsList(Search search, @PathVariable String userId, HttpServletRequest request) throws Exception {
	
		userId = redisUtil.getSession(request).getUserId();
		
		CommunityLogs communityLogs = new CommunityLogs();
		communityLogs.setUserId(userId);
		
		Map<String, Object> result = communityService.getCommunityLogsList(search, communityLogs);
		
		return ResponseEntity.ok(result);
	}
	
	//댓글 수정
	@PostMapping("/rest/updateReply/{replyNo}")
	public ResponseEntity<Reply> updateReply(@PathVariable("replyNo") int replyNo, @RequestParam("userId") String userId, 
												String updateReplyText, int recordNo, HttpServletRequest request, 
												@RequestParam(value = "updateReplyImageName", required = false) MultipartFile updateReplyImageName) throws Exception {

		userId = redisUtil.getSession(request).getUserId();
		
		Reply reply = new Reply();
		reply.setReplyNo(replyNo);
		reply.setRecordNo(recordNo);
		reply.setReplyText(updateReplyText);
		reply.setUserId(userId);
		
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
	
	//댓글 삭제
	@DeleteMapping("/rest/deleteReply/{userId}/{replyNo}")
	public String deleteReply(@PathVariable String userId, @PathVariable int replyNo, CommunityLogs communityLogs, HttpServletRequest request) throws Exception {
		
		int recordNo = 0;
		userId = redisUtil.getSession(request).getUserId();
		communityService.deleteCommunityLogs(communityLogs);		

		communityService.deleteReply(userId, replyNo);
		return "redirect: community/getReplyList";
	}	

	//기록별 댓글 수
	@PostMapping("/rest/getReplyTotalCount")
	public ResponseEntity<Integer> getReplyTotalCount(Search search, int recordNo) throws Exception {
		int replyCount = communityDao.getReplyTotalCount(search, recordNo);	
		return ResponseEntity.ok(replyCount);
	}
	
	//사용자 작성 댓글 수
	@PostMapping("/rest/getReplyUserTotalCount")
	public ResponseEntity<Integer> getReplyUserTotalCount(Search search, String userId) throws Exception {
		int userReplyCount = communityDao.getReplyUserTotalCount(search, userId);
		return ResponseEntity.ok(userReplyCount);
	}
		
	//좋아요 싫어요 중복 체크
	@PostMapping("/rest/checkLogs")
	public ResponseEntity<CommunityLogs> checkLogs(@RequestBody CommunityLogs communityLogs, String userId, HttpServletRequest request) throws Exception {
		
		userId = redisUtil.getSession(request).getUserId();
		
		communityLogs.setUserId(userId);
		
		communityService.checkLog(communityLogs);
		
		return ResponseEntity.ok(communityLogs);
	}
	
//	//좋아요 싫어요 상태 반환
//	@PostMapping("/rest/getReactionStatus")
//	public ResponseEntity<?> getReactionStatus(@RequestBody CommunityLogs communityLogs,  @RequestParam(value = "replyNo", required = false) Integer replyNo, 
//							String userId, HttpServletRequest request) throws Exception {
//		
//		userId = redisUtil.getSession(request).getUserId();
//		communityLogs.setUserId(userId);
//		
//		boolean alreadyReaction = communityService.getReactionStatusList(communityLogs);
//	
//		if(!alreadyReaction) {
//			
//			try {
//				return ResponseEntity.ok().build();
//			} catch (Exception e) {
//				e.printStackTrace();
//				return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
//			}	
//			} else {
//				return ResponseEntity.badRequest().body("이미 감정 표현했습니다");
//			}
//	}

	
//	//커뮤니티 로그 추가
//	@PostMapping("/rest/addCommunityLogs")
//	public ResponseEntity<CommunityLogs> addCommunityLogs(@RequestBody CommunityLogs communityLogs, String userId, HttpServletRequest request) throws Exception {
//		
//		userId = redisUtil.getSession(request).getUserId();
//		
//		communityLogs.setUserId(userId);
//		
//		communityService.addCommunityLogs(communityLogs);
//		return ResponseEntity.ok(communityLogs);
//	}
	
	//즐겨찾기 취소
	@DeleteMapping("/rest/deleteBookmark/{userId}/{recordNo}")
	public String deleteCommunityLogs(@PathVariable String userId, @PathVariable int recordNo, HttpServletRequest request) throws Exception {

		userId = redisUtil.getSession(request).getUserId();
		
		return "redirect: community/getDetailSharedRecord/"+recordNo;
	}
	
	//좋아요 개수
	@PostMapping("/rest/getReactionLikeTotalCount")
	public ResponseEntity<Integer> getReactionLikeTotalCount(Search search, @RequestBody CommunityLogs communityLogs, @RequestParam(required = false) Integer replyNo) throws Exception {
		int likeCount = communityDao.getReactionLikeTotalCount(communityLogs);
		return ResponseEntity.ok(likeCount);
	}	
	
	//싫어요 개수
	@PostMapping("/rest/getReactionDisLikeTotalCount")
	public ResponseEntity<Integer> getReactionDisLikeTotalCount(Search search, @RequestBody CommunityLogs communityLogs, @RequestParam(required = false) Integer replyNo ) throws Exception {
		int dislikeCount = communityDao.getReactionDisLikeTotalCount(communityLogs);
		return ResponseEntity.ok(dislikeCount);
	}	
	
	//즐겨찾기 확인
	@PostMapping("/rest/getBookmark")
	public ResponseEntity<Integer> getBookmark(Search search, @RequestBody CommunityLogs communityLogs, @RequestParam(required = false) Integer replyNo) throws Exception {
	
		int bookmark = communityDao.checkDuplicatieLogs(communityLogs.getUserId(), communityLogs.getRecordNo(), communityLogs.getReplyNo(), communityLogs.getLogsType());
		return ResponseEntity.ok(bookmark);
	}
	
	//신고하기 제출
	@PostMapping("/rest/doReport")
	public ResponseEntity<Report> doReport(@RequestBody Report report) throws Exception {
		communityService.doReport(report);
		return ResponseEntity.ok(report);
	}
	
	//사용자 신고 건수
	@GetMapping("/rest/getUserReportTotalCount")
	public ResponseEntity<Integer> getUserReportTotalCount(Search search, @RequestParam String userId) throws Exception {
		int reportCount = communityDao.getUserReportTotalCount(search, userId);
		return ResponseEntity.ok(reportCount);
	}
	
	//관리자 신고 처리
	@PostMapping("/rest/confirmReport/{reportNo}")
	public ResponseEntity<Report> confirmReport(Search search, @RequestBody Report report, @PathVariable int reportNo) throws Exception {
		communityService.confirmReport(report);
		return ResponseEntity.ok(report);
	}
	
	//사용자 차단
	@PostMapping("/rest/addBlockUser")
	public ResponseEntity<FollowBlock> addBlockUser(@RequestBody FollowBlock followBlock) throws Exception{
		
		if (userService.checkFollow(followBlock.getUserId(), followBlock.getTargetId()) == true) {
			communityService.updateBlockUser(followBlock);
		} 
		communityService.addBlockUser(followBlock);
		return ResponseEntity.ok(followBlock);
	}

	//사용자 신고 목록 Rest
	@GetMapping("/rest/getUserReportList/{userId}")
	public ResponseEntity<Model> getUserReportList(@PathVariable String userId, Search search, HttpServletRequest request, Model model) throws Exception {
		
		userId = redisUtil.getSession(request).getUserId();
		
		if(search.getCurrentPage() == 0) {
			search.setCurrentPage(1);
		}
	
		search.setPageSize(pageSize);
		
		userId = redisUtil.getSession(request).getUserId();
		
		Map<String, Object> reportList = communityService.getUserReportList(search, userId);
		System.out.println("테스트 : "+userId);
		model.addAttribute("reportList",  reportList.get("list"));
		model.addAttribute("totalCount", reportList.get("totalCount"));		
		
		return ResponseEntity.ok(model);
	}
	
	//차단 해제
	@DeleteMapping("/rest/deleteBlock/{userId}/{targetId}")
	public String deleteReply(@PathVariable String userId, @PathVariable String targetId, HttpServletRequest request) throws Exception {
		
		userId = redisUtil.getSession(request).getUserId();
		communityService.deleteBlockedUser(userId, targetId);

		return "redirect: community/getBlockList";
	}	

	//차단 목록 REST
    @GetMapping("/rest/getBlockList/{userId}")
    public ResponseEntity<Model> getBlockList(Search search, @PathVariable String userId, HttpServletRequest request, Model model) throws Exception {
		
		if(search.getCurrentPage() == 0) {
			search.setCurrentPage(1);
		}
	
		search.setPageSize(pageSize);
		
		userId = redisUtil.getSession(request).getUserId();
		
		Map<String, Object> blockList = communityService.getBlockedList(search, userId);
		System.out.println("테스트 : "+userId);
		model.addAttribute("blockList", blockList.get("list"));
		model.addAttribute("totalCount", blockList.get("totalCount"));		
    	return ResponseEntity.ok(model);
 
    }			
}