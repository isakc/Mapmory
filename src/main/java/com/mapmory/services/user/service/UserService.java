package com.mapmory.services.user.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.mapmory.common.domain.Search;
import com.mapmory.services.user.domain.FollowMap;
import com.mapmory.services.user.domain.LoginLog;
import com.mapmory.services.user.domain.SocialLoginInfo;
import com.mapmory.services.user.domain.SuspensionLog;
import com.mapmory.services.user.domain.User;

public interface UserService {
	
	/*
	 *  naver, google login service 구현 필요.. 
	 *  그 외 controller에서 사용해야 할 각종 business logic (REST 포함) 추가 예정
	 */
	
	public boolean addUser(String userId, String userPassword, String userName, String nickname, LocalDate birthday, int sex, String email, String phoneNumber);
	
	public boolean addSuspendUser(String userId);
	
	public boolean addSocialLoginLink(String userId, String socialId);
	
	public boolean addLeaveAccount(String userId);
	
	public boolean addLoginLog(String userId);

	// getProfile, getUserInfo 모두 이거로 처리
	public User getDetailUser(String userId);
	
	public String getId(String userName, String email);
	
	// 0: google, 1: naver, 2: kakao
	@Deprecated
	/**
	 * checkSocialId()를 사용하기 바람.
	 * @param socialLoginInfo
	 * @return
	 */
	public String getSocialId(SocialLoginInfo socialLoginInfo);

	public Map<String, Object> getUserList(Search search);
	
	public List<FollowMap> getFollowList(String userId, String searchKeyword, int currentPage, int limit);
	
	public List<SuspensionLog> getSuspensionLog(String userId);
	
	/**
	 * 사용자 전체 로그인 통계를 조회한다.
	 * @return
	 */
	public List<LoginLog> getUserLoginList(Search search);
	
	public boolean updateUserInfo(String userId, String userName, String nickname, LocalDate birthday, Integer sex, String email, String phoneNumber);
	
	public boolean updateProfile(String userId, String profileImageName, String introduction);
	
	public boolean updatePassword(String userId, String userPassword);
	
	public boolean updateSecondaryAuth(String userId);
	
	public boolean updateSuspendUser(String userId);
	
	/**
	 * 1: column 변경 성공, 0: column 변경 실패, 2: 정책 상 변경 불가.
	 * 1개월 조건 때문에 따로 분리
	 * @param userId
	 * @return
	 */
	public int updateRecoverAccount(String userId);
	
	public boolean updateHideProfile(String userId);
	
	public boolean deleteFollow(String userId, String targetId);
	
	/**
	 * true: 설정됨, false : 설정안함
	 * @param userId
	 * @return
	 */
	public boolean checkSecondaryAuth(String userId);
	
	/**
	 * false : 중복됨, true : 사용 가능
	 * @param userId
	 * @return
	 */
	public boolean checkDuplicationById(String userId);
	
	/**
	 * false : 중복됨, true : 사용 가능
	 * @param nickname
	 * @return
	 */
	public boolean checkDuplicationByNickname(String nickname);
	
	/**
	 * true : 검증됨, false : 검증 실패 
	 * @param userId
	 * @param socialId
	 * @return
	 */
	public boolean checkSocialId(String userId, String socialId);

	/**
	 * false : 3개월이 넘음, true : 정책상 아직 문제되지 않음
	 * @param userId
	 * @return
	 */
	public boolean checkPasswordChangeDeadlineExceeded(String userId);

	/**
	 * true: 설정함, false: 설정 안함
	 * @param userId
	 * @return
	 */
	public boolean checkHideProfile(String userId);
	
	public Map<String, Object> getTermsAndConditionsList();
	
	public Object getDetailTermsAndConditions();
	

}
