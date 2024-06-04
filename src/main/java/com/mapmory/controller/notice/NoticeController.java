package com.mapmory.controller.notice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.mapmory.common.domain.Page;
import com.mapmory.common.domain.Search;
import com.mapmory.services.notice.domain.Notice;
import com.mapmory.services.notice.service.NoticeService;

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

		@RequestMapping("/getNoticelist")
		public String getnoticeList(@ModelAttribute("Search") Search search,
										Model model)throws Exception
		{
			System.out.println("getNoticeList start......");
			if(search.getCurrentPage() == 0 ) {
				search.setCurrentPage(1);
			}
			
			search.setPageSize(pageSize);
			
			Map<String,Object> map = noticeService.getNoticeList(search);
			
			Page resultPage = new Page(search.getCurrentPage(),
					((Integer)map.get("totalCount"))
					.intValue(),pageUnit,pageSize);
			
			System.out.println(resultPage);
			
			model.addAttribute("noticeList",map.get("list"));
			model.addAttribute("resultPage",resultPage);
			model.addAttribute("search",search);
			return "forward:/notice/getNoticeList.html";
		}
		
		@PostMapping(value = "addNotice")
		public String addNotice(@ModelAttribute("Notice") Notice notice, 
                HttpSession session) throws Exception {
					System.out.println("addNotice Start...... ( POST )");

					String name = (String) session.getAttribute("name");
					System.out.println("name=" + name);
					
					noticeService.addNotice(notice);

					return "redirect:/notice/getNoticelist";
		}
		
		@GetMapping(value = "addNotice")
		public String addNotice() throws Exception{
			System.out.println("addNotice Start...... ( GET )");
			return "forward:/notice/addNotice.html";
		}
		
		@GetMapping(value = "getDetailNotice/{noticeNo}")
		public String getDetailNotice(@PathVariable int noticeNo,
								 Model model) throws Exception {
			System.out.println("getDetailNotice Start...... ");

			Notice notice = noticeService.getDetailNotice(noticeNo);
			model.addAttribute("noti", notice);
			System.out.println("notice 테스트용 == " + notice);

			return "forward:/notice/getDetailNotice.html";
		}
		
		@PostMapping(value = "updateNotice")
		public String updateNotice(@ModelAttribute("update") Notice notice) throws Exception{
			
			System.out.println("updateProduct Start......");
			noticeService.updateNoticeAndFaq(notice);
			
			return "forward:/notice/updateReadNotice.html";
		}
		
		@GetMapping(value = "updateNotice/{noticeNo}")
		public String updateNotice(@PathVariable int noticeNo,
									Model model) throws Exception{
			
			System.out.println("/updateProductView");
			Notice notice = noticeService.getDetailNotice(noticeNo);
			model.addAttribute("UpdateNotice", notice);
			
			return "forward:/notice/updateNotice.html";
		}
		
		@GetMapping(value = "delete/{noticeNo}")
		public void deleteNotice(@PathVariable int noticeNo,
									Model model) throws Exception {
			System.out.println("deleteNotice start......");
			noticeService.deleteNoticeAndFaq(noticeNo);

		}
}