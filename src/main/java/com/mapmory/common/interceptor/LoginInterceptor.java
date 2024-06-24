package com.mapmory.common.interceptor;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.web.servlet.HandlerInterceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mapmory.common.domain.SessionData;
import com.mapmory.common.util.CookieUtil;
import com.mapmory.common.util.RedisUtil;
import com.mapmory.services.user.service.LoginService;
import com.mapmory.services.user.service.UserService;

public class LoginInterceptor implements HandlerInterceptor {

	
	@Value("${mapmory.user.login}")
	private String loginPath;
	
	@Autowired
	private RedisUtil<SessionData> redisUtil;
	
	@Autowired
	private LoginService loginService;
	
	@Autowired
	private UserService userService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		/*
		String requestURI = request.getRequestURI();
		System.out.println("requestURI = " + requestURI);
		if(requestURI.equals("/") || requestURI.equals("/user/rest/login"))
			return true;
		*/
		
		String requestURI = request.getRequestURI();
		System.out.println("requestURI = " + requestURI);
			
		//////////////////////////////////////////////////////////
		///////////////////////// whitelist ///////////////////////
		////////////////////////////////////////////////////////
		if(requestURI.equals("/user/getSecondaryAuthView")) {
			
			return true;
			
		}
		
		// 이 메소드는 SECONDAUTH 쿠키가 만료되었을 때 로그인 페이지로 보내기 위해 필요함.
		/* login 페이지에서 getSEcondary Auth로 넘어오는 찰나의 시점에는 cookie가 생성이 안됨.
		if(requestURI.equals("/user/rest/generateKey")) {
			
			Cookie cookie = CookieUtil.findCookie("SECONDAUTH", request);
			if(cookie == null) {
				
				response.setContentType("application/json");
	            response.setCharacterEncoding("UTF-8");
	            
	            Map<String, String> jsonResponse = new HashMap<>();
	            jsonResponse.put("message", "no-cookie");
	            
	            ObjectMapper objectMapper = new ObjectMapper();
	            String json = objectMapper.writeValueAsString(jsonResponse);
	            
	            response.getWriter().write(json);
				return false;
				
			} else {
				return true;
			}
		}
		*/
		
		// 세션 연장 임시 조치
		// 현재 여전히 타임아웃 시 cookie는 살아있고 세션은 죽는 문제 존재. cookie만 살아 있는 경우, cookie를 제거해주는 로직 필요
		Cookie cookie = CookieUtil.findCookie("JSESSIONID", request);
		
		if(cookie == null) {

			System.out.println("쿠키가 만료되었어요. 다시 로그인해주세요...");
			response.sendRedirect("/");
			return false;
		} else {
			
			System.out.println("현재 login 상태입니다.");
			String sessionKeyName = cookie.getValue(); 
			SessionData sessionData = redisUtil.select(sessionKeyName, SessionData.class);
			
			// key가 만료된 경우
			if(sessionData == null) {

				System.out.println("현재 redis 버그로 인해 key가 사라짐. 강제로 cookie를 제거합니다...");
				cookie.setMaxAge(0);
				cookie.setPath("/");
				response.addCookie(cookie);
				response.sendRedirect("/");
				return false;
				
				/*
				System.out.println("현재 redis 버그로 인해 key가 사라짐. 강제로 redis에 키를 재생성합니다...");
				String key = cookie.getValue();
				loginService.setSession();
				*/
				
			} else {
			
				// 사용자가 관리자 페이지로 오면 거부
				if(requestURI.contains("Admin") && (sessionData.getRole() == 1)) {
					
					System.out.println("당신은 관리자가 아닙니다...");
					response.sendRedirect("/");
					return false;
				}
				
				
				// 세션을 연장한다.
				boolean result = redisUtil.updateSession(request, response);
				System.out.println("is session update successfully? : " + result);
				
				/*
				if(requestURI.equals("/")) {
					
					System.out.println("login 상태... main으로 이동합니다.");
					if(sessionData.getRole() == 1)
						response.sendRedirect("/map");
					else
						response.sendRedirect("/user/admin/getAdminMain");
				}
				*/
				
				return result;
				
			}
				
		}
		

		// TODO Auto-generated method stub
		/*
		String requestURI = request.getRequestURI();
		System.out.println("requestURI : " + requestURI);
		
		/// white list
		if(requestURI.equals("/") || requestURI.equals("/login")) {
			
			// 정지된 사용자의 경우에는 로그인 유지할 쿠키를 제거해서 재접속 불가능하게 만듦.
			Cookie[] cookies = request.getCookies();
			
			if(cookies != null ) {
				
				for(Cookie cookie : cookies ) {
					
					if(cookie.getName().equals("JSESSIONID")) {
						
						String sessionId = cookie.getValue();
						System.out.println("sessionId : " + sessionId);
						SessionData temp = redisUtil.select(sessionId, SessionData.class);
						if( temp != null ) {
							
							// 정지된 사용자의 경우에는 로그인 유지할 쿠키를 제거해서 재접속 불가능하게 만듦.
							if(userService.checkSuspended(temp.getUserId()).equals("true")) {
								
								Cookie deleteCookie = CookieUtil.createCookie("JSESSIONID", "");
								
								response.addCookie(deleteCookie);
								response.sendRedirect("/");
							}

							response.sendRedirect(loginPath);
							return false;
						}
						
						redisUtil.update(sessionId, temp);
						return true;
					}
				}
			}
			
			
			
			
			return true;
		}
			
		
		Cookie[] cookies = request.getCookies();
		if(cookies == null) {
			
			System.out.println("로그인 쿠키가 존재하지 않습니다.");
			response.sendRedirect(loginPath);
			
			return false;
		}
		
		for(Cookie cookie : cookies ) {
			
			if(cookie.getName().equals("JSESSIONID")) {
				
				String sessionId = cookie.getValue();
				System.out.println("sessionId : " + sessionId);
				SessionData temp = redisUtil.select(sessionId, SessionData.class);
				if( temp == null ) {
					
					System.out.println("세션이 만료되었습니다.");
					response.sendRedirect(loginPath);
					return false;
				}
				
				redisUtil.update(sessionId, temp);
				return true;
			}
		}
		
		System.out.println("로그인된 사용자가 아닙니다.");
		response.sendRedirect(loginPath);
		
		return false;
		*/

	}
}
