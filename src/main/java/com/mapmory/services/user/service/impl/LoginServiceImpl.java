package com.mapmory.services.user.service.impl;

import javax.servlet.http.HttpServletRequest;

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
	
	/**
	 * 비밀번호가 일치하면 true, 일치하지 않으면 false
	 */
	public boolean login(Login loginData, String savedPassword) throws Exception{
		
		if( loginData.getUserId().isEmpty() || loginData.getUserPassword().isEmpty()) {
			
			throw new Exception("아이디 또는 비밀번호가 비어있습니다.");
		} else {
			return passwordEncoder.matches(loginData.getUserPassword(),savedPassword);
		}
	}
	
	public boolean insertSession(Login loginData, byte role, String sessionId) {
		
		SessionData sessionData = SessionData.builder()
				.userId(loginData.getUserId())
				.role(role)
				.build();
		
		return redisUtil.insert(sessionId, sessionData);
	}
	
	/*
	public boolean logout(String a) {
		
		
	}
	*/
	
	public SessionData getSession(HttpServletRequest request) {
		
		return redisUtil.getSession(request);
	}
}
