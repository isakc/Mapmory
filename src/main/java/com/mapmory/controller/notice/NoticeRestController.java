package com.mapmory.controller.notice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mapmory.common.util.TextToImage;
import com.mapmory.services.notice.domain.Notice;
import com.mapmory.services.notice.service.NoticeService;


@RestController
@RequestMapping("/notice/*")
public class NoticeRestController {
	
	@Autowired
	@Qualifier("noticeServiceImpl")
	NoticeService noticeService;
	
	@Autowired
	TextToImage textToImage;
	
	@GetMapping("/rest/getDetailNotice/{noticeNo}")
	public Notice getDetailNotice(@PathVariable int noticeNo) throws Exception {
		System.out.println("getNotice json start......");

		Notice noti = noticeService.getDetailNotice(noticeNo);
		String noticeContent = noti.getNoticeText();
		
		String processedContent = textToImage.processImageTags(noticeContent);
		System.out.println("processendText :::::::::: " + processedContent);
        noti.setNoticeText(processedContent);

		return noti;
	}

}
