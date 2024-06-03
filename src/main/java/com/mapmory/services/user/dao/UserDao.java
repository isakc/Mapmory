package com.mapmory.services.user.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mapmory.common.domain.Search;
import com.mapmory.services.user.domain.Follow;
import com.mapmory.services.user.domain.LoginLog;
import com.mapmory.services.user.domain.SocialLoginInfo;
import com.mapmory.services.user.domain.SuspensionLog;
import com.mapmory.services.user.domain.User;

@Mapper
public interface UserDao {

	public int insertUser(User user);
	
	public int insertLoginLog(LoginLog log);
	
	public int insertSocialLoginLink(SocialLoginInfo socialLoginInfo);
	
	public int insertSuspendLog(SuspensionLog log);
	
	public User selectUser(User user);
	
	public List<User> selectUserList(Search search);
	
	public List<Follow> selectFollowList(Search search);
	
	public List<SocialLoginInfo> selectSocialIdList(String userId);
	
	public List<LoginLog> selectUserLoginList(Search search);
	
	public List<SuspensionLog> selectSuspensionList(Search search);
	
	// updateUserInfo, updateProfile는 모두 이것으로 동적 query로 처리할 예정
	public int updateUser(User user);
	
	public int updateRecoverAccount(String userId);
	
	
	// id와 nickname 중복 검사는 모두 이것으로 처리.
	public int checkDuplication(User user);
	
	public int getUserListTotalCount(Search search);
	
	public int getFollowListTotalCount(Search search);
}
