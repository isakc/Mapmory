package com.mapmory.services.user.service;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.mapmory.services.user.domain.Login;

@Service
public interface LoginService {

	/**
	 * 비밀번호가 일치하면 true, 그렇지 않으면 false
	 * @param loginData
	 * @param savedPassword
	 * @return
	 * @throws Exception
	 */
	public boolean login(Login loginData, String savedPassword) throws Exception;
	
	public void acceptLogin(String userId, byte role, HttpServletResponse response, boolean keep) throws Exception;
	
	/**
	 * 
	 * @param loginData
	 * @param role
	 * @param sessionId
	 * @param keepLogin  : 로그인 유지 여부
	 * @return
	 */
	public boolean setSession(String userId, byte role, String sessionId , boolean keepLogin);
	
	public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
