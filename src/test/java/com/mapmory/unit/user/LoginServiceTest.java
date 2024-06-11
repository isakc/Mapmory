package com.mapmory.unit.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import com.mapmory.services.user.domain.Login;
import com.mapmory.services.user.service.LoginService;
import com.mapmory.services.user.service.UserService;

@SpringBootTest
@Transactional
public class LoginServiceTest {

	@Autowired
	private LoginService loginService;
	
	@Autowired
	private UserService userService;
	
	// @Test
	@DisplayName("로그인 - 정상 상태")
	public void testInsert() throws Exception {
		
		userService.setupForTest();
		
		Login loginData = Login.builder()
					.userId("user1")
					.userPassword("password1")
					.build();
				
		
		
		String dbData = userService.getPassword("user1");
		
		boolean result = loginService.login(loginData, dbData);

		Assertions.assertThat(result).isTrue();
	}
	
	// @Test
	@DisplayName("로그인 - 비밀번호를 잘못 입력한 상태")
	public void testInsertWrong() throws Exception {
		
		userService.setupForTest();
		
		Login loginData = Login.builder()
					.userId("user1")
					.userPassword("kkkkkk")
					.build();
		
		String dbData = userService.getPassword("user1");
		
		boolean result = loginService.login(loginData, dbData);

		Assertions.assertThat(result).isFalse();
	}
}
