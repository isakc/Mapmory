package com.mapmory.services.user.service;

import java.util.Map;

import com.mapmory.services.user.domain.User;

public interface UserService {
	
	/*
	 * List의 경우, Map으로 요소 리스트와 총 개수를 담아서 넘긴다.
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
	
	// file io 처리하는 객체는 어떻게 정의할까
	public Object getDetailTermsAndConditions();
}
