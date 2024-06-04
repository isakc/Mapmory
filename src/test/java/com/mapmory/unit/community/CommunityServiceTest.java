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
import com.mapmory.services.community.service.CommunityService;

@SpringBootTest
public class CommunityServiceTest {

	@Autowired
	@Qualifier("communityServiceImpl")
	private CommunityService communityService;
	
	//@Test
	public void TestAddReply() throws Exception {

		Reply reply = Reply.builder()
				.recordNo(2)
				.userId("user2")
				.replyText("재밌다")
				.build();
		
		communityService.addReply(reply);
		
		System.out.println("add 테스트 : " +reply);
	}	
	
	//@Test
	public void TestGetReply() throws Exception {
		
		Reply reply = new Reply();
		
		reply = communityService.getReply(13);
		
		System.out.println("get 테스트 : "+reply);
	}	
	
	//@Test
	public void TestUpdateReply() throws Exception {
		
		Reply reply = communityService.getReply(13);
		
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
	public void TestAddBookmarkSharedRecord() throws Exception {

		CommunityLogs communityLogs = CommunityLogs.builder()
				.userId("user7")
				.recordNo(4)
				.logsType(2)
				.build();
				
		communityService.addBookmarkSharedRecord(communityLogs);
		
		System.out.println("즐겨찾기 결과 : "+communityLogs);
	}

	//@Test
	public void TestDeleteBookmarkSharedRecord() throws Exception {

		communityService.deleteBookmarkSharedRecord("user7", 4);
		
		System.out.println("delete 테스트 : ");	
	}	
	
	

}
