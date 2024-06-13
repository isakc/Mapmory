package com.mapmory.controller.user;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mapmory.common.util.ContentFilterUtil;
import com.mapmory.services.user.domain.Login;
import com.mapmory.services.user.domain.LoginDailyLog;
import com.mapmory.services.user.domain.LoginMonthlyLog;
import com.mapmory.services.user.domain.LoginSearch;
import com.mapmory.services.user.dto.CheckDuplicationDto;
import com.mapmory.services.user.service.LoginService;
import com.mapmory.services.user.service.UserService;

@RestController
@RequestMapping("/user/rest")
public class UserRestController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private LoginService loginService;
	
	@Autowired
	private ContentFilterUtil contentFilterUtil;
	
	@PostMapping("/login")
	public ResponseEntity<Boolean> login(@RequestBody Login loginData) throws Exception {
		
		if(loginData.getUserId().isEmpty())
			throw new Exception("아이디가 비어있습니다.");
		
		if(loginData.getUserPassword().isEmpty())
			throw new Exception("비밀번호가 비어있습니다.");
		
		
		String savedPassword = userService.getPassword(loginData.getUserId());
		boolean isValid = loginService.login(loginData, savedPassword);
		
		if( !isValid) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
		} else {
			return ResponseEntity.ok(true);
		}
	}


	@PostMapping("/updateFollowState")
	public ResponseEntity<Boolean> updateFollowState() {
		
		return ResponseEntity.ok(true);
	}
	
	@PostMapping("/updatePassword")
	public ResponseEntity<Boolean> updatePassword() {
		
		return ResponseEntity.ok(true);
	}
	
	@PostMapping("/updateSecondaryAuth")
	public ResponseEntity<Boolean> updateSecondaryAuth() {
		
		return ResponseEntity.ok(true);
	}
	
	@PostMapping("/recoverAccount")
	public ResponseEntity<Boolean> recoverAccount() {
		
		return ResponseEntity.ok(true);
	}
	
	@PostMapping("/sendAuthNum")
	public ResponseEntity<Boolean> sendAuthNum() {
		
		return ResponseEntity.ok(true);
	}
	
	@PostMapping("/checkDuplication")
	public ResponseEntity<Boolean> checkDuplication(@RequestBody CheckDuplicationDto dto) throws Exception {
		
		boolean result = false;
		
		if(dto.getType() == 0) {
			result = userService.checkDuplicationById(dto.getValue());
		} else if(dto.getType() == 1) {
			result = userService.checkDuplicationByNickname(dto.getValue()); 
		} else {
			throw new Exception("잘못된 type");
		}
		
		result = !result;  
		
		return ResponseEntity.ok(result);
	}
	
	@PostMapping(path="/checkBadWord")
	public ResponseEntity<Boolean> checkBadWord(@RequestBody Map<String, String> value) {
		
		String s = value.get("value");
		System.out.println("value : " + s);
		return ResponseEntity.ok(!contentFilterUtil.checkBadWord(s));
	}
	
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	//// admin ////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	
	@PostMapping("/admin/suspendUser")
	public ResponseEntity<Boolean> suspendUser() {
		
		return ResponseEntity.ok(true);
	}
	
	@PostMapping("/admin/getDailyLoginStatistics")
	public ResponseEntity<List<LoginDailyLog>> getDailyLoginStatistics() {
		
		LoginSearch search = null;
		
		List<LoginDailyLog> temp = userService.getUserLoginDailyList(search);
		
		return ResponseEntity.ok(temp);
	}
	
	@PostMapping("/admin/getMonthlyLoginStatistics")
	public ResponseEntity<List<LoginMonthlyLog>> getMonthlyLoginStatistics() {
		
		LoginSearch search = null;
		
		List<LoginMonthlyLog> temp = userService.getUserLoginMonthlyList(search);
		
		return ResponseEntity.ok(temp);
	}
	
}
