package com.mapmory.controller.user;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.mapmory.common.domain.SessionData;
import com.mapmory.common.util.ContentFilterUtil;
import com.mapmory.common.util.ObjectStorageUtil;
import com.mapmory.common.util.RedisUtil;
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

	
	// 이 메소드는 MainController에 GetMapping("/")에 위치하도록 옮겨줘야 한다. 그리고 로그인 화면 이름은 "index.html"로 변경할 것.
	@GetMapping("/getLoginView")
	public void getLoginView() {
		
		// db script 사용 이후 최초 로그인 시 반드시 사용할 것(암호화 로직을 적용하여 user password를 갈아엎음)
		// userService.setupForTest();
	}
	
	@PostMapping("/login")
	public void postLogin(@ModelAttribute Login login, @RequestParam(required=false) String keepLogin,  HttpServletResponse response) throws Exception{
		
		System.out.println("flag" + keepLogin);
		
		/*
		if ( !loginService.login(login, userService.getPassword(login.getUserId())) )
			throw new Exception("아이디 또는 비밀번호가 잘못되었습니다.");
		*/

		boolean keep;
		
		if(keepLogin == null)
			keep = false;
		else
			keep = true;

		String userId = login.getUserId();
		byte role = userService.getDetailUser(userId).getRole();
		String sessionId = UUID.randomUUID().toString();
		System.out.println("sessionId : " + sessionId);
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
	public void getSignUpView() {
		
	}
	
	// test 이후 삭제할 예정
	@GetMapping("/test")
	public void testSession(HttpServletRequest request) {
		
		SessionData temp = redisUtil.getSession(request);
		System.out.println(temp);
		
		System.out.println("_"+userService.getDetailUser(temp.getUserId())+"_"); 
	}
	
	
	
	@GetMapping("/getAgreeTermsAndConditionsList")
	public void getAgreeTermsAndConditionsList(Model model) throws Exception {
		
		/**
		 * 아니다, metadata에 file 경로를 명시한 후, numbering을 통해 matching을 시키도록 하자.
		 * 어차피 파일 경로를 노출하는 것이 보안 상 좋지 않을 뿐더러, 지금 체제에서 refactoring하고 싶지도 않기 때문이다.
		 * 
		 * 1. getTermsAndConditionsList()에서 List와 filePath가 담긴 map을 받는다.
		 * 2. 둘 다 view에 명시해둔다.
		 * 2. 사용자가 '자세히 보기'를 누른다.
		 * 3. 해당 경로에 대한 filePath를 받아서 getDetailAgreeTermsAndConditions()를 호출한다.
		 */
		
		List<TermsAndConditions> tacList = userService.getTermsAndConditionsList();
		
		model.addAttribute("tacList", tacList);
	}
	
	
	@GetMapping("/getDetailAgreeTermsAndConditions")
	public void getDetailAgreeTermsAndConditions() {
		
		
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
