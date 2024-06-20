package com.mapmory.common.interceptor;
//
//import javax.servlet.http.Cookie;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
//
//import com.mapmory.common.domain.SessionData;
//import com.mapmory.common.util.CookieUtil;
//import com.mapmory.common.util.RedisUtil;
//import com.mapmory.services.user.service.LoginService;
//import com.mapmory.services.user.service.UserService;
//
public class LoginInterceptor implements HandlerInterceptor {
//
//	
//	@Value("${mapmory.user.login}")
//	private String loginPath;
//	
//	@Autowired
//	private RedisUtil<SessionData> redisUtil;
//	
//	@Autowired
//	private LoginService loginService;
//	
//	@Autowired
//	private UserService userService;
//	
//	@Override
//	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
//			throws Exception {
//		
//		/*
//		String requestURI = request.getRequestURI();
//		System.out.println("requestURI = " + requestURI);
//		if(requestURI.equals("/") || requestURI.equals("/user/rest/login"))
//			return true;
//		*/
//		
//		
//		// 세션 연장 임시 조치
//		// 현재 여전히 타임아웃 시 cookie는 살아있고 세션은 죽는 문제 존재. cookie만 살아 있는 경우, cookie를 제거해주는 로직 필요
//		Cookie cookie = CookieUtil.findCookie("JSESSIONID", request);
//		
//		if(cookie == null) {
//
//			// System.out.println("쿠키가 만료되었어요. 다시 로그인해주세요...");
//			response.sendRedirect("/");
//			return false;
//		}
//			
//			
//		
//		else {
//			
//			String sessionKeyName = cookie.getValue(); 
//			SessionData sessionData = redisUtil.select(sessionKeyName, SessionData.class);
//			
//			if(sessionData == null) {
//		
//				cookie.setMaxAge(0);
//				cookie.setPath("/");
//				response.addCookie(cookie);
//				response.sendRedirect("/");
//				return false;
//				
//			} else {
//			
//				return redisUtil.updateSession(request, response);
//				
//			}
//				
//		}
//		
//
//		// TODO Auto-generated method stub
//		/*
//		String requestURI = request.getRequestURI();
//		System.out.println("requestURI : " + requestURI);
//		
//		/// white list
//		if(requestURI.equals("/") || requestURI.equals("/login")) {
//			
//			// 정지된 사용자의 경우에는 로그인 유지할 쿠키를 제거해서 재접속 불가능하게 만듦.
//			Cookie[] cookies = request.getCookies();
//			
//			if(cookies != null ) {
//				
//				for(Cookie cookie : cookies ) {
//					
//					if(cookie.getName().equals("JSESSIONID")) {
//						
//						String sessionId = cookie.getValue();
//						System.out.println("sessionId : " + sessionId);
//						SessionData temp = redisUtil.select(sessionId, SessionData.class);
//						if( temp != null ) {
//							
//							// 정지된 사용자의 경우에는 로그인 유지할 쿠키를 제거해서 재접속 불가능하게 만듦.
//							if(userService.checkSuspended(temp.getUserId()).equals("true")) {
//								
//								Cookie deleteCookie = CookieUtil.createCookie("JSESSIONID", "");
//								
//								response.addCookie(deleteCookie);
//								response.sendRedirect("/");
//							}
//
//							response.sendRedirect(loginPath);
//							return false;
//						}
//						
//						redisUtil.update(sessionId, temp);
//						return true;
//					}
//				}
//			}
//			
//			
//			
//			
//			return true;
//		}
//			
//		
//		Cookie[] cookies = request.getCookies();
//		if(cookies == null) {
//			
//			System.out.println("로그인 쿠키가 존재하지 않습니다.");
//			response.sendRedirect(loginPath);
//			
//			return false;
//		}
//		
//		for(Cookie cookie : cookies ) {
//			
//			if(cookie.getName().equals("JSESSIONID")) {
//				
//				String sessionId = cookie.getValue();
//				System.out.println("sessionId : " + sessionId);
//				SessionData temp = redisUtil.select(sessionId, SessionData.class);
//				if( temp == null ) {
//					
//					System.out.println("세션이 만료되었습니다.");
//					response.sendRedirect(loginPath);
//					return false;
//				}
//				
//				redisUtil.update(sessionId, temp);
//				return true;
//			}
//		}
//		
//		System.out.println("로그인된 사용자가 아닙니다.");
//		response.sendRedirect(loginPath);
//		
//		return false;
//		*/
//
//	}
}
