package com.mapmory.controller.community;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.mapmory.common.domain.Search;
import com.mapmory.common.util.ImageFileUtil;
import com.mapmory.common.util.ObjectStorageUtil;
import com.mapmory.controller.timeline.TimelineController;
import com.mapmory.services.community.domain.Reply;
import com.mapmory.services.community.domain.Report;
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
	private int pageUnit;
	
	@Value("${page.Size}")
	int pageSize;
	
	@Value("${object.reply.folderName}")
	private String replyFolder;
	
	TimelineController timelineController = new TimelineController();
	
	
	//공유 기록 목록 조회
	@GetMapping("/getSharedRecordList")
	public String getSharedRecordList(Search search, Model model) throws Exception {
		search = Search.builder()
				.currentPage(1)
				.limit(10)
				.build();
		model.addAttribute("sharedRecordlist",timelineService.getSharedRecordList(search));	
		return "community/getSharedRecordList";
	}
	
	//공유 기록 상세 조회
	@GetMapping("/getDetailSharedRecord/{recordNo}")
	public String getDetailSharedRecord(Model model, Search search, @PathVariable int recordNo) throws Exception{
		model.addAttribute("record", timelineService.getDetailSharedRecord(recordNo));
		
	    Map<String, Object> replyData = communityService.getReplyList(search, recordNo);
	    model.addAttribute("replyList", replyData.get("list"));
	    model.addAttribute("totalCount", replyData.get("totalCount"));
		return "community/getDetailSharedRecord";
	}

	//댓글 목록 조회
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
	public String getUserReportListt(Search search, @PathVariable String userId, Model model) throws Exception {
		if(search == null) {
			search = Search.builder()
					.currentPage(1)
					.limit(10)
					.build();
		}
	
		Map<String, Object> reportList = communityService.getUserReportList(search, userId);
		userId = "user5";
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
