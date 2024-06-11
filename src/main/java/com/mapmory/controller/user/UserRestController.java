package com.mapmory.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mapmory.services.user.domain.Login;
import com.mapmory.services.user.service.LoginService;
import com.mapmory.services.user.service.UserService;

@RestController
@RequestMapping("/user/rest")
public class UserRestController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private LoginService loginService;
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Login loginData) throws Exception {
		
		if(loginData.getUserId().isEmpty())
			throw new Exception("아이디가 비어있습니다.");
		
		if(loginData.getUserPassword().isEmpty())
			throw new Exception("비밀번호가 비어있습니다.");
		
		
		String savedPassword = userService.getPassword(loginData.getUserId());
		boolean isValid = loginService.login(loginData, savedPassword);
		
		if( !isValid) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 틀렸습니다.");
		} else {
			return ResponseEntity.ok(true);
		}
	}
}
