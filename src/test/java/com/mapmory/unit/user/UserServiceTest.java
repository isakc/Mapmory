package com.mapmory.unit.user;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.mapmory.common.domain.Search;
import com.mapmory.services.user.domain.FollowBlock;
import com.mapmory.services.user.domain.FollowMap;
import com.mapmory.services.user.domain.SocialLoginInfo;
import com.mapmory.services.user.domain.User;
import com.mapmory.services.user.service.UserService;

@SpringBootTest
@Transactional
public class UserServiceTest {
	
	@Autowired
	@Qualifier("userServiceImpl")
	private UserService userService;
	
	
	// @Test
	public void testAddUser() {
		
		String userId = "hong";
		String password = "qwer1234";
		String userName = "홍길동";
		String nickname="나는 홍길동";
		LocalDate birthday = LocalDate.parse("2010-02-22");
		String email = "test@test.com";
		String phoneNumber = "010-1234-1234";
		int sex = 1;
		
		boolean result = userService.addUser(userId, password, userName, nickname, birthday, sex, email, phoneNumber);
		
		Assertions.assertThat(result).isEqualTo(true);
		
		User resultUser = userService.getDetailUser(userId);
		
		Assertions.assertThat(resultUser.getUserPassword()).isEqualTo(password);
	}
	
	public void testAddSuspendUser() {
		
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
	
	// @Test
	public void testGetFollowList() {
		
		String userId = "user1";
		String searchKeyword = "a";
		
		List<FollowMap> list = userService.getFollowList(userId, searchKeyword);
		String nickname = list.get(0).getNickname();
		String userName = list.get(0).getUserName();
		boolean result = nickname.contains(searchKeyword) || userName.contains(searchKeyword);
		Assertions.assertThat(result).isTrue();
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
	public void testUpdateUserInfo() {
		
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
	public void testUpdateProfile() {
		
		String userId = "john_doe_90";
		String profileFileName = "apwoskd123.jpg";
		String introduction = "안녕하세요. 반갑습니다.";
		
		boolean result = userService.updateProfile(userId, profileFileName, introduction);
		
		Assertions.assertThat(result).isTrue();
		
		User user = userService.getDetailUser(userId);
		Assertions.assertThat(user.getProfileImageName()).isEqualTo(profileFileName);
		Assertions.assertThat(user.getIntroduction()).isEqualTo(introduction);
	}
	
	// @Test
	public void testDeleteFollow() {
		
		String userId = "user1";
		String targetId = "user2";

		boolean result = userService.deleteFollow(userId, targetId);
		
		Assertions.assertThat(result).isTrue();
		
		List<FollowMap> list = userService.getFollowList(userId, null);
		
		boolean flag = true;
		for(FollowMap fm : list) {
			if(fm.getUserId().equals("user2"))
				flag = false;
		}
		
		Assertions.assertThat(flag).isTrue();
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

	@Test
	public void testCheckSecondaryAuth() {
		
		// 2단계 인증을 설정하지 않은 경우
		String userId = "sample_user1";
		boolean result = userService.checkSecondaryAuth(userId);
		Assertions.assertThat(result).isFalse();
		
		// 2단계 인증을 설정한 경우
		userId = "admin";
		result = userService.checkSecondaryAuth(userId);
		Assertions.assertThat(result).isTrue();
		
		
	}
}
