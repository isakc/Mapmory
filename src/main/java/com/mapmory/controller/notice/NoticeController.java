package com.mapmory.controller.notice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.mapmory.common.domain.Search;
import com.mapmory.services.notice.domain.Notice;
import com.mapmory.services.notice.service.NoticeService;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/notice/*")
public class NoticeController {
    //Field
    @Autowired
    @Qualifier("noticeServiceImpl")
    private NoticeService noticeService;

    public NoticeController() {
        System.out.println(this.getClass());
    }

    @Value("${page.Unit}")
    private int pageUnit;

    @Value("${page.Size}")
    private int pageSize;

    @GetMapping("/getNoticeList")
    public String getNoticeList(@ModelAttribute("search") Search search, Model model) throws Exception {
    	Notice notice = new Notice();
    	
    	if(search.getCurrentPage() == 0) {
			search.setCurrentPage(1);
		}
		
		search.setPageSize(pageSize);
		
        Map<String, Object> map = noticeService.getNoticeList(search);
        List<Notice> noticeList = (List<Notice>) map.get("noticeList");
        
        int totalCount = (int) map.get("noticeTotalCount");
        
        System.out.println("noticetList =====" + noticeList);

        model.addAttribute("noticeList", noticeList);
        model.addAttribute("search", search);
        model.addAttribute("totalCount", totalCount);
        
        return "notice/getNoticeOrFaqList";
    }
    
    @GetMapping("/getFaqList")
    public String getFaqList(@ModelAttribute("search") Search search, Model model) throws Exception {
    	
    	if(search.getCurrentPage() == 0) {
			search.setCurrentPage(1);
		}
		
		search.setPageSize(pageSize);
		
        Map<String, Object> map = noticeService.getFaqList(search);
        List<Notice> noticeList = (List<Notice>) map.get("noticeList");
        
        int totalCount = (int) map.get("noticeTotalCount");
        
        System.out.println("noticetList =====" + noticeList);

        model.addAttribute("noticeList", noticeList);
        model.addAttribute("search", search);
        model.addAttribute("totalCount", totalCount);

        return "notice/getNoticeOrFaqList";
    }

    @PostMapping("/addNoticeOrFaq")
    public String addNoticeOrFaq(@ModelAttribute("Notice") Notice notice,
                                 HttpSession session) throws Exception {
        System.out.println("addNoticeOrFaq Start...... (POST)");
        noticeService.addNoticeOrFaq(notice);
        if (notice.getNoticeType() == 0) {
            return "redirect:/notice/getNoticeList";
        } else {
            return "redirect:/notice/getFaqList";
        }
    }

    @GetMapping("/addNoticeOrFaq")
    public String addNoticeOrFaq() throws Exception {
        System.out.println("addNoticeOrFaq Start...... (GET)");
        return "notice/admin/addNoticeOrFaq";
    }

    @GetMapping("/getDetailNotice/{noticeNo}")
    public String getDetailNotice(@PathVariable int noticeNo,
                                  Model model) throws Exception {
        System.out.println("getDetailNotice Start...... ");
        Notice notice = noticeService.getDetailNotice(noticeNo);
        model.addAttribute("notice", notice);
        System.out.println("notice 테스트용 == " + notice);
        return "notice/admin/getAdminDetailNoticeOrFaq";
    }

    @PostMapping("/updateNoticeOrFaq/{noticeNo}")
    public String updateNoticeOrFaq(@ModelAttribute("update") Notice notice,
    								@PathVariable int noticeNo) throws Exception {
        System.out.println("updateNoticeOrFaq Start......");
        
        noticeService.updateNoticeAndFaq(notice);
        notice = noticeService.getDetailNotice(noticeNo);
        
        if(notice.getNoticeType() == 0) {
        	return "redirect:/notice/getNoticeList";
        } else {
        	return "redirect:/notice/getFaqList";
        }
    }

    @GetMapping("/updateNoticeOrFaq/{noticeNo}")
    public String updateNoticeOrFaq(@PathVariable int noticeNo,
                                    Model model) throws Exception {
        System.out.println("/updateNoticeOrFaqView");
        Notice notice = noticeService.getDetailNotice(noticeNo);
        model.addAttribute("notice", notice);
        return "notice/admin/updateNoticeOrFaqe";
    }

    @GetMapping("deleteNoticeOrFaq/{noticeNo}")
    public String deleteNoticeOrFaq(@PathVariable int noticeNo) throws Exception {
        System.out.println("deleteNoticeOrFaq start......");
        
        Notice notice = noticeService.getDetailNotice(noticeNo);
        
        noticeService.deleteNoticeAndFaq(noticeNo);
        
        System.out.println("삭제전 문제 테스트 ::::: " + notice);
        
        if (notice.getNoticeType() == 0) {
            return "redirect:/notice/getNoticeList";
        } else {
            return "redirect:/notice/getFaqList";
        }
    }
}