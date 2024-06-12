package com.mapmory.services.user.service;

import com.mapmory.services.user.domain.User;

public interface UserServiceJM {

	public String getKakaoAccessToken(String authorize_code) throws Exception;
	
	public String getKakaoUserInfo(String access_Token) throws Exception;
	
	public String PhoneNumberCheck(String to) throws Exception;
	
	public User findByUserId(String userId) throws Exception;
}
