package com.mapmory.services.user.service.impl;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mapmory.common.domain.SessionData;
import com.mapmory.common.util.RedisUtil;
import com.mapmory.services.user.domain.Login;
import com.mapmory.services.user.service.LoginService;

@Service
public class LoginServiceImpl implements LoginService {

	@Autowired
	private RedisUtil<SessionData> redisUtil;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	private static final long KEEP_LOGIN_DAY = 60 * 24 * 90;
	
	
	public boolean login(Login loginData, String savedPassword) throws Exception{
		
		if( loginData.getUserId().isEmpty() || loginData.getUserPassword().isEmpty()) {
			
			throw new Exception("아이디 또는 비밀번호가 비어있습니다.");
		} else {
			return passwordEncoder.matches(loginData.getUserPassword(),savedPassword);
		}
	}
	
	public boolean setSession(String userId, byte role, String sessionId, boolean keepLogin) {
		
		int isKeepLogin = (keepLogin ? 1 : 0);
		
		SessionData sessionData = SessionData.builder()
				.userId(userId)
				.role(role)
				.isKeepLogin(isKeepLogin)
				.build();
		
		if (keepLogin == false)
			return redisUtil.insert(sessionId, sessionData);
		
		else
			return redisUtil.insert(sessionId, sessionData, KEEP_LOGIN_DAY);
	}
	
	public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		Cookie[] cookies = request.getCookies();
		for(Cookie cookie : cookies ) {
			
			if(cookie.getName().equals("JSESSIONID")) {
				
				System.out.println("session을 제거합니다.");
				
				redisUtil.delete(cookie.getValue());
				
				System.out.println("cookie getPath : "+ cookie.getPath());
				
				cookie.setMaxAge(0);
				cookie.setPath("/");
				response.addCookie(cookie);
				response.sendRedirect("/");
			}
		}
	}
}