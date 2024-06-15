package com.mapmory.services.user.service;

import com.mapmory.services.user.domain.SocialLoginInfo;
import com.mapmory.services.user.domain.User;

public interface UserServiceJM {

	public String getKakaoAccessToken(String authorize_code) throws Exception;
	
	public String getKakaoUserInfo(String access_Token) throws Exception;
	
	public String PhoneNumberCheck(String to) throws Exception;
	
	//소셜 로그인 아이디 유무
	public SocialLoginInfo socialLoginBySocialId(String socialId) throws Exception;
}
