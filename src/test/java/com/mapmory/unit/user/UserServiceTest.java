package com.mapmory.unit.user;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.mapmory.common.domain.Search;
import com.mapmory.services.user.domain.FollowBlock;
import com.mapmory.services.user.domain.FollowMap;
import com.mapmory.services.user.domain.SocialLoginInfo;
import com.mapmory.services.user.domain.SuspensionLogList;
import com.mapmory.services.user.domain.TermsAndConditions;
import com.mapmory.services.user.domain.User;
import com.mapmory.services.user.service.UserService;

@SpringBootTest
@Transactional
public class UserServiceTest {
	
	@Autowired
	@Qualifier("userServiceImpl")
	private UserService userService;
	
	
	// @Test
	public void testAddUser() throws Exception {
		
		String userId = null;
		String userName = "홍길동";
		String password = "qwer1234";
		String nickname=null;
		LocalDate birthday = LocalDate.parse("2010-02-22");
		String email = "test@test.com";
		String phoneNumber = "010-1234-1234";
		int sex = 1;
		
		// 유해성에 위배되는 아이디를 사용한 경우
		userId = "씨1fuc#k발";
		nickname = "홍길동";
		try {
			boolean result = userService.addUser(userId, password, userName, nickname, birthday, sex, email, phoneNumber);
			
		} catch(Exception e) {
			Assertions.assertThatExceptionOfType(Exception.class);
		}
		
		// 유해성에 위배되는 닉네임을 사용한 경우
		userId = "hong";
		nickname="씨   발";
		try {
			boolean result = userService.addUser(userId, password, userName, nickname, birthday, sex, email, phoneNumber);
			
		} catch(Exception e) {
			Assertions.assertThatExceptionOfType(Exception.class);
		}
		
		// 정상적으로 입력한 경우
		userId = "hong";
		nickname = "홍길동";
		boolean result = userService.addUser(userId, password, userName, nickname, birthday, sex, email, phoneNumber);
		
		Assertions.assertThat(result).isEqualTo(true);
		
		User resultUser = userService.getDetailUser(userId);
		
		Assertions.assertThat(resultUser.getUserPassword()).isEqualTo(password);
	}
	
	
	
	// @Test
	public void testAddSuspendUser() throws Exception {
		
		// 최초 정지인 경우
		String userId = "user10";
		String reason = "선정성";
		
		boolean result = userService.addSuspendUser(userId, reason);
				
		Assertions.assertThat(result).isTrue();
		
		SuspensionLogList resultTemp = userService.getSuspensionLogListActually(userId);
		Assertions.assertThat(resultTemp.getSuspensionDetailList().size()).isEqualTo(1);
		Assertions.assertThat(resultTemp.getSuspensionDetailList().get(0).getReason()).isEqualTo(reason);
				
		// 영구 정지인 경우
		userId = "user1";
		result = userService.addSuspendUser(userId, reason);
		
		Assertions.assertThat(result).isTrue();
		
		resultTemp = userService.getSuspensionLogListActually(userId);
		Assertions.assertThat(resultTemp.getSuspensionDetailList().size()).isEqualTo(4);
		Assertions.assertThat(resultTemp.getSuspensionDetailList().get(3).getReason()).isEqualTo(reason);
		
		
		
		// 예외가 발생하는 경우(정책 최대 개수 초과)
		userId = "user2";
		boolean flag = false;
		try {
			result = userService.addSuspendUser(userId, reason);
		} catch (Exception e) {
			Assertions.assertThat(e).isNotNull();
			flag = true;
		}
		
		Assertions.assertThat(flag).isTrue();
	}
	
	// @Test
	public void testAddLeaveUser() {
		
		String userId = "user_123";
		boolean result = userService.addLeaveAccount(userId);
		Assertions.assertThat(result).isTrue();
		
		LocalDateTime resultTime = userService.getDetailUser(userId).getLeaveAccountDate();
		Assertions.assertThat(resultTime).isNotNull();
	}
	
