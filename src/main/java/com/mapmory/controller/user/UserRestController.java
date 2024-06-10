package com.mapmory.controller.user;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mapmory.services.user.domain.Login;

@RestController
@RequestMapping("/user/rest")
public class UserRestController {

	@PostMapping("/login")
	public void login(@RequestBody Login loginData) {
		
		
	}
}
