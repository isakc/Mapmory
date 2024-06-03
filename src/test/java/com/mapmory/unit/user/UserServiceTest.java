package com.mapmory.unit.user;

import java.time.LocalDate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.mapmory.services.user.domain.User;
import com.mapmory.services.user.service.UserService;

@SpringBootTest
@Transactional
public class UserServiceTest {
	
	@Autowired
	@Qualifier("userServiceImpl")
	private UserService userService;
	

	// 아직 작업 안함
	// @Test
	public void testAddUser() {
		
		User user = User.builder()
				.userId("hong")
				.userPassword("1234")
				.role((byte) 1)
				.userName("홍길동")
				.nickname("나는 홍길동")
				.birthday(LocalDate.parse("2010-02-22"))
				.email("test@test.com")
				.phoneNumber("010-1234-1234")
				.sex(1)
				.build();
		
		int result = userService.addUser(user);
		
		Assertions.assertThat(result).isEqualTo(1);
	}
	
	@Test
	public void testGetUser() {
				
		User resultUser = userService.getUser("user_123");
		
		Assertions.assertThat(resultUser.getUserId()).isEqualTo("user_123");
	}
}
