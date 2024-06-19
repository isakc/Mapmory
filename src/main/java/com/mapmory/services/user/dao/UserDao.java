package com.mapmory.services.user.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mapmory.common.domain.Search;
import com.mapmory.services.user.domain.FollowBlock;
import com.mapmory.services.user.domain.FollowMap;
import com.mapmory.services.user.domain.FollowSearch;
import com.mapmory.services.user.domain.Login;
import com.mapmory.services.user.domain.LoginDailyLog;
import com.mapmory.services.user.domain.LoginLog;
import com.mapmory.services.user.domain.LoginMonthlyLog;
import com.mapmory.services.user.domain.LoginSearch;
import com.mapmory.services.user.domain.SocialLoginInfo;
import com.mapmory.services.user.domain.SuspensionLog;
import com.mapmory.services.user.domain.SuspensionLogList;
import com.mapmory.services.user.domain.User;

@Mapper
public interface UserDao {

	public int insertUser(User user);
	
	public int insertLoginLog(String userId);
	
	public int insertFollow(FollowBlock follow);
	
	public int insertSocialLoginLink(SocialLoginInfo socialLoginInfo);
	
	public int insertSuspendLog(SuspensionLog log);
	
	public User selectUser(User user);
	
	/**
	 * searchCondition :: 0 : ID 검색, 1 : 닉네임 검색
	 * searchKeyword :: 검색어
	 * startRowNum & endRowNum 사용
	 * @param search
	 * @return
	 */
	public List<User> selectUserList(Search search);
	
	public List<FollowMap> selectFollowList(FollowSearch search);
	
	public int selectFollowListTotalCount(Search search);
	
	public List<FollowMap> selectFollowerList(FollowSearch search);
	
	public int selectFollowerListTotalCount(Search search);
	
	public int isFollow(FollowBlock fb);
	
	public List<SocialLoginInfo> selectSocialIdList(String userId);
	
	public String selectUserIdBySocialId(String socialId);
	
	/**
	 * 사용자 일간 전체 로그인 통계를 조회한다.
	 * @param search
	 * @return
	 */
	public List<LoginDailyLog> selectUserLoginDailyList(LoginSearch search);
	
	/**
	 * 사용자 월간 전체 로그인 통계를 조회한다.
	 * @param search
	 * @return
	 */
	public List<LoginMonthlyLog> selectUserLoginMonthlyList(LoginSearch search);
	
	/**
	 * searchCondition (0: LIKE 조회, 1: '=' 조회)
	 * @param search
	 * @return
	 */
	public List<SuspensionLogList> selectSuspensionList(Search search);
	
	public String selectPassword(String userId);
	
	// 내 정보 수정, 프로필 수정, 비밀번호 변경, 2단계 인증 설정, 회원 탈퇴 처리, 회원 정지 모두 지원
	public int updateUser(User user);
	
	/**
	 * null 처리 때문에 따로 분리
	 * @param userId
	 * @return
	 */
	public int updateRecoverAccount(String userId);
	
	public int updateHideProfile(String userId);
	
	public int updateSecondaryAuth(String userId);
	
	public int updatePassword(Login login);
	
	public int updateFollowToBlock(FollowBlock block);
	
	public int deleteFollow(FollowBlock follow);

	public int deleteSuspendUser(int logNo);
	
	// id, nickname 모두 지원
	public int checkDuplication(User user);
	
	public int getUserListTotalCount(Search search);
	
	public int getFollowListTotalCount(FollowSearch search);
}
