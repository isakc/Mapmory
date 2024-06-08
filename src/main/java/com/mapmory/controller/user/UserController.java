package com.mapmory.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.mapmory.common.util.ObjectStorageUtil;
import com.mapmory.services.user.domain.User;
import com.mapmory.services.user.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	@Qualifier("userServiceImpl")
	private UserService userService;
	
	
	@Autowired
	@Qualifier("objectStorageUtil")
	private ObjectStorageUtil objectStorageUtil;
	
	@Value("${object.profile.folderName}")
	private String PROFILE_FOLDER_NAME;


	@GetMapping("/testUpdateProfile")
	public void testGetUpdateProfile(Model model) throws Exception {
		
		String userId = "user1";
		
		User user = userService.getDetailUser(userId);
		
		// ByteArrayResource profile_image_cdn = objectStorageUtil.getImageResource(user.getProfileImageName(), PROFILE_FOLDER_NAME);
		
	}
	
	@PostMapping("/testUpdateProfile")
	public void testPostUpdateProfile(@RequestParam(name = "profile") MultipartFile file, @RequestParam String introduction) throws Exception {
		
		String userId = "user1";
		
		boolean result = userService.updateProfile(file, userId, file.getOriginalFilename(), introduction);
		System.out.println(result);
	}
}
