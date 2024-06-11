package com.mapmory.unit.community;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import com.mapmory.common.domain.Search;
import com.mapmory.common.util.ContentFilterUtil;
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
				.recordNo(7)
				.userId("user4")
				.replyText("즐거운 모습이 보기 좋습니다.")
				.build();
		
		if(ContentFilterUtil.checkBadWord(reply.getReplyText()) == true) {
			System.out.println("비속어 등록 불가");
		} else {
			communityService.addReply(reply);	
		}
	}	
	
	//@Test
	public void TestGetReply() throws Exception {
		
		Reply reply = new Reply();
		
		reply = communityService.getReply(8);
		
		System.out.println("get Reply 테스트 : "+reply);
	}	
	
	//@Test
	public void TestUpdateReply() throws Exception {
		
		Reply reply = communityService.getReply(41);
		
		reply.setReplyText("안녕하세요 ㅆ  ㅂ  ^^");
		reply.setReplyImageName("HiHi.jpg");

		if(ContentFilterUtil.checkBadWord(reply.getReplyText()) == true) {
			System.out.println("비속어 등록 불가");
		} else {
			communityService.updateReply(reply);			
		}
		System.out.println("update 테스트 : "+reply);	
	}
		
	//@Test
	public void TestDeleteReply() throws Exception {
				
		communityService.deleteReply("user1", 12);
		
		System.out.println("delete 테스트 : ");	
	}	
	
	@Test
	public void TestGetReplyList() throws Exception {
		
		Search search = new Search();
		search.setLimit(10);
		search.setOffset(0);
		Map<String, Object> map = communityService.getReplyList(search, 1);
		
		List<Reply> list = (List<Reply>)map.get("list");
		
		System.out.println("list 테스트 : "+list);
		
		Integer totalCount = (Integer)map.get("totalCount");
//		Integer likeCount = (Integer)map.get("likeCount");
//		Integer dislikeCount = (Integer)map.get("dislikeCount");
	
		System.out.println("기록에 작성된 댓글 : "+totalCount);
//		System.out.println("좋아요 수 : "+likeCount);
//		System.out.println("싫어요 수 : "+dislikeCount);
		
	}	
	
	//@Test
	public void TestGetUserReplyList() throws Exception {
		
		Search search = new Search();
		search.setLimit(1);
		search.setOffset(0);
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
		search.setLimit(1);
		search.setOffset(0);
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
		search.setLimit(3);
		search.setOffset(0);

		Map<String, Object> map = communityService.getUserReportList(search, "user1");
		List<Report> list = (List<Report>)map.get("list");
		
		System.out.println("유저 신고 list 테스트 : "+list);	
		
		Integer totalCount = (Integer)map.get("totalCount");
		System.out.println("사용자 신고 총 건수 : "+totalCount);
	}

	//@Test
	public void TestGetAdminReportList() throws Exception {
		
		Search search = new Search();
		search.setLimit(3);
		search.setOffset(0);


		Map<String, Object> map = communityService.getAdminReportList(search, 1);
		List<Report> list = (List<Report>)map.get("list");
		
		System.out.println("총 신고 list 테스트 : "+list);	
		
		Integer totalCount = (Integer)map.get("totalCount");
		Integer unConfirmCount = (Integer)map.get("unConfirmCount");
		System.out.println("시스템 신고 총 건수 : "+totalCount);
		System.out.println("미처리 신고 건수 : "+unConfirmCount);
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
		search.setLimit(3);
		search.setOffset(0);

		Map<String, Object> map = communityService.getBlockedList(search, "user4");		
		List<CommunityLogs> list = (List<CommunityLogs>)map.get("list");
		
		System.out.println("차단 유저 list 테스트 : "+list);	
		
		Integer totalCount = (Integer)map.get("totalCount");
		System.out.println("사용자의 총 차단 수 : "+totalCount+"건");
	}
	
	//@Test
	public void TestGetBlockedUser() throws Exception {
		
		FollowBlock followBlock = communityService.getBlockedUser("user4", "user7");
		
		System.out.println("차단 유저 조회 테스트 : "+followBlock);	
	}
	
	//@Test
	public void TestDeleteBlockedUser() throws Exception {
		
		Search search = new Search();
		search.setLimit(3);
		search.setOffset(0);

		Map<String, Object> map = communityService.getBlockedList(search, "user4");		
		List<CommunityLogs> list = (List<CommunityLogs>)map.get("list");
		
		communityService.getBlockedList(search, "user4");
		
		communityService.deleteBlockedUser("user4", "user7");

		System.out.println("차단 유저 삭제 확인 : "+list);	
	}	
	
}
