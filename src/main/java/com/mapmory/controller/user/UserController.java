package com.mapmory.controller.user;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.mapmory.common.domain.SessionData;
import com.mapmory.common.util.ContentFilterUtil;
import com.mapmory.common.util.ObjectStorageUtil;
import com.mapmory.common.util.RedisUtil;
import com.mapmory.services.user.abs.TacConstants;
import com.mapmory.services.user.domain.Login;
import com.mapmory.services.user.domain.TermsAndConditions;
import com.mapmory.services.user.domain.User;
import com.mapmory.services.user.service.LoginService;
import com.mapmory.services.user.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	@Qualifier("userServiceImpl")
	private UserService userService;
	
	@Autowired
	private LoginService loginService;
	
	@Autowired
	private RedisUtil<SessionData> redisUtil;
	
	@Autowired
	@Qualifier("objectStorageUtil")
	private ObjectStorageUtil objectStorageUtil;
	
	@Autowired
	private ContentFilterUtil contentFilterUtil;
	
	@Value("${object.profile.folderName}")
	private String PROFILE_FOLDER_NAME;
	
	
	// test용
	@GetMapping("/setupForTest")
	public void setupForTest() {
		
		// db script 사용 이후 최초 로그인 시 반드시 사용할 것(암호화 로직을 적용하여 user password를 갈아엎음)
		userService.setupForTest();
		System.out.println("\n\n암호화 적용 성공! template error는 무시해주세요~~");
	}
	
	@PostMapping("/login")
	public void login(@ModelAttribute Login login, @RequestParam(required=false) String keepLogin,  HttpServletResponse response) throws Exception{

		boolean keep;
		
		if(keepLogin == null)
			keep = false;
		else
			keep = true;

		String userId = login.getUserId();
		byte role = userService.getDetailUser(userId).getRole();
		// System.out.println("role : " + role);
		String sessionId = UUID.randomUUID().toString();
		// System.out.println("sessionId : " + sessionId);
		if ( !loginService.setSession(login, role, sessionId, keep))
			throw new Exception("redis에 값이 저장되지 않음.");
		
		Cookie cookie = createCookie(sessionId);
		response.addCookie(cookie);
		
		if(role == 1)
			response.sendRedirect("/map");  // 성문님께서 구현되는대로 적용 예정
		else
			response.sendRedirect("/user/admin/adminMain");
	}
	
	@PostMapping("/logout")
	public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {

		loginService.logout(request, response);
	}
	
	@GetMapping("/getSignUpView")
	public void getSignUpView(Model model) {
		
		model.addAttribute("user", User.builder().build());
		
	}	
	
	@PostMapping("/signUp")
	public String postSignUpView(@ModelAttribute User user, Model model) throws Exception {
		
		boolean isDone = userService.addUser(user.getUserId(), user.getUserPassword(), user.getUserName(), user.getNickname(), user.getBirthday(), user.getSex(), user.getEmail(), user.getPhoneNumber());
		
		if( !isDone) {
			
			throw new Exception("회원가입에 실패했습니다.");
		}
		
		return "redirect:/";
	}
	

	@GetMapping("/getAgreeTermsAndConditionsList")
	public String getAgreeTermsAndConditionsList(HttpServletRequest request, Model model) throws Exception {
		

		List<TermsAndConditions> tacList = userService.getTermsAndConditionsList();
	
		int role = redisUtil.getSession(request).getRole();

		// 관리자는 redirect, 사용자는 forward시키고 싶은데, 현재로써는 방법을 모르겠다.

		if (role == 0) {
			
			model.addAttribute("tacList", tacList);
			return "/user/admin/getAdminTermsAndConditionsList";
		} else {
			model.addAttribute("tacList", tacList);
			return "/user/getAgreeTermsAndConditionsList";	
		}
	}
	
	
	@GetMapping("/getDetailAgreeTermsAndConditions")
	public String getDetailAgreeTermsAndConditions(@RequestParam Integer tacType, HttpServletRequest request, Model model) throws Exception {
		
		TermsAndConditions tac = userService.getDetailTermsAndConditions(TacConstants.getFilePath(tacType));
		
		int role = redisUtil.getSession(request).getRole();
		
		
		if(role == 0) {
			
			model.addAttribute("tac", tac);
			return "/user/admin/getAdminDetailTermsAndConditions";
		} else {
			
			model.addAttribute("tac", tac);
			return "/user/getUserDetailTermsAndConditions";
		}
			
	}
	
	@GetMapping("/getPersonalSecurityMenu")
	public void getPersonalSecurityMenu() {
		
	}
	
	@GetMapping("/getIdView")
	public void getIdView() {
		
	}
	
	@PostMapping("/getId")
	public void postIdView() {
		
		
	}
	
	@GetMapping("/getPasswordView")
	public void getPasswordView() {
		
	}
	
	@GetMapping("/getGoogleLoginView")
	public void getGoogleLoginView() {
		
		
	}
	
	@GetMapping("/getNaverLoginView")
	public void getNaverLoginView() {
		
	}
	
	@GetMapping("/getKakaoLoginView")
	public void getKakaoLoginView() {
		
	}
	
	@GetMapping("/getUserInfo")
	public void getUserInfo() {
		
	}
	
	@GetMapping("/getProfile")
	public void getProfile() {
		
	}
	
	@GetMapping("/getFollowList")
	public void getFollowList() {
		
	}
	
	@GetMapping("/getFollowerList")
	public void getFollowerList() {
		
	}
	
	
	@GetMapping("/getLeaveAccountView")
	public void getLeaveAccountView() {
		
	}
	
	@GetMapping("/getRecoverAccountView")
	public void getRecoverAccountView() {
		
	}
	
	@GetMapping("/getUpdateUserInfoView")
	public void getUpdateUserInfoView() {
		
	}

	@GetMapping("/getUpdateProfileView")
	public void getUpdateProfileView(Model model) throws Exception {
		
		String userId = "user1";
		
		User user = userService.getDetailUser(userId);
		
		String cdnPath = objectStorageUtil.getImageUrl(user.getProfileImageName(), PROFILE_FOLDER_NAME);
		
		model.addAttribute("profileImage", cdnPath);
		
	}
	
	@GetMapping("/getUpdateSecondaryAuthView")
	public void getUpdateSecondaryAuthView() {
		
	}
	
	@GetMapping("/getUpdatePasswordView")
	public void getUpdatePasswordView() {
		
	}
	
	@GetMapping("/getSocialLoginLinkedView")
	public void getSocialLoginLinkedView() {
		
	}

	
	
	@PostMapping("/updateProfile")
	public void postUpdateProfile(@RequestParam(name = "profile") MultipartFile file, @RequestParam String introduction, Model model) throws Exception {
		
		String userId = "user1";
		
		if(contentFilterUtil.checkBadImage(file)) {
			System.out.println("부적절한 이미지입니다.");
		}
		
		boolean result = userService.updateProfile(file, userId, file.getOriginalFilename(), introduction);
		System.out.println(result);
		
		User user = userService.getDetailUser(userId);
		
		String cdnPath = objectStorageUtil.getImageUrl(user.getProfileImageName(), PROFILE_FOLDER_NAME);
		
		model.addAttribute("profileImage", cdnPath);
	}

	
	@GetMapping("/updatePasswordView")
	public void updatePasswordView() {
		
	}
	
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	//// admin ////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	
	
	@GetMapping("/admin/getAdminMain")
	public void getAdminMain() {
		
	}
	
	@GetMapping("/admin/getAdminUserList")
	public void getAdminUserList() {
		
	}
	
	@GetMapping("/admin/getAdminDetailUser")
	public void getAdminDetailUser() {
		
	}
	

	private Cookie createCookie(String sessionId) {
		
		Cookie cookie = new Cookie("JSESSIONID", sessionId);
		cookie.setPath("/");
		// cookie.setDomain("mapmory.life");
		// cookie.setSecure(true);
		cookie.setHttpOnly(true);
		cookie.setMaxAge(30 * 60);
		
		return cookie;
	}
}
