package com.mapmory.services.user.service;

import java.util.Map;

import com.mapmory.services.user.domain.User;

public interface UserService {
	
	/*
	 * List와 count를 Map으로 같이 묶어서 controller에게 전달할 것
	 */
	public int addUser(User user);

	public User getUser(String userId);
	
	public int updateUserInfo(User user);
	
	public int updateProfile(User user);
	
	public int updateRecoverAccount(String userId);
	
	
	
	
	
	
	public String getId(String userName, String email);
	
	// 0: google, 1: naver, 2: kakao
	public String getSocialId(int type);
	

	
	
	public Map<String, Object> getTermsAndConditionsList();
	
	// file io 로 처리할 예정
	public Object getDetailTermsAndConditions();
}
