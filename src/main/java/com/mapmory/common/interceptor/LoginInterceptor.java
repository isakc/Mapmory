package com.mapmory.common.interceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.HandlerInterceptor;

import com.mapmory.common.domain.SessionData;
import com.mapmory.common.util.RedisUtil;
import com.mapmory.services.user.domain.Login;
import com.mapmory.services.user.service.LoginService;

public class LoginInterceptor implements HandlerInterceptor {

	
	@Value("${mapmory.user.login}")
	private String loginPath;
	
	@Autowired
	private RedisUtil<SessionData> redisUtil;
	
	@Autowired
	private LoginService loginService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// TODO Auto-generated method stub
		
		String requestURI = request.getRequestURI();
		System.out.println("requestURI : " + requestURI);
		
		Cookie[] cookies = request.getCookies();
		for(Cookie cookie : cookies ) {
			
			if(cookie.getName().equals("JSESSIONID")) {
				
				String sessionId = cookie.getValue();
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
	}
}
