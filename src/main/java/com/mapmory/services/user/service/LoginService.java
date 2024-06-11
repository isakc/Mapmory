package com.mapmory.services.user.service;

import org.springframework.stereotype.Service;

import com.mapmory.services.user.domain.Login;

@Service
public interface LoginService {

	
	public boolean login(Login loginData, String savedPassword) throws Exception;
	
	public boolean insertSessionInRedis(Login loginData, byte role, String sessionId);
}