	// @Test
	public void testAddSocialLoginLink() {
		
		String userId = "simple_7890";
		String socialId = "103984541210666630525";
		
		boolean result = userService.addSocialLoginLink(userId, socialId);
		
		Assertions.assertThat(result).isEqualTo(true);
		
		int type = 0;
		
		SocialLoginInfo info = SocialLoginInfo.builder()
								.userId(userId)
								.socialId(socialId)
								.socialLoginInfoType(type)
								.build();
		
		String resultSocialId = userService.getSocialId(info);
		
		Assertions.assertThat(resultSocialId).isEqualTo(socialId);
		
	}
	
	// @Test
	public void testGetDetialUser() {
					
			User resultUser = userService.getDetailUser("user_123");
			
			Assertions.assertThat(resultUser.getUserId()).isEqualTo("user_123");
		}
	
	// @Test
	public void testGetId() {
		
		String userName = "Tom Lee";
		String email = "tom.lee@example.com";
		
		String userId = userService.getId(userName, email);
		
		Assertions.assertThat(userId).isEqualTo("test_user789");
	}
	
	// @Test
	public void testGetSocialId() {
		
		String userId = "simple_7890";
		int type=1;
		
		SocialLoginInfo socialLoginInfo = SocialLoginInfo.builder()
										.userId(userId)
										.socialLoginInfoType(type)
										.build();
								
		String socialId = userService.getSocialId(socialLoginInfo);
		
		Assertions.assertThat(socialId).isEqualTo("Bb3H7Y5_2l38r-f7hNlrjLKbHcPmmwsDwp57dFIjZNo");
	}
	
	// @Test
	@SuppressWarnings("unchecked")
	public void testGetUserList() {
		
		Search search = Search.builder()
				.currentPage(1)
				.pageSize(5)
				.searchCondition(0)  // 0: user_id, 1: nickname
				.searchKeyword("user")
				.build();
		
		Map<String, Object> map = userService.getUserList(search);

		List<User> listUser = (List<User>) map.get("userList");
		int count = (int) map.get("count");
		
		Assertions.assertThat(listUser.get(0).getUserId().contains("user"))
					.isEqualTo(true);
		
		Assertions.assertThat(listUser.size()).isEqualTo(count);
		
		
		/// nickname 검색
		search = Search.builder()
					.currentPage(1)
					.pageSize(5)
					.searchCondition(1)  // 0: user_id, 1: nickname
					.searchKeyword("alice")
					.build();
		
		map = userService.getUserList(search);
		listUser = (List<User>) map.get("userList");
		count = (int) map.get("count");

		Assertions.assertThat(listUser.get(0).getNickname().contains("alice"))
					.isEqualTo(true);
		
		Assertions.assertThat(listUser.size()).isEqualTo(count);
	}
	
	/*
	// @Test
	public void testGetFollowList() {
		
		String userId = "user1";
		String searchKeyword = "a";
		int currentPage = 1;
		int limit = 5;
		
		List<FollowMap> list = userService.getFollowList(userId, searchKeyword, currentPage, limit);		
		String nickname = list.get(0).getNickname();
		String userName = list.get(0).getUserName();
		boolean result = nickname.contains(searchKeyword) || userName.contains(searchKeyword);
		Assertions.assertThat(result).isTrue();
	}
	*/
	
	// @Test
	public void testGetSuspensionLogList() {
		
		String userId = "user";
		int searchCondition = 0;
		int currentPage = 1;
		int limit = 5;
		
		Search search = Search.builder()
						.userId(userId)
						.searchCondition(searchCondition)
						.limit(limit)
						.currentPage(currentPage)
						.build();
		
		 List<SuspensionLogList> result = userService.getSuspensionLogList(userId, currentPage, limit);
		 Assertions.assertThat(result.get(0).getUserId()).isEqualTo("user1");
		 Assertions.assertThat(result.get(0).getSuspensionDetailList().get(0).getReason()).isEqualTo("욕함");
		 Assertions.assertThat(result.get(1).getUserId()).isEqualTo("user2");
	}
	

	// @Test
	public void testGetSuspensionLogListActually() {
		
		String userId = "user2";
		int searchCondition = 1;

		
		Search search = Search.builder()
						.userId(userId)
						.searchCondition(searchCondition)
						.build();
		
		SuspensionLogList result = userService.getSuspensionLogListActually(userId);
		Assertions.assertThat(result.getSuspensionDetailList().size()).isEqualTo(4);
		Assertions.assertThat(result.getUserId()).isEqualTo("user2");
	}
	
