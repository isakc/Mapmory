package com.mapmory.services.user.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mapmory.services.user.dao.UserDao;
import com.mapmory.services.user.domain.User;
import com.mapmory.services.user.service.UserService;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	@Qualifier("userDao")
	private UserDao userDao;
	
	@Override
	public String getId(String userName, String email) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSocialId(int type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateRecoverAccount(String userId) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	
	
	
	@Override
	public Map<String, Object> getTermsAndConditionsList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getDetailTermsAndConditions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int addUser(User user) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public User getUser(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateUserInfo(User user) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateProfile(User user) {
		// TODO Auto-generated method stub
		return 0;
	}
}
