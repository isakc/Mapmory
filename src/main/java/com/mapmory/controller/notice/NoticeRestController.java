package com.mapmory.controller.notice;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mapmory.common.domain.Page;
import com.mapmory.common.domain.Search;
import com.mapmory.common.util.TextToImage;
import com.mapmory.services.notice.domain.Notice;
import com.mapmory.services.notice.service.NoticeService;


@RestController
@RequestMapping("/rest/notice/*")
public class NoticeRestController {
	
	@Autowired
	@Qualifier("noticeServiceImpl")
	NoticeService noticeService;
	
	@Autowired
	TextToImage textToImage;
	
	@Value("${page.Size}")
    private int pageSize;
	
	@Value("${page.Unit}")
	private int pageUnit;
	
	@GetMapping("/getDetailNotice/{noticeNo}")
	public Notice getDetailNotice(@PathVariable int noticeNo) throws Exception {
		System.out.println("getNotice json start......");

		Notice noti = noticeService.getDetailNotice(noticeNo);
		String noticeContent = noti.getNoticeText();
		
		String processedContent = textToImage.processImageTags(noticeContent);
		System.out.println("processendText :::::::::: " + processedContent);
        noti.setNoticeText(processedContent);

		return noti;
	}
	
	@GetMapping("/getNoticeList")
    public ResponseEntity<?> getNoticeList(@ModelAttribute("search") Search search) throws Exception {
        if (search.getCurrentPage() == 0) {
            search.setCurrentPage(1);
        }

        search.setPageSize(pageSize);

        Map<String, Object> map = noticeService.getNoticeList(search);
        
        Page resultPage = new Page(search.getCurrentPage(), ((Integer) map.get("noticeTotalCount")).intValue(), pageUnit, pageSize);

        System.out.println("noticeList =====" + map.get("noticeList"));

        return ResponseEntity.ok(Map.of(
            "noticeList", map.get("noticeList"),
            "search", search,
            "resultPage", resultPage
        ));
    }

	@GetMapping("/getFaqList")
	public ResponseEntity<?> getFaqList(@ModelAttribute("search") Search search) throws Exception {
	    if (search.getCurrentPage() == 0) {
	        search.setCurrentPage(1);
	    }
	    search.setPageSize(pageSize);
	    
	    Map<String, Object> map = noticeService.getFaqList(search);
	    
	    Page resultPage = new Page(search.getCurrentPage(), ((Integer) map.get("FaqTotalCount")).intValue(), pageUnit, pageSize);
	    
	    System.out.println("FaqList =====" + map.get("FaqList"));
	    
	    return ResponseEntity.ok(Map.of(
	        "FaqList", map.get("FaqList"),
	        "search", search,
	        "resultPage", resultPage
	    ));
	}
	
	

}
