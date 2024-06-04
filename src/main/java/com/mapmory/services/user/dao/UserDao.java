package com.mapmory.services.user.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mapmory.common.domain.Search;
import com.mapmory.services.user.domain.Follow;
import com.mapmory.services.user.domain.FollowMap;
import com.mapmory.services.user.domain.LoginLog;
import com.mapmory.services.user.domain.SocialLoginInfo;
import com.mapmory.services.user.domain.SuspensionLog;
import com.mapmory.services.user.domain.User;

@Mapper
public interface UserDao {

	public int insertUser(User user);
	
	public int insertLoginLog(LoginLog log);
	
	public int insertFollow(Follow follow);
	
	public int insertSocialLoginLink(SocialLoginInfo socialLoginInfo);
	
	public int insertSuspendLog(SuspensionLog log);
	
	public User selectUser(User user);
	
	public List<User> selectUserList(Search search);
	
	public List<FollowMap> selectFollowList(Search search);
	
	public List<SocialLoginInfo> selectSocialIdList(String userId);
	
	/**
	 * 사용자 전체 로그인 통계를 조회한다.
	 * @param search
	 * @return
	 */
	public List<LoginLog> selectUserLoginList(Search search);
	
	public List<SuspensionLog> selectSuspensionList(Search search);
	
	// 내 정보 수정, 프로필 수정, 비밀번호 변경, 2단계 인증 설정, 회원 탈퇴 처리 모두 지원
	public int updateUser(User user);
	
	/**
	 * null 처리 때문에 따로 분리
	 * @param userId
	 * @return
	 */
	public int updateRecoverAccount(String userId);
	
	public int deleteFollow(Follow follow);
	
	// id, nickname 모두 지원
	public int checkDuplication(User user);
	
	public int getUserListTotalCount(Search search);
	
	public int getFollowListTotalCount(Search search);
}