	@Test
	public void testGetDetailTermsAndConditions() throws Exception {
		
		// String fileName = "C:\\Users\\rlaeo\\OneDrive\\바탕 화면\\bitcamp project\\이용약관 예제\\개인정보 수집 및 이용 약관.txt";
		String fileName = "개인정보 수집 및 이용 약관.txt";
		
		try {
			TermsAndConditions result = userService.getDetailTermsAndConditions(fileName);

			Assertions.assertThat(result).isNotNull();
			Assertions.assertThat(result.getTitle()).contains("개인정보");
			Assertions.assertThat(result.getRequired()).isTrue();
			Assertions.assertThat(result.getContents()).contains("회사");
		} catch (Exception e) {
			// TODO: handle exception
			Assertions.fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetTermsAndConditionsList() throws Exception {
		
		List<TermsAndConditions> result = userService.getTermsAndConditionsList();
		
		Assertions.assertThat(result.size()).isEqualTo(2);
		Assertions.assertThat(result.get(0).getTitle()).contains("개인정보");
		Assertions.assertThat(result.get(1).getTitle()).contains("위치");
	}
	
	// @Test
	public void testUpdateUserPassword() {
		
		String userId = "john_doe_90";
		String userPassword = "qwer1234!";
		
		User user = User.builder()
				.userId(userId)
				.userPassword(userPassword)
				.build();
		
		boolean result = userService.updatePassword(userId, userPassword);
		
		Assertions.assertThat(result).isEqualTo(true);
		
		String updatedUserPassword = userService.getDetailUser(userId).getUserPassword();
		
		Assertions.assertThat(updatedUserPassword).isEqualTo(userPassword);
	}
	
	// @Test
	@DisplayName("회원정보업데이트 - 정상인 경우")
	public void testUpdateUserInfo() throws Exception {
		
		String userId = "my_id-is_456";
		String userName = "홍길동";
		String nickname = "나는홍길동";
		LocalDate birthday = LocalDate.parse("2001-09-21");
		Integer sex = 0;
		String email = "test@test.com";
		String phoneNumber = "010-6666-3333";
		
		boolean result = userService.updateUserInfo(userId, userName, nickname, birthday, sex, email, phoneNumber);
		
		Assertions.assertThat(result).isEqualTo(true);

		User resultUser = userService.getDetailUser(userId);
		
		Assertions.assertThat(resultUser.getNickname()).isEqualTo(nickname);
	    Assertions.assertThat(resultUser.getBirthday()).isEqualTo(birthday);
	    Assertions.assertThat(resultUser.getSex()).isEqualTo(sex);
	    Assertions.assertThat(resultUser.getEmail()).isEqualTo(email);
	    Assertions.assertThat(resultUser.getPhoneNumber()).isEqualTo(phoneNumber);
	}
	
	@Test
	@DisplayName("회원정보업데이트 - 비속어를 사용한 경우")
	public void testUpdateUserInfoErr() throws Exception {
		
		String userId = "my_id-is_456";
		String userName = "홍길동";
		String 	nickname="씨   발";
		LocalDate birthday = LocalDate.parse("2001-09-21");
		Integer sex = 0;
		String email = "test@test.com";
		String phoneNumber = "010-6666-3333";
		
		
		try {
			userService.updateUserInfo(userId, userName, nickname, birthday, sex, email, phoneNumber);
			
		} catch(Exception e) {
			Assertions.assertThatExceptionOfType(Exception.class);
		}
	}
	
	// @Test
	public void testUpdateRecoverAccount() {
		
		/// 1개월이 지난 사용자
		String userId = "leave_user";
		int result = userService.updateRecoverAccount(userId);
		Assertions.assertThat(result).isEqualTo(2);
		
		/// 아직 1개월이 지나지 않은 사용자
		userId = "want_to_recover_user";
		result =  userService.updateRecoverAccount(userId);
		Assertions.assertThat(result).isEqualTo(1);
		
		LocalDateTime resultDate = userService.getDetailUser(userId).getLeaveAccountDate();
		Assertions.assertThat(resultDate).isNull();
	}
	
	
	// @Test
	// Multipart 관련은 test 불가.
	public void testUpdateProfile() throws Exception {
		
		String userId = "john_doe_90";
		MultipartFile file = null;
		String profileFileName = "apwoskd123.jpg";
		String introduction = "안녕하세요. 반갑습니다.";
		
		boolean result = userService.updateProfile(file, userId, profileFileName, introduction);
		
		Assertions.assertThat(result).isTrue();
		
		User user = userService.getDetailUser(userId);
		Assertions.assertThat(user.getProfileImageName()).isEqualTo(profileFileName);
		Assertions.assertThat(user.getIntroduction()).isEqualTo(introduction);
	}
	
	// @Test
	public void testUpdateHideProfile() {
		
		String userId = "user1";
		
		boolean result = userService.updateHideProfile(userId);
		
		Assertions.assertThat(result).isTrue();
	}
	
	// @Test
	public void testUpdateSecondaryAuth() {
		
		String userId = "user1";
		
		boolean result = userService.updateSecondaryAuth(userId);
		
		Assertions.assertThat(result).isTrue();
	}
	
	/*
	// @Test
	public void testDeleteFollow() {
		
		String userId = "user1";
		String targetId = "user2";

		boolean result = userService.deleteFollow(userId, targetId);
		
		Assertions.assertThat(result).isTrue();
		
		int currentPage = 1;
		int limit = 5;
		
		List<FollowMap> list = userService.getFollowList(userId, targetId, currentPage, limit);
		
		boolean flag = true;
		for(FollowMap fm : list) {
			if(fm.getUserId().equals("user2"))
				flag = false;
		}
		
		Assertions.assertThat(flag).isTrue();
	}
	*/
	
	// @Test
	public void testDeleteSuspendUser() {
		
		int logNo = 15;
		
		boolean result = userService.deleteSuspendUser(logNo);
		Assertions.assertThat(result).isTrue();
		
		
		String userId = "user1";
		int count = userService.getSuspensionLogListActually(userId).getSuspensionDetailList().size();
		Assertions.assertThat(count).isEqualTo(2);
	}
	
	// @Test
	public void testCheckSocialId() {
		
		String userId = "unique_id_4567";
		String socialId = "303984541210605030527";
		
		boolean result = userService.checkSocialId(userId, socialId);
		
		Assertions.assertThat(result).isEqualTo(true);
	}

	// @Test
	public void testCheckDuplicationById() {
		
		// 중복되는 경우
		String userId = "user1";
		
		boolean result = userService.checkDuplicationById(userId);
		
		Assertions.assertThat(result).isEqualTo(false);
		
		// 중복되지 않는 경우
		userId = "user9999";
		result = userService.checkDuplicationById(userId);
		Assertions.assertThat(result).isEqualTo(true);
	}
	
	// @Test
	public void testCheckDuplicationByNickname() {
		
		// 중복되는 경우
		String nickname = "tomlee";
		
		boolean result = userService.checkDuplicationByNickname(nickname);
		
		Assertions.assertThat(result).isEqualTo(false);
		
		// 중복되지 않는 경우
		nickname = "tomlee9999";
		result = userService.checkDuplicationByNickname(nickname);
		Assertions.assertThat(result).isEqualTo(true);
	}

	// @Test
	public void testCheckPasswordChangeDeadlineExceeded() {
		
		// 비밀번호 권고 기한이 지난 경우
		String userId = "password_user";
		boolean result = userService.checkPasswordChangeDeadlineExceeded(userId);
		Assertions.assertThat(result).isEqualTo(false);
		
		// 아직 권고 기한이 지나지 않은 경우
		userId = "simple_7890";
		result = userService.checkPasswordChangeDeadlineExceeded(userId);
		Assertions.assertThat(result).isEqualTo(true);
	}

	// @Test
	public void testCheckSecondaryAuth() {
		
		// 2단계 인증을 설정하지 않은 경우
		String userId = "sample_user1";
		boolean result = userService.checkSetSecondaryAuth(userId);
		Assertions.assertThat(result).isFalse();
		
		// 2단계 인증을 설정한 경우
		userId = "admin";
		result = userService.checkSetSecondaryAuth(userId);
		Assertions.assertThat(result).isTrue();
		
		
	}
}
