package com.mapmory.controller.main;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.mapmory.common.domain.SessionData;
import com.mapmory.common.util.RedisUtil;

@Controller
public class MainController {
	
	@Autowired
	private RedisUtil<SessionData> redisUtil;
	
	@GetMapping("/")
	public String index(HttpServletRequest request, Model model) {
		
		if( request.getCookies() != null) {
			
			SessionData temp = redisUtil.getSession(request);
			
			if(temp != null) {
				
				String userId = temp.getUserId();
				model.addAttribute("userId", userId);
				
			}
				
		}
		
		return "index.html";
	}
	
	@GetMapping("/common/menu")
	public void getMenu() {
		
	}
	
	// test용
	@GetMapping("/test")
	public void getFooter() {
		
	}
}
