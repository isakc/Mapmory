package com.mapmory.unit.community;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import com.mapmory.common.domain.Search;
import com.mapmory.services.community.domain.CommunityLogs;
import com.mapmory.services.community.domain.Reply;
import com.mapmory.services.community.domain.Report;
import com.mapmory.services.community.service.CommunityService;
import com.mapmory.services.user.domain.FollowBlock;

@SpringBootTest
public class CommunityServiceTest {

	@Autowired
	@Qualifier("communityServiceImpl")
	private CommunityService communityService;
	
	//@Test
	public void TestAddReply() throws Exception {

		Reply reply = Reply.builder()
				.recordNo(6)
				.userId("user8")
				.replyText("재밌다고해라")
				.build();
		
		communityService.addReply(reply);
		
		System.out.println("add 테스트 : " +reply);
	}	
	
	//@Test
	public void TestGetReply() throws Exception {
		
		Reply reply = new Reply();
		
		reply = communityService.getReply(8);
		
		System.out.println("get Reply 테스트 : "+reply);
	}	
	
	//@Test
	public void TestUpdateReply() throws Exception {
		
		Reply reply = communityService.getReply(14);
		
		reply.setReplyText("반가워유우우123.");
		reply.setReplyImageName("HiHi.jpg");
		
		communityService.updateReply(reply);
		
		System.out.println("update 테스트 : "+reply);	
	}

	//@Test
	public void TestDeleteReply() throws Exception {
				
		communityService.deleteReply("user1", 12);
		
		System.out.println("delete 테스트 : ");	
	}	
	
	//@Test
	public void TestGetReplyList() throws Exception {
		
		Search search = new Search();
		search.setCurrentPage(1);
		search.setPageSize(10);
		Map<String, Object> map = communityService.getReplyList(search, 1);
		
		List<Reply> list = (List<Reply>)map.get("list");
		
		System.out.println("list 테스트 : "+list);
		
		Integer totalCount = (Integer)map.get("totalCount");
		System.out.println("기록에 작성된 댓글 : "+totalCount);
	}	
	
	//@Test
	public void TestGetUserReplyList() throws Exception {
		
		Search search = new Search();
		search.setCurrentPage(1);
		search.setPageSize(10);
		Map<String, Object> map = communityService.getUserReplyList(search, "user2");
		
		List<Reply> list = (List<Reply>)map.get("list");
		
		System.out.println("list 테스트 : "+list);
		
		Integer totalCount = (Integer)map.get("totalCount");
		System.out.println("사용자가 작성한 댓글 : "+totalCount);		
	}
	
	//@Test
	public void TestAddCommunityLogs() throws Exception {
		
		CommunityLogs communityLogs = CommunityLogs.builder()
				.userId("user5")
				.recordNo(3)
				.replyNo(4)
				.logsType(2)
				.build();
		
		communityService.addCommunityLogs(communityLogs);
		System.out.println("활동 추가 결과 : "+communityLogs);		
	}
	
	//@Test
	public void TestGetCommunityLogs() throws Exception {
		
		CommunityLogs communityLogs = communityService.getCommunityLogs(14);
		
		System.out.println("커뮤니티 로그 출력 확인 : "+communityLogs);		
	}
	
	//@Test
	public void TestUpdateCommunityLogs() throws Exception {
		
		CommunityLogs communityLogs = communityService.getCommunityLogs(19);
		
		communityLogs.setLogsType(2);
		
		communityService.updateCommunityLogs(communityLogs);
		
		System.out.println("커뮤니티 활동 수정 : "+communityLogs);		
	}	
	
	//@Test
	public void TestDeleteCommunityLogs() throws Exception {

		communityService.deleteCommunityLogs(23, "user4");		
		System.out.println("delete 테스트 : ");	
	}	
	
