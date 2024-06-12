package com.mapmory.services.user.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.mapmory.common.domain.SessionData;
import com.mapmory.services.user.domain.Login;

@Service
public interface LoginService {

	
	public boolean login(Login loginData, String savedPassword) throws Exception;
	
	public boolean insertSession(Login loginData, byte role, String sessionId);
	
	public SessionData getSession(HttpServletRequest request);
}
