package com.mapmory.unit.notice;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import com.mapmory.common.domain.Search;
import com.mapmory.services.notice.domain.Notice;
import com.mapmory.services.notice.service.NoticeService;

@SpringBootTest
public class NoticeServiceTest {
	
	@Autowired
	@Qualifier("noticeServiceImpl")
	private NoticeService noticeService;

	//@Test
	public void TestGetNoticeList() throws Exception{
		
		Search search = new Search();
		search.setCurrentPage(1);
		search.setPageSize(10);
		
		Map<String,Object> map = noticeService.getNoticeList(search);
		
		List<Object> list = (List<Object>)map.get("noticeList");
		
		System.out.println(list);
		
		Assert.assertEquals(10, list.size());
		
		Integer totalCount = (Integer)map.get("noticeTotalCount");
	 	System.out.println(totalCount);
		
	 	System.out.println("=======================================");
		
	}

	//@Test
	public void TestGetDetailNotice() throws Exception{
		
		Notice notice = new Notice();
		
		notice = noticeService.getDetailNotice(3);
		
		System.out.println(notice);
	}
	
	//@Test
	public void TestAddNotice() throws Exception {
		
		Notice notice = new Notice();
		notice.setNoticeTitle("테스트클래스 작성중입니다");
		notice.setNoticeText("테스트클래스 내용입니다.");
		notice.setUserId("admin");
		
		noticeService.addNotice(notice);
		
		notice = noticeService.getDetailNotice(11);
		
		System.out.println(notice);
	}
	
	//@Test
	public void TestUpdateNotice() throws Exception {
		
		
		Notice notice = noticeService.getDetailNotice(11);
		
		notice.setNoticeTitle("테스트클래스 업데이트 테스트");
		notice.setNoticeText("테스트클래스 업데이트 내용 테스트");
		
		noticeService.updateNoticeAndFaq(notice);
		
		noticeService.getDetailNotice(11);
	}
	
	//@Test
	public void TestDeleteNotice() throws Exception {

		noticeService.deleteNoticeAndFaq(13);
	}
	
	//@Test
	public void TestAddFaq() throws Exception {
		
		Notice notice = new Notice();
		
		notice.setNoticeTitle("테스트클래스 작성중입니다");
		notice.setNoticeText("테스트클래스 내용입니다.");
		notice.setUserId("admin");

		noticeService.addFaq(notice);
		
		notice = noticeService.getDetailNotice(17);
		
		System.out.println(notice);
	}
}
