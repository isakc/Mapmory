package com.mapmory.controller.main;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.mapmory.common.domain.SessionData;
import com.mapmory.common.util.CookieUtil;
import com.mapmory.common.util.RedisUtil;

@Controller
public class MainController {
	
	@Autowired
	private RedisUtil<SessionData> redisUtil;
	
    @Value("${naver.client.id}")
	private String naverClientId;
	
	@Value("${naver.redirect.uri}")
	private String naverRedirectUri;
	
	@Value("${naver.state}")
	private String naverState;
	
	@Value("${google.client.id}")
	private String clientId;
	
	@Value("${google.client.secret}")
	private String clientSecret;
	
	@Value("${google.redirect.uri}")
	private String redirectUri;
	
	@Value("${google.state}")
	private String state;
	
	@Value("${google.scope}")
	private String scope;
	
	
	@GetMapping("/")
	public String index(HttpServletRequest request, Model model, HttpServletResponse response) throws IOException {
		
		// String requestURI = request.getRequestURI();
		
		javax.servlet.http.Cookie cookie = CookieUtil.findCookie("JSESSIONID", request);
		
		if(cookie != null) {
			
			/*
			System.out.println("=================MAIN :: GET JSESSIONID COOKIE====================");
			System.out.println("쿠키에 저장된 key name : " + cookie.getValue());
			System.out.println("남은 쿠키의 수명 : " + cookie.getMaxAge());
			System.out.println("쿠키에 설정된 domain : " + cookie.getDomain());
			System.out.println("쿠키에 설정된 path : " + cookie.getPath());
			System.out.println("쿠키에 설정된 이름 : " + cookie.getName());
			System.out.println("쿠키에 설정된 secure 상태 : " + cookie.getSecure());
			System.out.println("쿠키에 저장된 value : " + cookie.getValue());
			System.out.println("쿠키에 설정된 comment : " + cookie.getComment());
			System.out.println("=====================================");
			*/
			
			SessionData sessionData = redisUtil.getSession(request);
			
			if(sessionData == null) {

				/// 현재 '로그인 유지'가 무의미한 상황...
				System.out.println("현재 redis 로그인 버그로 인해 세션이 만료됨. 쿠키를 강제 제거합니다...");
				cookie.setMaxAge(0);
				cookie.setPath("/");
				response.addCookie(cookie);
				// response.sendRedirect("/");
				return "redirect:/";
			}
			
			System.out.println("login 상태... main으로 이동합니다.");
			if(sessionData.getRole() == 1)
				// response.sendRedirect("/map");
				return "redirect:/map";
			else
				// response.sendRedirect("/user/admin/getAdminMain");
				return "redirect:/user/admin/getAdminMain";
		
		} else {
			
			/*
			if( request.getCookies() != null) {
				
				SessionData temp = redisUtil.getSession(request);
				
				if(temp != null) {
					
					String userId = temp.getUserId();
					model.addAttribute("userId", userId);
					
				}
					
			}
			*/
			
			System.out.println("현재 로그인되지 않은 상태... login page로 이동합니다.");
			model.addAttribute("naver_client_id", naverClientId);
			model.addAttribute("naver_redirect_uri", naverRedirectUri);
			model.addAttribute("naver_state", naverState);
			
			/*
			model.addAttribute("google_client_id", clientId);
	        model.addAttribute("google_redirect_uri", redirectUri);
	        model.addAttribute("google_response_type", "code");
	        model.addAttribute("google_scope", scope); 
	        model.addAttribute("google_access_type", "offline");
	        model.addAttribute("google_prompt", "consent");
	        model.addAttribute("google_state", state);
			*/
		}
	
		
		return "index.html";
	}
	
	@GetMapping("/common/menu")
	public void getMenu(HttpServletRequest request, Model model) {

		SessionData sessionData = redisUtil.getSession(request);
	
		model.addAttribute("userId", sessionData.getUserId());		
	}
	
	// test용
	@GetMapping("/common/footer")
	public void getFooter(HttpServletRequest request, Model model) {
		
		SessionData sessionData = redisUtil.getSession(request);
		
		model.addAttribute("userId", sessionData.getUserId());
		
	}
	
	@GetMapping("/test")
	public String test() {
		return "/test.html";
	}
	
	@GetMapping("/common/toolBar")
	public void getToolBar() {
		
	}
}
