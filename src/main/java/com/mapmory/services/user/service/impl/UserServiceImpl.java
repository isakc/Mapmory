package com.mapmory.services.user.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mapmory.common.domain.Search;
import com.mapmory.services.user.dao.UserDao;
import com.mapmory.services.user.domain.LoginLog;
import com.mapmory.services.user.domain.SocialLoginInfo;
import com.mapmory.services.user.domain.User;
import com.mapmory.services.user.service.UserService;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	@Qualifier("userDao")
	private UserDao userDao;

	@Override
	public boolean addUser(User user) {
		// TODO Auto-generated method stub
		int result = userDao.insertUser(user);
		
		if (result == 1)
			return true;
		else
			return false;
	}

	@Override
	public User getDetailUser(String userId) {
		// TODO Auto-generated method stub
		User user = User.builder()
					.userId(userId)
					.build();
		
		return userDao.selectUser(user);
	}
	
	@Override
	public String getId(User user) {
		// TODO Auto-generated method stub
		
		User resultUser =  userDao.selectUser(user);
		return resultUser.getUserId();
	}

	@Override
	public boolean addSuspendUser(User user) {
		// TODO Auto-generated method stub
		
		return false;
	}

	@Override
	public boolean addSocialLoginLink(SocialLoginInfo socialLoginInfo) {
		// TODO Auto-generated method stub
		
		/* 예외 처리 사항
		 * 1. 동일한 업체의 소셜 계정을 overriding하는 경우 -> 기존 것을 제거 후 새 것으로 교체
		 */
		
		int socialIdLength = socialLoginInfo.getSocialId().length();
		
		if(socialIdLength == 10)
			socialLoginInfo.setSocialLoginInfoType(2);
		else if (socialIdLength == 21)
			socialLoginInfo.setSocialLoginInfoType(0);
		else
			socialLoginInfo.setSocialLoginInfoType(1);
		
		int result = userDao.insertSocialLoginLink(socialLoginInfo);

		if (result == 1)
			return true;
		else
			return false;
	}

	// deprecated
	@Override
	public String getSocialId(SocialLoginInfo socialLoginInfo) {
		// TODO Auto-generated method stub
		
		List<SocialLoginInfo> resultList = userDao.selectSocialIdList(socialLoginInfo.getUserId());
		
		for(SocialLoginInfo info : resultList) {
			
			if(info.getSocialLoginInfoType() == socialLoginInfo.getSocialLoginInfoType())
				return info.getSocialId();
		}
		
		return null;
	}

	@Override
	public Map<String, Object> getUserList(Search search) {
		// TODO Auto-generated method stub
		
		List<User> userList = userDao.selectUserList(search);
		int count = userDao.getUserListTotalCount(search);
		
		Map<String, Object> result = new HashMap<>();
		result.put("userList", userList);
		result.put("count", count);
		
		return result;
	}

	@Override
	public Map<String, Object> getFollowList(Search search) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<LoginLog> getUserStatistics() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean updateUser(User user) {
		// TODO Auto-generated method stub
		
		int result = userDao.updateUser(user);
		
		if (result == 1)
			return true;
		else
			return false;
	}

	@Override
	public boolean updateSuspendUser(String userId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateRecoverAccount(String userId) {
		// TODO Auto-generated method stub
		int result = userDao.updateRecoverAccount(userId);
		
		if (result == 1)
			return true;
		else
			return false;
	}

	@Override
	public boolean checkSecondaryAuth(String userId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean checkDuplication(User user) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean checkSocialId(SocialLoginInfo socialLoginInfo) {
		// TODO Auto-generated method stub
		
		List<SocialLoginInfo> resultList = userDao.selectSocialIdList(socialLoginInfo.getUserId());
		
		for (SocialLoginInfo info : resultList) {
			
			if(info.getSocialId().equals(socialLoginInfo.getSocialId()))
				return true;
		}
		return false;
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
}
