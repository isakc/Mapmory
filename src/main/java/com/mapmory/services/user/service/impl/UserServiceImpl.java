package com.mapmory.services.user.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mapmory.common.domain.Search;
import com.mapmory.services.user.dao.UserDao;
import com.mapmory.services.user.domain.FollowBlock;
import com.mapmory.services.user.domain.FollowMap;
import com.mapmory.services.user.domain.LoginLog;
import com.mapmory.services.user.domain.SocialLoginInfo;
import com.mapmory.services.user.domain.SuspensionLog;
import com.mapmory.services.user.domain.User;
import com.mapmory.services.user.service.UserService;

@Service("userServiceImpl")
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	@Qualifier("userDao")
	private UserDao userDao;

	@Override
	public boolean addUser(String userId, String userPassword, String userName, String nickname, LocalDate birthday, int sex, String email, String phoneNumber) {
		// TODO Auto-generated method stub
		User user = User.builder()
						.userId(userId)
						.userPassword(userPassword)
						.userName(userName)
						.nickname(nickname)
						.birthday(birthday)
						.sex(sex)
						.email(email)
						.phoneNumber(phoneNumber)
						.build();
		
		int result = userDao.insertUser(user);
		
		return intToBool(result);
	}

	// 먼저 조회 로직을 구상한 후에, count 로직을 만들어서 정책에 따라 처리해야 한다.
	@Override
	public boolean addSuspendUser(String userId) {
		// TODO Auto-generated method stub
		
		/*
		User user = User.builder()
					.userId(userId)
					.endSuspensionDate();
					.build();
		
		int result = userDao.updateUser(user);
		
		return intToBool(result);
		*/
		
		return false;
	}

	@Override
	public boolean addSocialLoginLink(String userId, String socialId) {
		// TODO Auto-generated method stub
		
		/* 예외 처리 사항
		 * 1. 동일한 업체의 소셜 계정을 overriding하는 경우 -> 기존 것을 제거 후 새 것으로 교체
		 */
		
		int socialIdLength = socialId.length();
		
		Integer type = null;
		if(socialIdLength == 10)
			type = 2;
		else if (socialIdLength == 21)
			type = 0;
		else
			type = 1;
		
		SocialLoginInfo socialLoginInfo = SocialLoginInfo.builder()
								.userId(userId)
								.socialLoginInfoType(type)
								.socialId(socialId)
								.build();
		
		int result = userDao.insertSocialLoginLink(socialLoginInfo);

		return intToBool(result);
	}
	
	@Override
	public boolean addLeaveAccount(String userId) {
		// TODO Auto-generated method stub
		
		User user = User.builder()
					.userId(userId)
					.leaveAccountDate(LocalDateTime.now())
					.build();
		
		int result = userDao.updateUser(user);
		
		return intToBool(result);
	}
	
	@Override
	public boolean addLoginLog(String userId) {
		// TODO Auto-generated method stub
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
	public String getId(String userName, String email) {
		// TODO Auto-generated method stub
		
		User user = User.builder()
					.userName(userName)
					.email(email)
					.build();
		User resultUser =  userDao.selectUser(user);
		return resultUser.getUserId();
	}

	@Deprecated
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
	public List<FollowMap> getFollowList(String userId, String searchKeyword, int currentPage, int limit) {
		// TODO Auto-generated method stub
		
		Search search = Search.builder()
						.userId(userId)
						.searchKeyword(searchKeyword)
						.currentPage(currentPage)
						.limit(limit)
						.build();
		
		return userDao.selectFollowList(search);
	}

	@Override
	public List<LoginLog> getUserLoginList(Search search) {
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
	public List<SuspensionLog> getSuspensionLog(String userId) {
		// TODO Auto-generated method stub
		
		
		
		return null;
	}

	@Override
	public boolean updateSuspendUser(String userId) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean updateUserInfo(String userId, String userName, String nickname, LocalDate birthday, Integer sex, String email, String phoneNumber) {
		// TODO Auto-generated method stub
		
		
		User user = User.builder()
				.userId(userId)
				.userName(userName)
				.nickname(nickname)
				.birthday(birthday)
				.sex(sex)
				.email(email)
				.phoneNumber(phoneNumber)
				.build();
		
		int result = userDao.updateUser(user);
		
		return intToBool(result);
	}

	@Override
	public boolean updateProfile(String userId, String profileImageName, String introduction) {
		// TODO Auto-generated method stub
		
		User user = User.builder()
						.userId(userId)
						.profileImageName(profileImageName)
						.introduction(introduction)
						.build();
		
		int result = userDao.updateUser(user);
		
		return intToBool(result);
	}

	@Override
	public boolean updatePassword(String userId, String userPassword) {
		// TODO Auto-generated method stub
		
		User user = User.builder()
						.userId(userId)
						.userPassword(userPassword)
						.build();
		
		int result = userDao.updateUser(user);
		
		return intToBool(result);
	}

	@Override
	public boolean updateSecondaryAuth(String userId) {
		// TODO Auto-generated method stub
		
		
		
		return false;
	}	

	@Override
	public int updateRecoverAccount(String userId) {
		// TODO Auto-generated method stub
		
		User user = User.builder()
					.userId(userId)
					.build();
		
		LocalDate leaveAccountDate = userDao.selectUser(user).getLeaveAccountDate().toLocalDate();
		
		// 변경 일자가 오늘로부터 1개월이 지난 경우면 진행 불가능.
		if( leaveAccountDate.isBefore( LocalDate.now().minusMonths(1) ) ) {
			return 2;
		} else {
			int result = userDao.updateRecoverAccount(userId);
			
			if (result == 1)
				return 1;
			else
				return 0;
		}
	}

	@Override
	public boolean updateHideProfile(String userId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteFollow(String userId, String targetId) {
		// TODO Auto-generated method stub
		
		FollowBlock follow = FollowBlock.builder()
						.userId(userId)
						.targetId(targetId)
						.build();
		
		int result = userDao.deleteFollow(follow);
		
		return intToBool(result);
	}
	
	@Override
	public boolean checkSecondaryAuth(String userId) {
		// TODO Auto-generated method stub
		
		User user = User.builder()
						.userId(userId)
						.build();
						
		Byte result = userDao.selectUser(user).getSetSecondaryAuth();
		
		return intToBool(result);
	}

	@Override
	public boolean checkDuplicationById(String userId) {
		// TODO Auto-generated method stub
		
		User user = User.builder()
						.userId(userId)
						.build();
		
		int result = userDao.checkDuplication(user);
		
		return intToBool(result);
	}
	
	@Override
	public boolean checkDuplicationByNickname(String nickname) {
		// TODO Auto-generated method stub
		
		User user = User.builder()
						.nickname(nickname)
						.build();
		
		int result = userDao.checkDuplication(user);
		
		return intToBool(result);
	}
	
	@Override
	public boolean checkSocialId(String userId, String socialId) {
		// TODO Auto-generated method stub
		
		List<SocialLoginInfo> resultList = userDao.selectSocialIdList(userId);
		
		for (SocialLoginInfo info : resultList) {
			
			if(info.getSocialId().equals(socialId))
				return true;
		}
		return false;
	}
	
	@Override
	public boolean checkPasswordChangeDeadlineExceeded(String userId) {
		// TODO Auto-generated method stub
		
		User user = User.builder()
						.userId(userId)
						.build();
		
		LocalDate updatedPasswordDate = userDao.selectUser(user).getUpdatePasswordDate().toLocalDate();

		if( updatedPasswordDate.isBefore( LocalDate.now().minusMonths(3) ) ) {
			return false;
		} else {
			return true;
		}
	}	

	@Override
	public boolean checkHideProfile(String userId) {
		// TODO Auto-generated method stub
		
		User user = User.builder()
						.userId(userId)
						.build();
		
		Byte result = userDao.selectUser(user).getHideProfile();
		
		return intToBool(result);
	}
	
	
	@Override
	public Map<String, Object> getTermsAndConditionsList() {
		// TODO Auto-generated method stub
		// 이용약관은 file io 로 처리
		
		return null;
	}

	@Override
	public Object getDetailTermsAndConditions() {
		// TODO Auto-generated method stub
		// 이용약관은 file io 로 처리
		
		return null;
	}


	private boolean intToBool(int result) {
		
		if(result == 1)
			return true;
		else
			return false;
	}


}
