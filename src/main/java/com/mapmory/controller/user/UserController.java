package com.mapmory.controller.user;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mapmory.common.domain.Page;
import com.mapmory.common.domain.Search;
import com.mapmory.common.domain.SessionData;
import com.mapmory.common.util.CookieUtil;
import com.mapmory.common.util.ObjectStorageUtil;
import com.mapmory.common.util.RedisUtil;
import com.mapmory.services.purchase.domain.Subscription;
import com.mapmory.services.purchase.service.SubscriptionService;
import com.mapmory.services.timeline.service.TimelineService;
import com.mapmory.services.user.abs.TacConstants;
import com.mapmory.services.user.domain.FollowMap;
import com.mapmory.services.user.domain.Profile;
import com.mapmory.services.user.domain.SocialUserInfo;
import com.mapmory.services.user.domain.TermsAndConditions;
import com.mapmory.services.user.domain.User;
import com.mapmory.services.user.domain.auth.google.GoogleAuthenticatorKey;
import com.mapmory.services.user.domain.auth.google.GoogleUserOtpCheck;
import com.mapmory.services.user.domain.auth.naver.NaverAuthToken;
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
	private RedisUtil<SessionData> redisUtil;
	
	@Autowired
	private RedisUtil<String> redisUtilString;
	
	@Autowired
	private SubscriptionService subscriptionService;
	
	@Autowired
	@Qualifier("objectStorageUtil")
	private ObjectStorageUtil objectStorageUtil;
	
	@Value("${object.profile.folderName}")
	private String PROFILE_FOLDER_NAME;
	
	@Value("${page.Unit}")
    int pageUnit;

	@Value("${page.Size}")
	private int pageSize;
	
	@Value("${kakao.client.Id}")
    private String kakaoClientId;
    
    @Value("${kakao.redirect.uri}")
    private String kakaoRedirectUri;
    
    @Value("${naver.client.id}")
	private String naverClientId;
    
    @Value("${naver.client.secret}")
	private String naverClientSecret;
    
	@Value("${naver.redirect.uri}")
	private String naverRedirectUri;
    
	@Value("${naver.token.request.url}")
	private String naverTokenRequestUrl;
	
	@Value("${naver.profile.request.url}")
	private String profileRequestUrl;
	
	@Value("${naver.state}")
	private String naverState;

	@Value("${server.host.name}")
	private String hostName;
	
	@Value("${directory.path.tac}")
	private String tacPath;
	
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

	
	@PostMapping("/logout")
	public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {

		loginService.logout(request, response);		
		
	}
	
	// @GetMapping("/getSignUpView")  // get 방식으로 접근할 수 없게 막는다.
	@PostMapping("/getSignUpView")
	public String getSignUpView(Model model, @RequestParam String[] checked,
			HttpServletRequest request, HttpServletResponse response) {  // HttpServletResponse response
		
		// refactoring 필요... -> 무엇이 check되었는지를 파악해야 함
		System.out.println("checked : "+ Arrays.asList(checked));
		// model.addAttribute("user", User.builder().build());
		
		Cookie[] cookies = request.getCookies();
		
		if(cookies != null) {	
			SocialUserInfo socialUserInfo = userService.getSocialInfo(request);
			System.out.println("getSignUpview : : :: : : : : : : : : : : :" +socialUserInfo);
			model.addAttribute("socialUserInfo", socialUserInfo);
			
			Cookie emailCookie = new Cookie("EMAILAUTHKEY", null);
			emailCookie.setMaxAge(0);
			response.addCookie(emailCookie);
			
			Cookie phoneCookie = new Cookie("PHONEAUTHKEY", null);
			phoneCookie.setMaxAge(0);
			response.addCookie(phoneCookie);
		} else {
	         SocialUserInfo socialUserInfo = new SocialUserInfo();
	         socialUserInfo.setGender("U");
	         model.addAttribute("socialUserInfo",socialUserInfo);
	      }
				
		model.addAttribute("user", User.builder().build());
		
		System.out.println("controller finished");
		
		return "/user/getSignUpView";
	}	

	
	@GetMapping("/getAgreeTermsAndConditionsList")
	public void getAgreeTermsAndConditionsList(HttpServletRequest request, Model model) throws Exception {
		

		List<TermsAndConditions> tacList = userService.getTermsAndConditionsList();
		
		model.addAttribute("tacList", tacList);
	}
	
	@GetMapping("/getUserTermsAndConditionsList")
	public void getUserTermsAndConditionsList(HttpServletRequest request, Model model) throws Exception {
	
		List<TermsAndConditions> tacList = userService.getTermsAndConditionsList();
		
		model.addAttribute("tacList", tacList);
	}
	
	@GetMapping("/getUserDetailTermsAndConditions")
	public void getDetailAgreeTermsAndConditions(@RequestParam Integer tacType, HttpServletRequest request, Model model) throws Exception {
		
		String filePath = tacPath + TacConstants.getFilePath(tacType);
		
		TermsAndConditions tac = userService.getDetailTermsAndConditions(filePath);
		
		model.addAttribute("tac", tac);
	}
	
	
	/*
	@GetMapping("/getPersonalSecurityMenu")
	public void getPersonalSecurityMenu(HttpServletRequest request, Model model) {
		
		String userId = redisUtil.getSession(request).getUserId();
		
		model.addAttribute("userId", userId);
		
	}
	*/
	
	@GetMapping("/getIdView")
	public void getIdView() {
		
	}
	
	@GetMapping("/getPasswordView")
	public void getPasswordView() {
		
	}
	
	@PostMapping("/getPasswordView")
	public String postPasswordView(@RequestParam Map<String, String> map, HttpServletResponse response) {
		
		String userId = map.get("userId");
		String email = map.get("email");
		
		boolean result = userService.checkUserExist(userId, email);
		
		// 시간 되면 uuid로 변환할 것
		Cookie cookie = CookieUtil.createCookie("UPDATEPW", userId, 60*5, "/user");
		response.addCookie(cookie);
		
		if(result == true)
			return "redirect:/user/getUpdatePasswordView";
		else
			return "/error";
		
		
	}
	

	@GetMapping("/getSecondaryAuthView")
	public void getSecondaryAuthView(@RequestParam String userId, Model model) {
				
		model.addAttribute("userId", userId);
	}
	
	@GetMapping("/getAddSecondaryAuthView")
	public void getAddSecondaryAuthView(HttpServletRequest request, Model model) {
		
		String userId = redisUtil.getSession(request).getUserId();
		
		GoogleAuthenticatorKey returnKey = new GoogleAuthenticatorKey();
		String secondAuthKeyName = "SECONDAUTH-"+ userId;  // key name도 userId 기반 단방향 암호화 로직이 들어가야 한다.
		String encodedKey = new String(userService.generateSecondAuthKey());
		System.out.println("keyName : " + secondAuthKeyName);
		System.out.println("encodedKey : " + encodedKey);
		
		returnKey.setEncodedKey(encodedKey);
		//returnKey.setUserName(userId);
		//returnKey.setHostName(hostName);
		// 2단계 encodedkey는 RDBMS에 저장하는 것이 좋겠다.
		redisUtilString.insert(secondAuthKeyName, encodedKey, 60*24*90L);
		// boolean result = userService.updateSecondaryAuth(userId);
		boolean result = userService.updateSecondaryAuth(userId, 1);
		System.out.println("is set secondary auth changed? " + result);
		
		String keyQR = "https://quickchart.io/chart?cht=qr&chs=200x200&chl=otpauth://totp/"+userId+"@"+hostName+"?secret="+encodedKey+"&chld=H|0";
		
		model.addAttribute("authKey", returnKey);
		model.addAttribute("keyQR", keyQR);

		/*
		String userId = redisUtil.getSession(request).getUserId();
		
		
		String encodedKey = userService.generateSecondAuthKey();
		String userName = userService.getDetailUser(userId).getUserName();
		
		model.addAttribute("encodedKey", encodedKey);
		model.addAttribute("userName", userName);
		model.addAttribute("hostName", hostName);
		*/

	}
	
	@GetMapping("/getUserInfo")
	public void getUserInfo(HttpServletRequest request, Model model) { 
		
		String userId = redisUtil.getSession(request).getUserId();
		
		User user = userService.getDetailUser(userId);
		
		model.addAttribute("user", user);
		
	}
	
	@GetMapping("/getProfile")
	public void getProfile(HttpServletRequest request, @RequestParam(required=false) String userId, Model model) throws Exception {
		
		String myUserId = redisUtil.getSession(request).getUserId();

		Profile profile;
		if( (userId == null) || userId.equals(myUserId) ) {
			
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
				
		// System.out.println(profile);
		
		String profileImage = userService.getImage("profile", profile.getUser().getProfileImageName());
		model.addAttribute("profileImage", profileImage);
		
		String badgeImage = userService.getImage("profile", "sub.png");
		model.addAttribute("badgeImage", badgeImage);
		
		System.out.println("profileImage : " + profileImage);
		System.out.println("badgeImage : " + badgeImage);
		
		// model.addAttribute("userId", myUserId);
		model.addAttribute("sessionId", myUserId);
		model.addAttribute("profile", profile);
		// model.addAttribute("profileImage", objectStorageUtil.getImageBytes(profile.getUser().getProfileImageName(), PROFILE_FOLDER_NAME));
		
	}
	
	@GetMapping("/getFollowList")
	public void getFollowList(@RequestParam String profileUserId, Model model, HttpServletRequest request) {
		
		String myUserId = redisUtil.getSession(request).getUserId();
		
		List<FollowMap> followList = userService.getFollowList(myUserId, profileUserId, null, 1, pageSize, 0);
		
		System.out.println(followList);
		
		// model.addAttribute("type", 0);
		model.addAttribute("list", followList);
		// model.addAttribute("sessionId", myUserId);
		model.addAttribute("profileUserId", profileUserId);
		// model.addAttribute("sessionId", myUserId);
		// model.addAttribute("profileFolder",  PROFILE_FOLDER_NAME);
	}
	
	@GetMapping("/getFollowerList")
	public void getFollowerList(@RequestParam String profileUserId, Model model, HttpServletRequest request) {
		
		String myUserId = redisUtil.getSession(request).getUserId();
		
		List<FollowMap> followerList = userService.getFollowList(myUserId, profileUserId, null, 1, pageSize, 1);
		
		
		// model.addAttribute("type", 1);
		model.addAttribute("list", followerList);
		// model.addAttribute("profileFolder", PROFILE_FOLDER_NAME);
		model.addAttribute("profileUserId", profileUserId);
	}
	
	@GetMapping("/getRecoverAccountView")
	public void getRecoverAccountView() {
		
	}
	
	@GetMapping("/getUpdateUserInfoView")
	public void getUpdateUserInfoView(HttpServletRequest request, Model model) {
		
		String userId = redisUtil.getSession(request).getUserId();
		
		User user = userService.getDetailUser(userId);
		
		String[] temp = user.getPhoneNumber().split("-");
		String phoneNumber = temp[0] + temp[1] + temp[2];
		user.setPhoneNumber(phoneNumber);
		
		model.addAttribute("user", user);
		
	}

	@GetMapping("/getUpdateProfileView")
	public void getUpdateProfileView(Model model, HttpServletRequest requset) throws Exception {
		
		String userId = redisUtil.getSession(requset).getUserId();
		
		User user = userService.getDetailUser(userId);
		
		// String cdnPath = objectStorageUtil.getImageUrl(user.getProfileImageName(), PROFILE_FOLDER_NAME);
		
		/*
		ByteArrayResource cdnPath = objectStorageUtil.getImageResource(user.getProfileImageName(), PROFILE_FOLDER_NAME);
		System.out.println(cdnPath);
		
		model.addAttribute("profileImage", cdnPath);
		*/
		
		model.addAttribute("user",user );
		
	}

	@GetMapping("/getUpdatePasswordView")
	public void postUpdatePasswordView() {
		
		
	}
	
	
	@GetMapping("/getSocialLoginLinkedView")
	public void getSocialLoginLinkedView() {
		
	}

	// for navigation
	@PostMapping("/checkSecondaryKey")
	public String checkKey(@RequestBody GoogleUserOtpCheck googleUserOtpCheck, @RequestParam(required=false) String changePassword) throws InvalidKeyException, NoSuchAlgorithmException {
		
		
		boolean result = userService.checkSecondAuthKey(googleUserOtpCheck);
		
		if(result) {
			
			// acceptLogin(userId, role, response, keep, changePassword);
			return "redirect";
		} else {
			
			throw new InvalidKeyException("유효하지 않은 인증번호");
		}
	}
	
	@RequestMapping("/naver/auth/callback")
	public String naverLogin(HttpServletRequest request, @RequestParam String code, @RequestParam String state, HttpServletResponse response, Model model) throws Exception {
		
		System.out.println("Flag");
		
	    NaverAuthToken token = userService.getNaverToken(code, state);
	    // Map<String, Object> profileInfo = userService.getNaverProfile(code, state, token.getAccess_token());
	    SocialUserInfo profileInfo = userService.getNaverProfile(code, state, token.getAccess_token());
	    // System.out.println("naver profile:: " + profileInfo);
	    
	    // String naverId = profileInfo.getId();

	    String naverId = profileInfo.getId();
	    
	    String userId = userService.getUserIdBySocialId(naverId);
	    
	    System.out.println("naver 소셜 연동이 된 사용자? " + userId);
	    if(userId == null) {

	    	// 회원 : 소셜 로그인을 동록해줌
	    	SessionData sessionData = redisUtil.getSession(request);    	
	    	if(sessionData != null) {
	    		
	    		boolean result = userService.addSocialLoginLink(sessionData.getUserId(), naverId);
	    		
	    		if(result) {
	    			
	    			// 이 로직은 REST로 개선해야 사용자에게 성공 여부를 알릴 수 있다...
		    		System.out.println("네이버 소셜 로그인 등록 성공!");
		    		loginService.logout(request, response);
		    		return null;
		    		// return "redirect:/";
	    		} else {
	    			
	    			System.out.println("실패...");
	    			return "redirect:/user/getSocialLoginLinkedView";
	    		}
	    	} else {
	    		
	    		// 신규 : 회원가입 페이지로 이동
	    		System.out.println("신규 회원입니다. 회원가입 페이지로 이동합니다.");
		    	String uuid = UUID.randomUUID().toString();
	        	String keyName = "n-"+uuid;
	        	
	        	/*
	        	redisUtilString.insert(keyName, naverId, 10L);
	        	redisUtilMap.insert(keyName, profileInfo, 10L);
	        	*/
	        	userService.setSocialKey(keyName, profileInfo);
	        	
	            Cookie cookie = CookieUtil.createCookie("NAVERKEY", keyName, 60 * 10, "/user");
	            response.addCookie(cookie);
		    	
		    	// model.addAttribute("naverId", naverId);
		    	return "redirect:/user/getAgreeTermsAndConditionsList";
	    		
	    	}	    	
	    } else {
	    	
	    	// 로그인
	    	if(userService.checkSetSecondaryAuth(userId)) {
	    		
	    		model.addAttribute("userId", userId);
	    		// return "redirect:/user/getSecondaryAuthView/"+userId;
	    		return "redirect:/user/getSecondaryAuthView";
	    	}
	    	
	    	User user = userService.getDetailUser(userId);
        	byte role=user.getRole();
        	
	    	return doSocialLogin(userId, role, response);
	    }
	}

	@GetMapping("/getNaverLoginView")
	public String getNaverLoginView() {
		
		String naverAuthUrl = "https://nid.naver.com/oauth2.0/authorize?client_id="+naverClientId+"&redirect_url="+naverRedirectUri+"&response_type=code&state="+naverState;
		
		return "redirect:"+naverAuthUrl;
		
	}
	
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	//// admin ////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	
	// 로그인 통계를 보여줄 예정
	@GetMapping("/admin/getAdminMain")
	public void getAdminMain() {
		
		
		
	}
	
	@GetMapping("/admin/getAdminTermsAndConditionsList")
	public void getAdminTermsAndConditionsList(Model model) throws Exception {
		
		List<TermsAndConditions> tacList = userService.getTermsAndConditionsList();
		
		model.addAttribute("tacList", tacList);
	}
	
	@GetMapping("/admin/getAdminDetailTermsAndConditions")
	public void getAdminDetailAgreeTermsAndConditions(@RequestParam Integer tacType, HttpServletRequest request, Model model) throws Exception {
		
		String filePath = tacPath + TacConstants.getFilePath(tacType);
		
		TermsAndConditions tac = userService.getDetailTermsAndConditions(filePath);
		
		/*  login interceptor에서 할 일
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
	public void getAdminUserList(Model model, HttpServletRequest request, @ModelAttribute Search search) {

		if(search.getCurrentPage() == 0) {
			search.setCurrentPage(1);
		}
		
		search.setPageSize(pageSize);
		
		Map<String, Object> map = userService.getUserList(search);
		Page resultPage = new Page(search.getCurrentPage(),((Integer)map.get("count")).intValue(),pageUnit,pageSize);
		
		System.out.println("현재 page : " + resultPage);
		
       //  model.addAttribute("userList", (List<User>) map.get("userList"));
        model.addAttribute("userList",  map.get("userList"));
        model.addAttribute("search", search);
        model.addAttribute("resultPage", resultPage);
	}
	
	@GetMapping("/admin/getAdminDetailUser")
	public void getAdminDetailUser(@RequestParam String userId, Model model) {
		
		User user = userService.getDetailUser(userId);
		
		Map<String, String> suspendMap= userService.checkSuspended(userId);
		
		String isSuspended = suspendMap.get("isSuspended");
		
		if(isSuspended.equals("true")) {
			
			String endSuspensionDate = suspendMap.get("endSuspensionDate");
			// System.out.println("DB로부터 가져온 정지 시작 일");
			 user.setEndSuspensionDate(LocalDateTime.parse(endSuspensionDate).toLocalDate());
			 // System.out.println(user.getEndSuspensionDate());
		}
			
			// user.setEndSuspensionDate(java.time.LocalDate.parse(suspendMap.get("endSuspensionDate")));
		
		//  System.out.println("dddddddddddd ::::::: " + java.time.LocalDate.parse(suspendMap.get("endSuspensionDate")));
		// System.out.println(suspendMap.get("endSuspensionDate"));
		
		model.addAttribute("user", user);
		model.addAttribute("suspendMap", suspendMap);
	}
	
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	//// jaemin ////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	
	@GetMapping("/kakaoCallback")
    public String kakaoLogin(@RequestParam(value = "code", required = false) String code, HttpServletResponse response, HttpServletRequest request, Model model) throws Exception {
        try {
        	
            String accessToken = userService.getKakaoAccessToken(code);

            SocialUserInfo socialUserInfo = userService.getKakaoUserInfo(accessToken);

            
            /*
            if (kakaoId == null) {
                throw new Exception("소셜 연동 정보가 없습니다."); // 카카오 사용자 정보가 없으면 에러 처리
            }
            */
            
            String kakaoId = (String) socialUserInfo.getId();
            String userId = userService.getUserIdBySocialId(kakaoId);
            
            // 소셜 로그인 연동 정보가 없는 경우
            if (userId == null) {
            	
            	// 회원 : 소셜 로그인을 동록해줌
            	System.out.println("현재 로그인 상태인지 확인합니다...");
    	    	SessionData sessionData = redisUtil.getSession(request);    
    	    	System.out.println("is session null? " + sessionData);
    	    	if(sessionData != null) {
    	    		
    	    		System.out.println("현재 회원을 대상으로 소셜 로그인 연동을 진행합니다...");
    	    		boolean result = userService.addSocialLoginLink(sessionData.getUserId(), kakaoId);
    	    		
    	    		if(result) {
    	    			
    	    			// 이 로직은 REST로 개선해야 사용자에게 성공 여부를 알릴 수 있다...
    		    		System.out.println("카카오 소셜 로그인 등록 성공!");
    		    		loginService.logout(request, response);
    		    		// return "redirect:/";
    		    		return null;
    	    		} else {
    	    			
    	    			System.out.println("실패...");
    	    			return "redirect:/user/getSocialLoginLinkedView";
    	    		}
    	    	} else {
    	    		
    	    		System.out.println("신규 유저");
    	    		// 신규 : 회원가입 페이지로 이동
                	String uuid = UUID.randomUUID().toString();
                	String keyName = "k-"+uuid;

                	// redisUtilSocialUserInfo.insert(keyName, socialUserInfo, 10L);
                	userService.setSocialKey(keyName, socialUserInfo);

                    Cookie cookie = CookieUtil.createCookie("KAKAOKEY", keyName, 60 * 10, "/user");
                    response.addCookie(cookie);
                    
                    model.addAttribute("socialUserInfo", socialUserInfo);
                    // response.sendRedirect("/user/getAgreeTermsAndConditionsList");
                    return "redirect:/user/getAgreeTermsAndConditionsList"; // 회원 가입 페이지로 리다이렉트
    	    	}	
                
            } else {

            	// 로그인 처리
    	    	if(userService.checkSetSecondaryAuth(userId)) {
    	    		
    	    		model.addAttribute("userId", userId);
    	    		// return "redirect:/user/getSecondaryAuthView/"+userId;
    	    		return "redirect:/user/getSecondaryAuthView";
    	    	}
            	
            	User user = userService.getDetailUser(userId);
            	byte role=user.getRole();    
            	
            	return doSocialLogin(userId, role, response);
            }
            
        } catch (Exception e) {
            
            // return ResponseEntity.internalServerError().body(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }
	
	
	@GetMapping("/getKakaoLoginView")
	public String getKakaoLoginView() {
		
		String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize?client_id=" + kakaoClientId + "&redirect_uri=" + kakaoRedirectUri + "&response_type=code";
		
		return "redirect:"+kakaoAuthUrl;
		
	}

	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	//// util ////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////

	
	private Profile setProfileViewData(String userId) throws Exception {
		
		User user = userService.getDetailUser(userId);
		// user.setProfileImageName(objectStorageUtil.getImageUrl(user.getProfileImageName(), PROFILE_FOLDER_NAME));
		
		// boolean isSubscribed = subscriptionService.getDetailSubscription(userId).isSubscribed();
		boolean isSubscribed;
		Subscription subscription = subscriptionService.getDetailSubscription(userId);
		if(subscription == null || subscription.isSubscribed() == false) {
			
			isSubscribed = false;
			
		} else {
			
			isSubscribed = true;
			
		}
		
		int totalFollowCount = userService.getFollowListTotalCount(userId, null, 0, 0, 0);
		int totalFollowerCount = userService.getFollowListTotalCount(userId, null, 0, 0, 1);
		
		Search search = Search.builder()
				.userId(userId)
				.logsType(0).build();
		
		// int totalSharedListCount = timelineService.getTimelineList(search).size();
		int totalSharedListCount = timelineService.getProfileTimelineCount(search);
		System.out.println("profile count : " + totalSharedListCount);
		
		
		Profile profile = Profile.builder()
					.user(user)
					.isSubscribed(isSubscribed)
					.totalFollowCount(totalFollowCount)
					.totalFollowerCount(totalFollowerCount)
					.totalSharedListCount(totalSharedListCount)
					.build();
		
		return profile;
	}
	
	private String doSocialLogin(String userId, byte role, HttpServletResponse response) throws Exception {
    	
		System.out.println("소셜 로그인 성공!");
		
		// 소셜 로그인 회원은 보안 상 로그인 유지 설정 불가하게 만듦. 어차피 로그인하기 편해서 불편하지는 않을 것으로 생각됨.
    	loginService.acceptLogin(userId, role, response, false);
    	userService.addLoginLog(userId);


    	// 소셜 로그인 사용자는 비밀번호가 없기 때문에 비밀번호 변경 권장 메세지를 보낼 필요가 없음.
		System.out.println("role : " + role);
		if(role == 1)
			return "redirect:/map";
		else
			return "redirect:/user/admin/getAdminMain";
		
    }
	
    private HttpEntity<MultiValueMap<String, String>> makeTokenRequest(MultiValueMap<String, String> params) {
		
	    HttpHeaders headers = new HttpHeaders();
	    headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
	    HttpEntity<MultiValueMap<String, String>> googleTokenRequest = new HttpEntity<>(params, headers);
	    return googleTokenRequest;
	}
}