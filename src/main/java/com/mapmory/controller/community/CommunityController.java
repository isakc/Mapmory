package com.mapmory.controller.community;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mapmory.common.domain.Search;
import com.mapmory.common.domain.SessionData;
import com.mapmory.common.util.ObjectStorageUtil;
import com.mapmory.common.util.RedisUtil;
import com.mapmory.controller.timeline.TimelineController;
import com.mapmory.services.community.domain.CommunityLogs;
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
    
    @Autowired
    private RedisUtil<SessionData> redisUtil;
	
	@Value("${page.Unit}")
	private int pageUnit;
	
	@Value("${page.Size}")
	private int pageSize;
	
	@Value("${object.reply.folderName}")
	private String replyFolder;
	
	@Value("${timeline.kakaomap.apiKey}")
	private String apiKey;
	
	TimelineController timelineController = new TimelineController();
	
	//공유 기록 목록 조회
	@GetMapping("/getSharedRecordList")
	public String getSharedRecordList(Search search, String userId, Model model, HttpServletRequest request) throws Exception {
		
		userId = redisUtil.getSession(request).getUserId();
		
	    int currentPage = (search.getCurrentPage() != 0) ? search.getCurrentPage() : 1;
	    int pageSize = (search.getPageSize() != 0) ? search.getPageSize() : 10;
	    search.setLimit(pageSize);
	    search.setOffset((currentPage - 1) * pageSize);
	  
		model.addAttribute("sharedRecordlist",timelineService.getSharedRecordList(search));	
		System.out.println("목록 :"+model);
		return "community/getSharedRecordList";
	}
	
	//공유 기록 상세 조회
	@GetMapping("/getDetailSharedRecord/{recordNo}")
	public String getDetailSharedRecord(Search search, String userId, Model model, @PathVariable int recordNo, HttpServletRequest request) throws Exception{
		
		userId = redisUtil.getSession(request).getUserId();
			
		model.addAttribute("record", timelineService.getDetailSharedRecord(recordNo, userId));
		
	    int currentPage = (search.getCurrentPage() != 0) ? search.getCurrentPage() : 1;
	    int pageSize = (search.getPageSize() != 0) ? search.getPageSize() : 10;
	    search.setLimit(pageSize);
	    search.setOffset((currentPage - 1) * pageSize);		
		
		System.out.println("페이지 값1 : " +search);

	    
	    Map<String, Object> replyData = communityService.getReplyList(search, recordNo);
	    model.addAttribute("userId", userId);
	    model.addAttribute("apiKey", apiKey);	    
	    model.addAttribute("replyList", replyData.get("list"));
	    model.addAttribute("totalCount", replyData.get("totalCount"));
	    
	    CommunityLogs communityLogs = CommunityLogs.builder()
	    		.userId(userId)
	    		.recordNo(recordNo)
	    		.logsType(0)
	    		.build();
	    		
	    communityService.addCommunityLogs(communityLogs);		
	    
	    System.out.println("model :"+model);
		return "community/getDetailSharedRecord";
	}

	//댓글 목록 조회
	@GetMapping("/getReplyList/{recordNo}")
	public String getReplyList(Search search, String userId, @PathVariable int recordNo, Model model, HttpServletRequest request, CommunityLogs communityLogs) throws Exception {
		
		userId = redisUtil.getSession(request).getUserId();

		search.setUserId(userId);
		
	    int currentPage = (search.getCurrentPage() != 0) ? search.getCurrentPage() : 1;
	    int pageSize = (search.getPageSize() != 0) ? search.getPageSize() : 10;
	    search.setLimit(pageSize);
	    search.setOffset((currentPage - 1) * pageSize);
				
		System.out.println("페이지 값: " +search);
		
	    Map<String, Object> replyData = communityService.getReplyList(search, recordNo);
	    Map<String, Object> reactionData = communityService.getReactionStatusList(communityLogs);
	    
	    model.addAttribute("search", search);
	    model.addAttribute("replyList", replyData.get("list"));
	    model.addAttribute("totalCount", replyData.get("totalCount"));

	    System.out.println("댓글 리스트 :"+model);
	    
		return "community/getReplyList";
    }	
	
	//댓글 선택 조회(관리자용)
	@GetMapping("/getReply/{replyNo}")
	public String getReply(Search search, @PathVariable int replyNo, Model model) throws Exception {
		
		model.addAttribute("reply", communityService.getReply(replyNo));
		return "community/getReply";
	}
	
	//신고하기 화면 이동
	@GetMapping("/addReport")
	public String addReport(Model model) throws Exception {
		return "community/addReport";
	}
	
	//사용자 신고 목록 조회	
	@GetMapping("/getUserReportList/{userId}")
	public String getUserReportListt(@Param("search")Search search, @PathVariable("userId") String userId, Model model, HttpServletRequest request) throws Exception {
    
		if(search.getCurrentPage() == 0) {
			search.setCurrentPage(1);
		}
	
		search.setPageSize(pageSize);
		
		userId = redisUtil.getSession(request).getUserId();
		
		Map<String, Object> reportList = communityService.getUserReportList(search, userId);
		System.out.println("테스트 : "+userId);
		model.addAttribute("reportList",  reportList.get("list"));
		model.addAttribute("totalCount", reportList.get("totalCount"));
		
		return "community/getUserReportList";
	}	
	
	//관리자 신고 조회
	@GetMapping("/admin/getAdminReportList")
	public String getAdminReportList(Search search, Integer role, Model model) throws Exception {
		
		role = 0;
			
		if(role == null | role !=0) {
			return "index";
		}
		
    	if(search.getCurrentPage() == 0) {
			search.setCurrentPage(1);
		}
		
		search.setPageSize(pageSize);
		
		Map<String, Object> allReportList = communityService.getAdminReportList(search);

		model.addAttribute("allReportList", allReportList.get("list"));
		model.addAttribute("totalCount", allReportList.get("totalCount"));
		model.addAttribute("unConfirmCount", allReportList.get("unConfirmCount"));
			
			return "community/admin/getAdminReportList";
	}
	
	//관리자 신고 처리
	@GetMapping("/admin/getAdminConfirmReport/{reportNo}")
	public String getAdminConfirmReport(Model model, Search search, @PathVariable int reportNo) throws Exception {

		model.addAttribute("report", communityService.getReport(reportNo));
		return "community/admin/getAdminConfirmReport";
	}	
	
	
	
	
//	@PostMapping("/deleteReplyByRecord/{recordNo}")
//	public void deleteReplyByRecord(@PathVariable("recordNo") int recordNo) throws Exception {
//		communityService.deleteReplyByRecord(recordNo);
//		return;
//	}
//
//	@PostMapping("/deleteCommunityLogsByRecord/{recordNo}")
//	public void deleteCommunityLogsByRecord(@PathVariable("recordNo") int recordNo) throws Exception {
//		communityService.deleteCommunityLogsByRecord(recordNo);
//		return;
//	}	
	
	
}
