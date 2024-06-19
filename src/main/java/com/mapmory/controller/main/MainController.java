package com.mapmory.controller.main;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.mapmory.common.domain.SessionData;
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
	public String index(HttpServletRequest request, Model model) {
		
		if( request.getCookies() != null) {
			
			SessionData temp = redisUtil.getSession(request);
			
			if(temp != null) {
				
				String userId = temp.getUserId();
				model.addAttribute("userId", userId);
				
			}
				
		}
		
		model.addAttribute("naver_client_id", naverClientId);
		model.addAttribute("naver_redirect_uri", naverRedirectUri);
		model.addAttribute("naver_state", naverState);
		
		model.addAttribute("google_client_id", clientId);
        model.addAttribute("google_redirect_uri", redirectUri);
        model.addAttribute("google_response_type", "code");
        model.addAttribute("google_scope", scope); 
        model.addAttribute("google_access_type", "offline");
        model.addAttribute("google_prompt", "consent");
        model.addAttribute("google_state", state);
		
		return "index.html";
	}
	
	@GetMapping("/common/menu")
	public void getMenu() {
		
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
