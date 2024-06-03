package com.mapmory.unit.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import com.mapmory.services.community.domain.Reply;
import com.mapmory.services.community.service.CommunityService;

@SpringBootTest
public class CommunityServiceTest {

	@Autowired
	@Qualifier("communityServiceImpl")
	private CommunityService communityService;
	
	//@Test
	public void TestAddReply() throws Exception {
//		Reply reply = new Reply();
//		
//		reply.setRecordNo(1);
//		reply.setUserId("user1");
//		reply.setReplyText("반갑습니다.");
//		reply.setReplyImageName(null);
//		
//		communityService.addReply(reply);

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
	
	@Test
	public void TestUpdateReply() throws Exception {
		
		Reply reply = communityService.getReply(13);
		
		reply.setReplyText("반가워유.");
		reply.setReplyImageName("HiHi.jpg");
		
		communityService.updateReply(reply);
		
		System.out.println("update 테스트 : "+reply);	
	}
	
}
