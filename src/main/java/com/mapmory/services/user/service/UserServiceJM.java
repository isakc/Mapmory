package com.mapmory.services.user.service;

import com.mapmory.services.call.domain.UserJM;
import com.mapmory.services.user.domain.User;

public interface UserServiceJM {

	public String getKakaoAccessToken(String authorize_code) throws Exception;
	
	public String getKakaoUserInfo(String access_Token) throws Exception;
	
	public String PhoneNumberCheck(String to) throws Exception;
	
	public void updateOnlineStatus(String userId, boolean isOnline) throws Exception;
	
	public UserJM findByUserId(String userId) throws Exception;
}
