package com.mapmory.controller.user;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mapmory.common.domain.Search;
import com.mapmory.common.domain.SessionData;
import com.mapmory.common.util.ContentFilterUtil;
import com.mapmory.common.util.ObjectStorageUtil;
import com.mapmory.common.util.RedisUtil;
import com.mapmory.services.purchase.domain.Subscription;
import com.mapmory.services.purchase.service.SubscriptionService;
import com.mapmory.services.timeline.service.TimelineService;
import com.mapmory.services.user.abs.TacConstants;
import com.mapmory.services.user.domain.FollowMap;
import com.mapmory.services.user.domain.Profile;
import com.mapmory.services.user.domain.TermsAndConditions;
import com.mapmory.services.user.domain.User;
import com.mapmory.services.user.domain.auth.google.GoogleJwtPayload;
import com.mapmory.services.user.domain.auth.google.GoogleToken;
import com.mapmory.services.user.domain.auth.google.GoogleUserOtpCheck;
import com.mapmory.services.user.domain.auth.naver.NaverAuthToken;
import com.mapmory.services.user.domain.auth.naver.NaverProfile;
import com.mapmory.services.user.domain.auth.naver.NaverProfileResponse;
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
	private RedisUtil<Map> redisUtilMap;
	
	@Autowired
	private RedisUtil<String> redisUtilString;
	
	@Autowired
	private SubscriptionService subscriptionService;
	
	@Autowired
	@Qualifier("objectStorageUtil")
	private ObjectStorageUtil objectStorageUtil;
	
	@Autowired
	private ContentFilterUtil contentFilterUtil;
	
	@Value("${object.profile.folderName}")
	private String PROFILE_FOLDER_NAME;
	
	@Value("${page.Size}")
	private int pageSize;
	
	@Value("${google.client.id}")
	private String clientId;
	
	@Value("${google.client.secret}")
	private String clientSecret;
	
	@Value("${google.redirect.uri}")
	private String redirectUri;

	@Value("${google.redirect.uri}")
	private String googleTokenRequestUrl;

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
	public void getSignUpView(Model model, @RequestParam String[] checked) {
		
		// refactoring 필요... -> 무엇이 check되었는지를 파악해야 함
		System.out.println("checked : "+ Arrays.asList(checked));
		// model.addAttribute("user", User.builder().build());
		model.addAttribute("user", User.builder().build());
		
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
	public void getPersonalSecurityMenu(HttpServletRequest request, Model model) {
		
		String userId = redisUtil.getSession(request).getUserId();
		
		model.addAttribute("userId", userId);
		
	}
	
	@GetMapping("/getIdView")
	public void getIdView() {
		
	}
	
	@PostMapping("/getId")
	public void postIdView(@RequestParam Map<String, String> map,  Model model) {
		
		String userName = map.get("userName");
		String email = map.get("email");
		
		String userId= userService.getId(userName, email);
		
		model.addAttribute(userId);
		
	}
	
	@GetMapping("/getPasswordView")
	public void getPasswordView() {
		
	}
	

	@GetMapping("/getSecondaryAuthView")
	public void getSecondaryAuthView(Model model, HttpServletRequest request, @RequestParam String userId) {
		
		// login interceptor에서 직접 접근하는 것을 막아야 함
		
		Cookie cookie = findCookie("SECONDARYAUTH", request);
		// Map<String, String> map = redisUtilMap.select(cookie.getValue(), Map.class);
		// String userId = map.get("userId");
		
		model.addAttribute("userId", userId);
	}
	
	
	/*
	@GetMapping("/getGoogleLoginView")
	public void getGoogleLoginView() {
		
		
	}

	@GetMapping("/getNaverLoginView")
	public void getNaverLoginView() {
		
	}
	*/
	
	
	
	@GetMapping("/getUserInfo")
	public void getUserInfo() { 
		
	}
	
	@GetMapping("/getProfile")
	public void getProfile(HttpServletRequest request, @RequestParam(required=false) String userId, Model model) throws Exception {
		
		String myUserId = redisUtil.getSession(request).getUserId();

		Profile profile;
		if( userId.equals(myUserId) ) {
			
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
				
		System.out.println(profile);
		model.addAttribute("sessionId", myUserId);
		model.addAttribute("profile", profile);
		// model.addAttribute("profileImage", objectStorageUtil.getImageBytes(profile.getUser().getProfileImageName(), PROFILE_FOLDER_NAME));
		
	}
	
	@GetMapping("/getFollowList")
	public void getFollowList(@RequestParam String userId, Model model, HttpServletRequest request) {
		
		String myUserId = redisUtil.getSession(request).getUserId();
		
		List<FollowMap> followList = userService.getFollowList(myUserId, userId, null, 1, pageSize, 0);
		
		System.out.println(followList);
		
		// model.addAttribute("type", 0);
		model.addAttribute("list", followList);
		// model.addAttribute("sessionId", myUserId);
		// model.addAttribute("profileFolder",  PROFILE_FOLDER_NAME);
	}
	
	@GetMapping("/getFollowerList")
	public void getFollowerList(@RequestParam String userId, Model model, HttpServletRequest request) {
		
		String myUserId = redisUtil.getSession(request).getUserId();
		
		List<FollowMap> followerList = userService.getFollowList(myUserId, userId, null, 1, pageSize, 1);
		
		
		// model.addAttribute("type", 1);
		model.addAttribute("list", followerList);
		model.addAttribute("profileFolder", PROFILE_FOLDER_NAME);
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
		
		// String cdnPath = objectStorageUtil.getImageUrl(user.getProfileImageName(), PROFILE_FOLDER_NAME);
		
		ByteArrayResource cdnPath = objectStorageUtil.getImageResource(user.getProfileImageName(), PROFILE_FOLDER_NAME);
		
		model.addAttribute("profileImage", cdnPath);
		
	}

	@GetMapping("/getUpdatePasswordView")
	public void getUpdatePasswordView() {
		
	}

	
	@GetMapping("/getSocialLoginLinkedView")
	public void getSocialLoginLinkedView() {
		
	}

	
	
	@PostMapping("/updateProfile")
	public String postUpdateProfile(@RequestParam(name = "profile") MultipartFile file, @RequestParam String introduction, Model model, HttpServletRequest request) throws Exception {
		
		String userId = redisUtil.getSession(request).getUserId();
		
		if(contentFilterUtil.checkBadImage(file)) {
			System.out.println("부적절한 이미지입니다.");
		}
		
		boolean result = userService.updateProfile(file, userId, file.getOriginalFilename(), introduction);
		System.out.println(result);
		
		/*
		User user = userService.getDetailUser(userId);
		
		String cdnPath = objectStorageUtil.getImageUrl(user.getProfileImageName(), PROFILE_FOLDER_NAME);
		
		model.addAttribute("profileImage", cdnPath);
		*/
		
		return "redirect:/user/getProfile?userId="+userId;
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
	public String naverLogin(@RequestParam String code, @RequestParam String state, HttpServletResponse response) throws Exception {
		
	    NaverAuthToken token = userService.getNaverToken(code, state);
	    NaverProfile profileInfo = userService.getNaverProfile(code, state, token.getAccess_token());
	    
	    String naverId = profileInfo.getId();
	    String userId = userService.getUserIdBySocialId(naverId);
	    
	    // 회원가입 페이지로 이동
	    if(userId == null) {

	    	String uuid = UUID.randomUUID().toString();
        	String keyName = "n-"+uuid;
            redisUtilString.insert(keyName, naverId, 10L); 
            Cookie cookie = createCookie("NAVERKEY", keyName, 60 * 10, "/user");
            response.addCookie(cookie);
	    	
	    	// model.addAttribute("naverId", naverId);
	    	return "redirect:/user/getAgreeTermsAndConditionsList";
	    	
	    } else {
	    	
	    	User user = userService.getDetailUser(userId);
        	byte role=user.getRole();
        	
	    	acceptLogin(userId, role, response, false);
	    	return "redirect:/map";
	    	
	    }
	}
	
	@RequestMapping("/google/auth/callback")
	public void googleLogin(@RequestParam String code, HttpServletResponse response) throws Exception {

		System.out.println("code : " + code);
		
		/*
		// Parameter로 전달할 속성들 추가
	    Map<String, String> params = new HashMap<>();
	    params.put("grant_type","authorization_code");
	    params.put("client_id", clientId);
	    params.put("client_secret", clientSecret);
	    params.put("code", code);
	    params.put("redirect_uri", redirectUri);
	    
	    
	    URL url = new URL(googleTokenRequestUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        conn.setDoOutput(true);

        // 파라미터 문자열 생성
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, String> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(param.getKey());
            postData.append('=');
            postData.append(param.getValue());
        }

        // 파라미터 전송
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = postData.toString().getBytes("UTF-8");
            os.write(input, 0, input.length);
        }

        // 응답 읽기
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder res = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            res.append(inputLine);
        }
        in.close();
	    
	    System.out.println(res);
        */
		
		// GoogleJwtPayload payload = userService.getGoogleProfie(code);
		
		/*
		GoogleToken token = userService.getGoogleToken(code);
		System.out.println(token);
		GoogleJwtPayload payload = userService.getGoogleProfile(token.getId_token());

		
		
		
		String googleId = payload.getSub();
		String userId = userService.getUserIdBySocialId(googleId);
		
		if(userId == null) {
			
			String uuid = UUID.randomUUID().toString();
        	String keyName = "g-"+uuid;
            redisUtilString.insert(keyName, googleId, 10L); 
            Cookie cookie = createCookie("GOOGLEKEY", keyName, 60 * 10, "/user");
            response.addCookie(cookie);
	    	
	    	// model.addAttribute("googleId", googleId);
	    	return "redirect:/user/getAgreeTermsAndConditionsList";
	    	
		} else {
	    	
	    	User user = userService.getDetailUser(userId);
        	byte role=user.getRole();
        	
	    	acceptLogin(userId, role, response, false);
	    	return "redirect:/map";
	    	
	    }
				*/
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
	
	@GetMapping("/admin/getDetailTermsAndConditions")
	public void getAdminDetailAgreeTermsAndConditions(@RequestParam Integer tacType, HttpServletRequest request, Model model) throws Exception {
		
		TermsAndConditions tac = userService.getDetailTermsAndConditions(TacConstants.getFilePath(tacType));
		
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
	public void getAdminUserList() {
		
	}
	
	@GetMapping("/admin/getAdminDetailUser")
	public void getAdminDetailUser(@RequestParam String userId, Model model) {
		
		User user = userService.getDetailUser(userId);
		
		Map<String, String> map= userService.checkSuspended(userId);
		
		String isSuspended = map.get("isSuspended");
		
		if(isSuspended.equals("true"))
			user.setEndSuspensionDate(java.time.LocalDate.parse(map.get("endSuspensionDate")));
		
		model.addAttribute("user", user);
	}
	
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	//// jaemin ////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	
	@GetMapping("/kakaoCallback")
    public String kakaoLogin(@RequestParam(value = "code", required = false) String code, HttpServletResponse response) throws Exception {
        try {
        	
            String accessToken = userService.getKakaoAccessToken(code);
            String kakaoId = userService.getKakaoUserInfo(accessToken);
            
            if (kakaoId == null) {
                throw new Exception("소셜 연동 정보가 없습니다."); // 카카오 사용자 정보가 없으면 에러 처리
            }

            String userId = userService.getUserIdBySocialId(kakaoId);
            // 소셜 로그인 연동 정보가 없는 경우
            if (userId == null) {

            	String uuid = UUID.randomUUID().toString();
            	String keyName = "k-"+uuid;
                redisUtilString.insert(keyName, kakaoId, 10L); // 임시로 카카오 아이디 저장
                Cookie cookie = createCookie("KAKAOKEY", keyName, 60 * 10, "/user");
                response.addCookie(cookie);
                
                // response.sendRedirect("/user/getAgreeTermsAndConditionsList");
                return "redirect:/user/getAgreeTermsAndConditionsList"; // 회원 가입 페이지로 리다이렉트 
                
            } else {

            	// 로그인 처리
            	User user = userService.getDetailUser(userId);
            	byte role=user.getRole();    
            	/*
            	String uuid = UUID.randomUUID().toString();
            	loginService.setSession(userId, user.getRole(), uuid, false);
            	Cookie cookie = createLoginCookie(uuid, false);
            	response.addCookie(cookie);
            	*/
            	
            	acceptLogin(userId, role, response, false);
            	
                return "redirect:/map"; // 메인 페이지로 리다이렉트
            	// return ResponseEntity.ok("map");
            	
            }
            
        } catch (Exception e) {
            
            // return ResponseEntity.internalServerError().body(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

	/*
	// redis에 맞게 session 방식 변경 필요
 	@GetMapping(value = "/kakaoLogin")
     public String kakaoLogin(@RequestParam(value = "code", required = false) String code) {
 		
         try {
             String accessToken = userService.getKakaoAccessToken(code);
             String kakaoId = userService.getKakaoUserInfo(accessToken);

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
	
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	//// util ////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	
	private Cookie findCookie(String cookieKeyName, HttpServletRequest request) {
    	
    	System.out.println(request.getPathInfo());
    	System.out.println(request.getServletPath());
    	Cookie[] cookies = request.getCookies();
    	
    	for(Cookie cookie : cookies) {
    		
    		if(cookie.getName().equals(cookieKeyName)) {
    			
    			return cookie;
    			
    		}	
    	}
    	
    	return null;
    }
	
    private Cookie createCookie(String codeKeyName, String codeKey, int maxAge, String path) {
		
		Cookie cookie = new Cookie(codeKeyName, codeKey);
		cookie.setPath(path);
		// cookie.setDomain("mapmory.co.kr");
		// cookie.setSecure(true);
		cookie.setHttpOnly(false);
		cookie.setMaxAge(maxAge);
		
		return cookie;
	}
	
	private Cookie createTempCookie(String cookieId, String sessionId) {
		
		Cookie cookie = new Cookie(cookieId, sessionId);
		cookie.setPath("/");
		// cookie.setDomain("mapmory.co.kr");
		// cookie.setSecure(true);
		cookie.setHttpOnly(true);
		cookie.setMaxAge(60 * 10);
		
		return cookie;
	}
	
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
	
	private void acceptLogin(String userId, byte role, HttpServletResponse response, boolean keep) throws Exception {

		String sessionId = UUID.randomUUID().toString();
		if ( !loginService.setSession(userId, role, sessionId, keep))
			throw new Exception("redis에 값이 저장되지 않음.");
		
		Cookie cookie = createLoginCookie(sessionId, keep);
		response.addCookie(cookie);
		
		userService.addLoginLog(userId);
	}
    
    private Cookie createLoginCookie(String sessionId, boolean keepLogin) {
		
		Cookie cookie = new Cookie("JSESSIONID", sessionId);
		cookie.setPath("/");
		// cookie.setDomain("mapmory.life");
		// cookie.setSecure(true);
		cookie.setHttpOnly(true);
		
		if(keepLogin)
			cookie.setMaxAge(60 * 60 * 24 * 90 );
		else
			cookie.setMaxAge(30 * 60);
			// cookie.setMaxAge(-1);  redis쪽에서도 설정 필요
		
		return cookie;
	}
    
private HttpEntity<MultiValueMap<String, String>> makeTokenRequest(MultiValueMap<String, String> params) {
		
	    HttpHeaders headers = new HttpHeaders();
	    headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
	    HttpEntity<MultiValueMap<String, String>> googleTokenRequest = new HttpEntity<>(params, headers);
	    return googleTokenRequest;
	}
}
