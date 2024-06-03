package com.mapmory.unit.user;

import java.time.LocalDate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
	public void testGetDetialUser() {
				
		User resultUser = userService.getDetailUser("user_123");
		
		Assertions.assertThat(resultUser.getUserId()).isEqualTo("user_123");
	}
	
	// @Test
	public void testAddUser() {
		
		String userId = "hong";
		String password = "qwer1234";
		
		User user = User.builder()
				.userId(userId)
				.userPassword(password)
				.role((byte) 1)
				.userName("홍길동")
				.nickname("나는 홍길동")
				.birthday(LocalDate.parse("2010-02-22"))
				.email("test@test.com")
				.phoneNumber("010-1234-1234")
				.sex(1)
				.build();
		
		boolean result = userService.addUser(user);
		
		Assertions.assertThat(result).isEqualTo(true);
		
		User resultUser = userService.getDetailUser(userId);
		
		Assertions.assertThat(resultUser.getUserPassword()).isEqualTo(password);
	}
	
	public void testAddSuspendUser() {
		
	}
	
	// @Test
	public void testAddSocialLoginLink() {
		
		String userId = "simple_7890";
		String socialId = "103984541210666630525";
		
		SocialLoginInfo info = SocialLoginInfo.builder()
								.userId(userId)
								.socialId(socialId)
								.build();
		
		boolean result = userService.addSocialLoginLink(info);
		
		Assertions.assertThat(result).isEqualTo(true);
		
		String resultSocialId = userService.getSocialId(userId, 0);
		
		Assertions.assertThat(resultSocialId).isEqualTo(socialId);
		
	}
	
	// @Test
	public void testGetId() {
		
		String userName = "Tom Lee";
		String email = "tom.lee@example.com";
		
		User user = User.builder()
					.userName(userName)
					.email(email)
					.build();
		
		String userId = userService.getId(user);
		
		Assertions.assertThat(userId).isEqualTo("test_user789");
	}
	
	// @Test
	public void testGetSocialId() {
		
		String userId = "simple_7890";
		int type=1;
		
		String socialId = userService.getSocialId(userId, type);
		
		Assertions.assertThat(socialId).isEqualTo("Bb3H7Y5_2l38r-f7hNlrjLKbHcPmmwsDwp57dFIjZNo");
	}
	
	public void testGetUserList() {
		
	}
	
	// @Test
	public void testCheckSocialId() {
		
		String userId = "unique_id_4567";
		String socialId = "303984541210605030527";
		
		SocialLoginInfo socialLoginInfo = SocialLoginInfo.builder()
											.userId(userId)
											.socialId(socialId)
											.build();
		
		boolean result = userService.checkSocialId(socialLoginInfo);
		
		Assertions.assertThat(result).isEqualTo(true);
	}
	
}
