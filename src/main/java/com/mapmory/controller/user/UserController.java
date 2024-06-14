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

import com.mapmory.common.domain.Search;
import com.mapmory.common.domain.SessionData;
import com.mapmory.common.util.ContentFilterUtil;
import com.mapmory.common.util.ObjectStorageUtil;
import com.mapmory.common.util.RedisUtil;
import com.mapmory.services.purchase.domain.Subscription;
import com.mapmory.services.purchase.service.SubscriptionService;
import com.mapmory.services.timeline.domain.Record;
import com.mapmory.services.timeline.service.TimelineService;
import com.mapmory.services.user.abs.TacConstants;
import com.mapmory.services.user.domain.FollowMap;
import com.mapmory.services.user.domain.Login;
import com.mapmory.services.user.domain.Profile;
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
	private TimelineService timelineService;
	
	@Autowired
	private RedisUtil redisUtil;
	
	@Autowired
	private SubscriptionService subscriptionService;
	
	@Autowired
	@Qualifier("objectStorageUtil")
	private ObjectStorageUtil objectStorageUtil;
	
	@Autowired
	private ContentFilterUtil contentFilterUtil;
	
	@Value("${object.profile.folderName}")
	private String PROFILE_FOLDER_NAME;
	
    
	////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////
    
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
		
		Cookie cookie = createCookie(sessionId, keep);
		response.addCookie(cookie);
		
		boolean isLog = userService.addLoginLog(userId);
		System.out.println("로그인 로그 등록 여부 : " + isLog);
		
		if(role == 1)
			response.sendRedirect("/map");  // 성문님께서 구현되는대로 적용 예정
		else
			response.sendRedirect("/user/admin/adminMain");
	}
	
	@PostMapping("/logout")
	public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {

		loginService.logout(request, response);
	}
	
	// @GetMapping("/getSignUpView")  // get 방식으로 접근할 수 없게 막는다.
	@PostMapping("/getSignUpView")
	public void getSignUpView(Model model) {
		
		// model.addAttribute("user", User.builder().build());
		model.addAttribute("user", User.builder().build());
		
	}	
	
	@PostMapping("/signUp")
	public String postSignUpView(@ModelAttribute User user, Model model) throws Exception {
		
		boolean isDone = userService.addUser(user.getUserId(), user.getUserPassword(), user.getUserName(), user.getNickname(), user.getBirthday(), user.getSex(), user.getEmail(), user.getPhoneNumber());
		
		if( !isDone) {
			
			throw new Exception("회원가입에 실패했습니다.");
		}
		
		return "forward:/user/ok";
	}
	
	@RequestMapping("/ok")
	public void signUpOk() {
		
		
	}

	@GetMapping("/getAgreeTermsAndConditionsList")
	public void getAgreeTermsAndConditionsList(HttpServletRequest request, Model model) throws Exception {
		

		List<TermsAndConditions> tacList = userService.getTermsAndConditionsList();
		
		model.addAttribute("tacList", tacList);
	}
	
	
	@GetMapping("/getUserDetailTermsAndConditions")
	public void getDetailAgreeTermsAndConditions(@RequestParam Integer tacType, HttpServletRequest request, Model model) throws Exception {
		
		TermsAndConditions tac = userService.getDetailTermsAndConditions(TacConstants.getFilePath(tacType));
		
		model.addAttribute("tac", tac);
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
	public void getProfile(HttpServletRequest request, @RequestParam(required=false) String userId, Model model) throws Exception {
		
		String myUserId = redisUtil.getSession(request).getUserId();

		Profile profile;
		if( userId == null ) {
			
			profile = setProfileViewData(myUserId);
			model.addAttribute("myProfile", true);
			
		} else {
			
			profile = setProfileViewData(userId);
			model.addAttribute("myProfile", false);
			
			boolean isFollow = userService.checkFollow(myUserId, userId);
			
			System.out.println("isFollow : " + isFollow);
			if(isFollow == true) {
				model.addAttribute("isFollow", true);
			} else {
				model.addAttribute("isFollow", false);
			}
			
		}
				
		model.addAttribute("sessionId", myUserId);
		model.addAttribute("profile", profile);
		
		
	}
	
	@GetMapping("/getFollowList")
	public void getFollowList(@RequestParam String userId) {
		
	}
	
	@GetMapping("/getFollowerList")
	public void getFollowerList(@RequestParam String userId) {
		
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
	
	@GetMapping("/admin/getDetailTermsAndConditions")
	public void getAdminDetailAgreeTermsAndConditions(@RequestParam Integer tacType, HttpServletRequest request, Model model) throws Exception {
		
		TermsAndConditions tac = userService.getDetailTermsAndConditions(TacConstants.getFilePath(tacType));
		
		/*
		int role = redisUtil.getSession(request).getRole();
		

		if(role == 0) {
			
			model.addAttribute("tac", tac);
			return "/user/admin/getAdminDetailTermsAndConditions";
		} else {
			
			model.addAttribute("tac", tac);
			return "/user/getUserDetailTermsAndConditions";
		}
		*/
			
		model.addAttribute("tac", tac);
	}
	
	@GetMapping("/admin/getAdminUserList")
	public void getAdminUserList() {
		
	}
	
	@GetMapping("/admin/getAdminDetailUser")
	public void getAdminDetailUser() {
		
	}
	
	
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	//// jaemin ////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	
	// redis에 맞게 session 방식 변경 필요
	/*
	@GetMapping(value = "kakaoLogin")
    public String kakaoLogin(@RequestParam(value = "code", required = false) String code, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            String access_Token = userServiceJM.getKakaoAccessToken(code);
            String kakaoId = userServiceJM.getKakaoUserInfo(access_Token);

            if (kakaoId == null) {
                return "error"; // 카카오 사용자 정보가 없으면 에러 처리
            }
            
            SocialLoginInfo socialLoginInfo = SocialLoginInfo.builder()
                    .socialId(kakaoId)
                    .build();
            
            String tempSocialId = userService.getSocialId(socialLoginInfo);

            if (tempSocialId == null) {
                // 카카오 사용자 정보가 없으면 회원가입 페이지로 리다이렉트
                session.setAttribute("kakaoId", kakaoId);
                redirectAttributes.addAttribute("kakaoId", kakaoId);
                return "redirect:/user/addUser";
            }

            // 로그인 성공 처리
            session.setAttribute("tempSocialId", tempSocialId);
            return "index";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
    */
	
	private Cookie createCookie(String sessionId, boolean keepLogin) {
		
		Cookie cookie = new Cookie("JSESSIONID", sessionId);
		cookie.setPath("/");
		// cookie.setDomain("mapmory.life");
		// cookie.setSecure(true);
		cookie.setHttpOnly(true);
		
		if(keepLogin)
			cookie.setMaxAge(60 * 60 * 24 * 90 );
		else
			cookie.setMaxAge(30 * 60);
		
		return cookie;
	}
	
	private Profile setProfileViewData(String userId) throws Exception {
		
		User user = userService.getDetailUser(userId);
		user.setProfileImageName(objectStorageUtil.getImageUrl(user.getProfileImageName(), PROFILE_FOLDER_NAME));
		
		// boolean isSubscribed = subscriptionService.getDetailSubscription(userId).isSubscribed();
		boolean isSubscribed;
		Subscription subscription = subscriptionService.getDetailSubscription(userId);
		if(subscription == null || subscription.isSubscribed() == false) {
			
			isSubscribed = false;
			
		} else {
			
			isSubscribed = true;
			
		}
		
		int totalFollowCount = userService.getFollowListTotalCount(userId, null, 0, 0);
		int totalFollowerCount = userService.getFollowerListTotalCount(userId, null, 0, 0);
		
		Search search=Search.builder()
				.userId(userId).
				currentPage(1)
				.limit(5)
				.sharedType(1)
				.tempType(1)
				.timecapsuleType(0)
				.build();
		
		int totalSharedListCount = timelineService.getTimelineList(search).size();
		
		Profile profile = Profile.builder()
					.user(user)
					.isSubscribed(isSubscribed)
					.totalFollowCount(totalFollowCount)
					.totalFollowerCount(totalFollowerCount)
					.totalSharedListCount(totalSharedListCount)
					.build();
		
		return profile;
	}
}
