package com.mapmory.services.user.service;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.mapmory.common.domain.Search;
import com.mapmory.services.user.domain.FollowMap;
import com.mapmory.services.user.domain.Login;
import com.mapmory.services.user.domain.LoginDailyLog;
import com.mapmory.services.user.domain.LoginMonthlyLog;
import com.mapmory.services.user.domain.LoginSearch;
import com.mapmory.services.user.domain.SocialLoginInfo;
import com.mapmory.services.user.domain.SuspensionLogList;
import com.mapmory.services.user.domain.TermsAndConditions;
import com.mapmory.services.user.domain.User;
import com.mapmory.services.user.domain.auth.google.GoogleJwtPayload;
import com.mapmory.services.user.domain.auth.google.GoogleToken;
import com.mapmory.services.user.domain.auth.google.GoogleUserOtpCheck;
import com.mapmory.services.user.domain.auth.naver.NaverAuthToken;
import com.mapmory.services.user.domain.auth.naver.NaverProfile;

public interface UserService {

	public void setupForTest();
	
	/**
	 * 
	 * @param userId  : 5~20자의 영문 소문자, 숫자와 특수기호(_),(-)만 사용 가능
	 * @param userPassword  : 8~16자의 영문 대/소문자, 숫자, 특수문자만 사용 가능
	 * @param userName  : 최소 2자 최대 18자의 한글 및 영문 사용만 가능
	 * @param nickname  : 최소 1자 최대 10자의 영어, 숫자, 띄어쓰기만 사용 가능하다. 첫 글자는 띄어쓰기가 불가능
	 * @param birthday
	 * @param sex  : 0:비공개, 1:남자, 2:여성
	 * @param email  : test@test.com
	 * @param phoneNumber  : 010-1234-1234
	 * @return
	 * @throws Exception
	 */
	public boolean addUser(String userId, String userPassword, String userName, String nickname, LocalDate birthday, int sex, String email, String phoneNumber, String socialId) throws Exception;
	
	/**
	 * 계정 정지 정책 (1회: 1일 정지, 2회: 7일 정지, 3회: 14일 정지, 4회: 영구 정지)
	 * @param userId
	 * @param reason
	 * @return
	 * @throws Exception
	 */
	public boolean addSuspendUser(String userId, String reason) throws Exception;
	
	public boolean addSocialLoginLink(String userId, String socialId);
	
	public boolean addLeaveAccount(String userId);
	
	public boolean addLoginLog(String userId);
	
	public boolean addFollow(String userId, String targetId);

	// getProfile, getUserInfo 모두 이거로 처리
	public User getDetailUser(String userId);
	
	public String getId(String userName, String email);
	
	@Deprecated
	/**
	 * socialLoginInfoType (0 : google, 1 : naver, 2 : kakao)
	 * checkSocialId()를 사용하기 바람.
	 * @param socialLoginInfo
	 * @return
	 */
	public String getSocialId(SocialLoginInfo socialLoginInfo);

	public Map<String, Object> getUserList(Search search);
	
	/**
	 * 
	 * @param myUserId  :: 내 session id
	 * @param userId  :: target id
	 * @param searchKeyword : nullable
	 * @param currentPage : nullable (paging 불필요시)
	 * @param limit : nullable (paging 불필요시)
	 * @param selectFollow  :: true : 팔로우 목록, false : 팔로워 목록
	 * @return
	 */
	public List<FollowMap> getFollowList(String myUserId, String userId, String searchKeyword, int currentPage, int limit, int selectFollow);
	
	/**
	 * 
	 * @param userId  :: target id
	 * @param searchKeyword : nullable
	 * @param currentPage : nullable (paging 불필요시)
	 * @param limit : nullable (paging 불필요시)
	 * @param selectFollow  :: true : 팔로우 목록, false : 팔로워 목록
	 * @return
	 */
	public int getFollowListTotalCount(String userId, String searchKeyword, int currentPage, int limit, int selectFollow);
	
	// public List<FollowMap> getFollowerList(String myUserId, String userId, String searchKeyword, int currentPage, int limit);
	
	// public int getFollowerListTotalCount(String userId, String searchKeyword, int currentPage, int limit);
	