	//@Test
	public void TestGetCommunityLogsList() throws Exception {
		Search search = new Search();
		search.setCurrentPage(1);
		search.setPageSize(10);
		Map<String, Object> map = communityService.getCommunityLogsList(search, "user2", 1);
		
		List<CommunityLogs> list = (List<CommunityLogs>)map.get("list");
		
		System.out.println("커뮤니티 활동 list 테스트 : "+list);	
	}
	
	//@Test
	public void TestAddReport() throws Exception {

		Report report = Report.builder()
				.reportNo(0)
				.userId("user4")
				.targetUserId("user7")
				.recordNo(null)
				.replyNo(null)
				.chatroomNo(null)
				.build();
				
		communityService.addReport(report);
	}
	
	//@Test
	public void TestDoReport() throws Exception {

		Report report = Report.builder()
				.userId("user4")
				.targetUserId("user7")
				.recordNo(null)
				.replyNo(null)
				.chatroomNo(null)
				.reportTitle("욕설함")
				.reportText("욕설이 난무함")
				.build();
				
		communityService.doReport(report);
	}	
	
	//@Test
	public void TestGetUserReportList() throws Exception {
		
		Search search = new Search();
		search.setCurrentPage(0);
		search.setPageSize(5);

		Map<String, Object> map = communityService.getUSerReportList(search, "user1");
		List<Report> list = (List<Report>)map.get("list");
		
		System.out.println("유저 신고 list 테스트 : "+list);	
		
		Integer totalCount = (Integer)map.get("totalCount");
		System.out.println("사용자 신고 총 건수 : "+totalCount);
	}

	//@Test
	public void TestGetAdminReportList() throws Exception {
		
		Search search = new Search();
		search.setCurrentPage(0);
		search.setPageSize(5);

		Map<String, Object> map = communityService.getAdminReportList(search, 1);
		List<Report> list = (List<Report>)map.get("list");
		
		System.out.println("총 신고 list 테스트 : "+list);	
		
		Integer totalCount = (Integer)map.get("totalCount");
		System.out.println("시스템 신고 총 건수 : "+totalCount);
	}	
	
	//@Test
	public void TestGetReport() throws Exception {
		
		Report report = new Report();
		report = communityService.getReport(3);
		
		System.out.println("get Report 테스트 : "+report);
	}
	
	//@Test
	public void TestConfirmReport() throws Exception {
		
		Report report = new Report();
		report = communityService.getReport(3);

		report.setReportStatus(2);
		report.setReportResult(1);
		
		System.out.println("신고 처리 테스트 : "+report);
	}	
	
	//@Test
	public void TestaddBlockUser() throws Exception {
		
		FollowBlock followBlock = FollowBlock.builder()
				.userId("user3")
				.targetId("user6")
				.fb_type(1)
				.build();
		
		communityService.addBlockUser(followBlock);

		System.out.println("신고 처리 테스트 : "+followBlock);
	}
	
	//@Test
	public void TestGetBlockedList() throws Exception {
		
		Search search = new Search();
		search.setCurrentPage(1);
		search.setPageSize(10);
		Map<String, Object> map = communityService.getBlockedList(search, "user4");		
		List<CommunityLogs> list = (List<CommunityLogs>)map.get("list");
		
		System.out.println("차단 유저 list 테스트 : "+list);	
	}
	
	//@Test
	public void TestGetBlockedUser() throws Exception {
		
		FollowBlock followBlock = communityService.getBlockedUser("user4", "user7");
		
		System.out.println("차단 유저 조회 테스트 : "+followBlock);	
	}
	
	@Test
	public void TestDeleteBlockedUser() throws Exception {
		
		Search search = new Search();
		search.setCurrentPage(1);
		search.setPageSize(10);
		Map<String, Object> map = communityService.getBlockedList(search, "user4");		
		List<CommunityLogs> list = (List<CommunityLogs>)map.get("list");
		
		communityService.getBlockedList(search, "user4");
		
		communityService.deleteBlockedUser("user4", "user7");

		System.out.println("차단 유저 삭제 확인 : "+list);	
	}	
	
}