	public boolean checkFollow(String myUserId, String targetUserId);
	
	/**
	 * LIKE 검색
	 * @param userId
	 * @param currentPage
	 * @param limit
	 * @return
	 */
	public List<SuspensionLogList> getSuspensionLogList(String userId, Integer currentPage, Integer limit);
	
	/**
	 * equal 검색
	 * 아무 값도 존재하지 않으면 null을 반환.
	 * @param userId
	 * @return
	 */
	public SuspensionLogList getSuspensionLogListActually(String userId);
	
	/**
	 * 사용자 전체 로그인 통계를 조회한다. 
	 * @return
	 */
	public List<LoginDailyLog> getUserLoginDailyList(LoginSearch search);
	
	public List<LoginMonthlyLog> getUserLoginMonthlyList(LoginSearch search);

	
	/**
	 * 이용약관은 file io 로 처리
	 * @return
	 */
	public List<TermsAndConditions> getTermsAndConditionsList() throws Exception;
	
	/**
	 * 이용약관은 file io 로 처리
	 * @return
	 */
	public TermsAndConditions getDetailTermsAndConditions(String filePath) throws Exception;
	
	public String getPassword(String userId);
	
	public String getUserIdBySocialId(String socialId) throws Exception;
	
	public boolean updateUserInfo(String userId, String userName, String nickname, LocalDate birthday, Integer sex, String email, String phoneNumber) throws Exception;
	
	/**
	 * 
	 * @param file
	 * @param userId
	 * @param profileImageName
	 * @param introduction  : 최대 100글자
	 * @return
	 * @throws Exception
	 */
	public boolean updateProfile(MultipartFile file, String userId, String profileImageName, String introduction) throws Exception;
	
	/**
	 * 자시소개만 변경하는 경우에 사용
	 */
	public boolean updateProfile(String userId, String introduction);
	
	public boolean updatePassword(String userId, String userPassword);
	
	// public boolean updateSecondaryAuth(String userId);
	public boolean updateSecondaryAuth(String userId, int type);
	
	/**
	 * 1: column 변경 성공, 0: column 변경 실패, 2: 정책 상 변경 불가. 4: 현재 탈퇴 상태가 아님
	 * 1개월 조건 때문에 따로 분리
	 * @param userId
	 * @return
	 */
	public int updateRecoverAccount(String userId);
	
	public boolean updateHideProfile(String userId);
	
	public boolean updateFollowToBlock(String myUserId, String targetId);
	
	public boolean deleteFollow(String userId, String targetId);
	
	public boolean deleteSuspendUser(int logNo);

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
	 * true : 존재함, false : 존재하지 않음
	 * @param userId
	 * @param email
	 * @return
	 */
	public boolean checkUserExist(String userId, String email);
	
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
	
	/**
	 * true : 정상, false : 정지 상태
	 * @param userId
	 * @return
	 */
	public Map<String, String> checkSuspended(String userId);
	
	/**
	 * true: 설정됨, false : 설정안함
	 * @param userId
	 * @return
	 */
	public boolean checkSetSecondaryAuth(String userId);
	
	public NaverAuthToken getNaverToken(String code, String state) throws JsonMappingException, JsonProcessingException;
	
	public NaverProfile getNaverProfile(String code, String state, String accessToken)  throws JsonMappingException, JsonProcessingException;
	
	public String generateSecondAuthKey();
	
	public boolean checkSecondAuthKey(GoogleUserOtpCheck googleUserOtpCheck) throws InvalidKeyException, NoSuchAlgorithmException;
	
	/*
	public GoogleToken getGoogleToken(String code) throws JsonMappingException, JsonProcessingException;
	
	public GoogleJwtPayload getGoogleProfile(String idToken) throws JsonMappingException, JsonProcessingException, UnsupportedEncodingException;
	*/
	
	// public GoogleJwtPayload getGoogleProfie(String code) throws JsonMappingException, JsonProcessingException, UnsupportedEncodingException;
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	//// jaemin ////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	
	public String getKakaoAccessToken (String authorizeCode);
	
	public String getKakaoUserInfo (String accessToken) throws Exception;
	
	public int PhoneNumberCheck(String to) throws Exception;
	
}
