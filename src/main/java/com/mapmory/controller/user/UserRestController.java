package com.mapmory.controller.user;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base32;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mapmory.common.domain.Search;
import com.mapmory.common.domain.SessionData;
import com.mapmory.common.util.ContentFilterUtil;
import com.mapmory.common.util.ObjectStorageUtil;
import com.mapmory.common.util.RedisUtil;
import com.mapmory.services.timeline.domain.Record;
import com.mapmory.services.timeline.service.TimelineService;
import com.mapmory.services.user.domain.FollowMap;
import com.mapmory.services.user.domain.FollowSearch;
import com.mapmory.services.user.domain.Login;
import com.mapmory.services.user.domain.LoginDailyLog;
import com.mapmory.services.user.domain.LoginMonthlyLog;
import com.mapmory.services.user.domain.LoginSearch;
import com.mapmory.services.user.domain.SuspensionDetail;
import com.mapmory.services.user.domain.User;
import com.mapmory.services.user.domain.auth.google.GoogleAuthenticatorKey;
import com.mapmory.services.user.domain.auth.google.GoogleUserOtpCheck;
import com.mapmory.services.user.domain.auth.naver.NaverProfile;
import com.mapmory.services.user.dto.CheckDuplicationDto;
import com.mapmory.services.user.service.LoginService;
import com.mapmory.services.user.service.UserService;

@RestController
@RequestMapping("/user/rest")
public class UserRestController {

	@Value("${captcha.api.url}")
    private String clientId;

    @Value("${captcha.api.secret.key}")
    private String clientSecret;
	
    @Value("${server.host.name}")
	private String hostName;
	
	@Autowired
	JavaMailSenderImpl mailSender;
    
    @Value("${spring.mail.username}")
    private String emailId;
    
	@Autowired
	private UserService userService;
	
	@Autowired
	private LoginService loginService;
	
	
	@Autowired
	private TimelineService timelineService;
	
	@Autowired
	private RedisUtil<SessionData> redisUtil;
	
	// 소셜 로그인용
	@Autowired
	private RedisUtil<String> redisUtilString;
	
	@Autowired
	private RedisUtil<Integer> redisUtilInteger;

	
	@Autowired
	private RedisUtil<Map> redisUtilMap;
	
	@Autowired
	private ContentFilterUtil contentFilterUtil;
	
	@Autowired
	@Qualifier("objectStorageUtil")
	private ObjectStorageUtil objectStorageUtil;
	
	@Value("${page.Size}")
	private int pageSize;
	
	@Value("${object.profile.folderName}")
	private String PROFILE_FOLDER_NAME;
	
	@Value("${kakao.client.Id}")
    private String kakaoClientId;
    
    @Value("${kakao.redirect.uri}")
    private String kakaoRedirectUri;
	
	////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////
	
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody Map<String, String> map, HttpServletResponse response) throws Exception {
			
		String userId = map.get("userId");
		String password = map.get("userPassword");
		
		if(userId.isEmpty())
			throw new Exception("아이디가 비어있습니다.");
		
		if(password.isEmpty())
			throw new Exception("비밀번호가 비어있습니다.");
		
		Login loginData = Login.builder()
				.userId(userId)
				.userPassword(password)
				.build();
		
		String encodedPassword = userService.getPassword(userId);
		boolean isValid = loginService.login(loginData, encodedPassword);
		
		if( !isValid) {

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("wrong password");
			
		} else {
			
			Map<String, String> resultMap = userService.checkSuspended(userId);
			
			if(resultMap.get("isSuspended").equals("true")) {

				
				return ResponseEntity.ok(resultMap.get("endSuspensionDate"));
				
			} else {
				User user = userService.getDetailUser(userId);
				byte role = user.getRole();
				byte setSecondAuth = user.getSetSecondaryAuth();
				boolean keep = Boolean.valueOf(map.get("keepLogin"));
				boolean needToChangePassword = userService.checkPasswordChangeDeadlineExceeded(userId);
				
				if(setSecondAuth == 1) {
					
					// 임시 인증 쿠키 생성
					String uuid = UUID.randomUUID().toString();
					Map<String, String> tempMap = new HashMap<>();
					tempMap.put("userId", userId);
					tempMap.put("role", String.valueOf(role));
					tempMap.put("keepLogin", String.valueOf(keep));
					// tempMap.put("changePasword", String.valueOf(needToChangePassword));
					
					redisUtilMap.insert(uuid, tempMap, 5L);
					Cookie cookie = createCookie("SECONDAUTH", uuid, 60*5, "/user");
					response.addCookie(cookie);
					return ResponseEntity.ok("secondAuth");
					
				} else {
					
					acceptLogin(userId, role, response, keep);
					
					
					if(!needToChangePassword) {
					
						// 비밀번호 변경 후, 반드시 기존 쿠키와 세션을 제거할 것.
						return ResponseEntity.ok("passwordExceeded");  // 비밀번호 변경을 권장하기 위한 표시
						
					} else {

						if(role == 1)
							return ResponseEntity.ok("user");
						else
							return ResponseEntity.ok("admin");
						
					}	
				}
			}	
		}
	}

	@PostMapping("/signUp")
	public ResponseEntity<Boolean> signUp(@RequestBody User user, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		// transaction 처리 필요 - 소셜 로그인
		boolean isDone = userService.addUser(user.getUserId(), user.getUserPassword(), user.getUserName(), user.getNickname(), user.getBirthday(), user.getSex(), user.getEmail(), user.getPhoneNumber());
		
		if( !isDone) {
			
			throw new Exception("회원가입에 실패했습니다.");
			
		} else {
			
			// transaction 처리 필요
			// String kakaoId = redisUtil.select("", String.class);
			
			/*
			Map<String, String> map = getSocialIdByCookie(request, response);

	        if (map != null) {
	        	
	            System.out.println("Calling addSocialLoginLink");
	            userService.addSocialLoginLink(user.getUserId(), map.get("socialId"));
	            System.out.println("Finished addSocialLoginLink");
	            
	        }
	        */
			return ResponseEntity.ok(true);
		}
		
	}
	
	@PostMapping("/addFollow")
	public ResponseEntity<Boolean> addFollow(HttpServletRequest request, @RequestBody Map<String, String> value) {
		
		String userId = redisUtil.getSession(request).getUserId();
		String targetId = value.get("targetId");
		
		boolean result = userService.addFollow(userId, targetId);
		
		return ResponseEntity.ok(result);
		
	}

	
	@PostMapping("/getFollowList")
	public List<FollowMap> getFollowList(@ModelAttribute Search search, HttpServletRequest request) {
		
		/*
		if(currentPage == null)
			currentPage = 1;
		*/
		
		String myUserId = redisUtil.getSession(request).getUserId();
		String userId = search.getUserId();
		String keyword = search.getSearchKeyword();
		int currentPage = search.getCurrentPage();
		int selectFollow = 0;
		
		List<FollowMap> followList = userService.getFollowList(myUserId, userId, keyword, currentPage, pageSize, selectFollow);
		
		return followList;
	}
	
	@PostMapping("/getFollowerList")
	public List<FollowMap> getFollowerList(@ModelAttribute Search search, HttpServletRequest request) {

		/*
		if(currentPage == null)
			currentPage = 1;
		*/
		
		String myUserId = redisUtil.getSession(request).getUserId();
		String userId = search.getUserId();
		String keyword = search.getSearchKeyword();
		int currentPage = search.getCurrentPage();
		int selectFollow = 1;
		
		List<FollowMap> followerList = userService.getFollowList(myUserId, userId, keyword, currentPage, pageSize, selectFollow);
		
		return followerList;
	}
	
	@GetMapping("/getSharedList")
	public ResponseEntity<List<Record>> getSharedList(@RequestBody Map<String, String> value) throws Exception {
		
		String userId = value.get("userId");
		
		Search search=Search.builder()
				.userId(userId).
				currentPage(1)
				.limit(5)
				.sharedType(1)
				.tempType(1)
				.timecapsuleType(0)
				.build();
		
		List<Record> sharedList = timelineService.getTimelineList(search);
		
		return ResponseEntity.ok(null);
	}

	@PostMapping("/updateFollowState")
	public ResponseEntity<Boolean> updateFollowState() {
		
		return ResponseEntity.ok(true);
	}
	
	@PostMapping("/updatePassword")
	public ResponseEntity<Boolean> updatePassword(HttpServletResponse response) {
		
		
		
		if(true)
			return ResponseEntity.ok(true);
		else
			return ResponseEntity.ok(false);
	}
	
	@PostMapping("/updateSecondaryAuth")
	public ResponseEntity<String> updateSecondaryAuth(@RequestBody Map<String, String> value) {
		
		String userId = value.get("userId");
		
		boolean result = userService.updateSecondaryAuth(userId);
		
		if(result) {
			
			String secondAuthKeyName = "SECONDAUTH-"+userId;
			redisUtilString.delete(secondAuthKeyName);
			return ResponseEntity.ok("true");
		}
			
		else
			return ResponseEntity.internalServerError().body("설정 변경에 실패...");
	}
	
	@PostMapping("/deleteFollow")
	public ResponseEntity<Boolean> deleteFollow(HttpServletRequest request, @RequestBody Map<String, String> value) {
		
		String userId = redisUtil.getSession(request).getUserId();
		String targetId = value.get("targetId");
		
		boolean result = userService.deleteFollow(userId, targetId);
		
		return ResponseEntity.ok(result);
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
	
	@PostMapping("/checkSetSecondaryAuth")
	public ResponseEntity<Boolean> checkSetSecondaryAuth(@RequestBody Map<String, String> value) {
		
		String userId = value.get("userId");
		
		boolean result = userService.checkSetSecondaryAuth(userId);
		
		return ResponseEntity.ok(result);
	}
	
	@PostMapping(path="/checkBadWord")
	public ResponseEntity<Boolean> checkBadWord(@RequestBody Map<String, String> value) {
		
		String s = value.get("value");
		System.out.println("value : " + s);
		return ResponseEntity.ok(!contentFilterUtil.checkBadWord(s));
	}

	@PostMapping("/generateKey")
	public ResponseEntity<GoogleAuthenticatorKey> generateKey(@RequestBody Map<String, String> map) { 
		
		String userId = map.get("userId");
		String userName = map.get("userName");
		
		
		String secondAuthKeyName = "SECONDAUTH-"+userId;
		String encodedKey = redisUtilString.select(secondAuthKeyName, String.class);
		
		GoogleAuthenticatorKey returnKey = new GoogleAuthenticatorKey();
		
		if(encodedKey == null) {
			
			encodedKey = new String(userService.generateSecondAuthKey()); 
			returnKey.setEncodedKey(encodedKey);
			returnKey.setUserName(userName);
			returnKey.setHostName(hostName);
			redisUtilString.insert(secondAuthKeyName, encodedKey, 60*24*90L);
			
			/*
			 * 발급된 encodedKey를 가지고 본인의 Google Authenticator app에 추가한다.
			 * Google Authenticator app에서 QR을 통해서도 등록이 가능하다.
			 */
			return ResponseEntity.ok(returnKey); 
			
		} else {

			returnKey.setEncodedKey("");
			return ResponseEntity.ok(returnKey); 
		}

	}
	
	@PostMapping("/checkSecondaryKey")
	public ResponseEntity<Boolean> checkSecondaryKey(@RequestBody GoogleUserOtpCheck googleUserOtpCheck, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		boolean result = userService.checkSecondAuthKey(googleUserOtpCheck);
		
		if(result) {
		
			Cookie cookie = findCookie("SECONDAUTH", request);
			
			Map<String, String> map = redisUtilMap.select(cookie.getValue(), Map.class);
			String userId = map.get("userId");
			byte role = Byte.valueOf(map.get("role"));
			boolean keep = Boolean.valueOf(map.get("keepLogin"));

			acceptLogin(userId, role, response, keep);

			response.addCookie(createCookie("SECONDAUTH", "", 0, "/user"));
		}
		
		return ResponseEntity.ok(result);
	}
	
	@RequestMapping("/naver/auth/callback")
	public String naverCallback(@RequestParam String code, @RequestParam String state, Model model) throws Exception {
		
	    NaverProfile profileInfo = userService.getNaverProfile(code, state);
	    
	    String userId = userService.getUserIdBySocialId(profileInfo.getId());
	    
	    // 회원가입 페이지로 이동
	    if(userId == null) {

	    	model.addAttribute("profileInfo", profileInfo);
	    	return "redirect:/user/getAgreeTermsAndConditionsList";
	    	
	    } else {
	    	
	    	return "redirect:/map";
	    	
	    }
	}
	
	
	@GetMapping("/getKakaoLoginView")
	public String getKakaoLoginView() {

        // 카카오 로그인 페이지로 리다이렉트
        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize?client_id=" + kakaoClientId + "&redirect_uri=" + kakaoRedirectUri + "&response_type=code";
        
        return "redirect:" + kakaoAuthUrl;
		
	}
	
	@GetMapping("/kakaoCallback")
    public String kakaoLogin(@RequestParam(value = "code", required = false) String code, HttpServletResponse response) {
        try {
        	
            String accessToken = userService.getKakaoAccessToken(code);
            String kakaoId = userService.getKakaoUserInfo(accessToken);
            
            if (kakaoId == null) {
                return "error"; // 카카오 사용자 정보가 없으면 에러 처리
            }

            String userId = userService.getUserIdBySocialId(kakaoId);
            if (userId == null) {
            	
                // 소셜 로그인 정보가 없는 경우
            	String uuid = UUID.randomUUID().toString();
                redisUtilString.insert("k-"+uuid, kakaoId, 30L); // Redis에 임시로 카카오 아이디 저장 (30분 만료)
                // Cookie cookie = createTempCookie("KAKAOKEY", uuid);
                Cookie cookie = createCookie("KAKAOKEY", uuid, 60 * 5, "/user/getSignUpView");
                response.addCookie(cookie);
                
                return "redirect:/user/getAgreeTermsAndConditionsList"; // 회원 가입 페이지로 리다이렉트
                
            } else {

            	User user = userService.getDetailUser(userId);
            	          	
            	String uuid = UUID.randomUUID().toString();
            	loginService.setSession(userId, user.getRole(), uuid, false);
            	Cookie cookie = createLoginCookie(uuid, false);
            	response.addCookie(cookie);
            	
                return "redirect:/map"; // 메인 페이지로 리다이렉트   
                
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
	
	
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	//// admin ////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	
	@PostMapping("/admin/addSuspendUser")
	public ResponseEntity<Boolean> addSuspendUser(@RequestBody Map<String, String> map) throws Exception {
		
		String userId = map.get("userId");
		String reason = map.get("reason");
		
		boolean result = userService.addSuspendUser(userId, reason);
		
		if(result == true)
			return ResponseEntity.ok(true);
		else
			return ResponseEntity.internalServerError().body(false);
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
	
	@PostMapping("/admin/deleteSuspendUser")
	public ResponseEntity<Boolean> deleteSuspendUser(@RequestBody Map<String, String> map) throws Exception {
		
		String userId = map.get("userId");
		
		List<SuspensionDetail> list = userService.getSuspensionLogListActually(userId).getSuspensionDetailList();
		
		int logNo = list.get(list.size()-1).getLogNo();
		
		boolean result = userService.deleteSuspendUser(logNo);
		
		if(result == true)
			return ResponseEntity.ok(true);
		else
			return ResponseEntity.internalServerError().body(false);
	}

	
	
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	//// jaemin ////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	
	
	@PostMapping("/sendPhoneNumberAuthNum")
	public ResponseEntity<Boolean> sendPhoneNumberAuthNum(@RequestParam(value="to") String to, HttpServletResponse response) throws Exception {
		int codeValue = userService.PhoneNumberCheck(to);
		
		String codeKey = "p-"+UUID.randomUUID().toString();
		// authMap.put(codeKey, codeValue);
		
		redisUtilInteger.insert(codeKey, codeValue, 3L);
		
		Cookie cookie = createCookie("PHONEAUTHKEY", codeKey, 60*5, "/user/getSignUpView");
		
		response.addCookie(cookie);
		
		return ResponseEntity.ok(true);
	}
	
	@PostMapping("/sendEmailAuthNum")
	@ResponseBody
	public ResponseEntity<Boolean> sendEmailAuthNum(String email, HttpServletResponse response) {


		//난수의 범위 111111 ~ 999999 (6자리 난수)
		Random random = new Random();
		int codeValue = random.nextInt(888888)+111111;

		//이메일 보낼 양식
		String setFrom = emailId; //2단계 인증 x, 메일 설정에서 POP/IMAP 사용 설정에서 POP/SMTP 사용함으로 설정o
		String toMail = email;
		String title = "회원가입 인증 이메일";
		String content = "인증 코드 : " + codeValue;

		try {
			MimeMessage message = mailSender.createMimeMessage(); //Spring에서 제공하는 mail API
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			helper.setFrom(setFrom,"mapmory");
			helper.setTo(toMail);
			helper.setSubject(title);
			helper.setText(content, true);
			mailSender.send(message);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("발급된 인증번호 : " +codeValue);
		
		String codeKey = "e-"+UUID.randomUUID().toString();
		
		redisUtilInteger.insert(codeKey, codeValue, 3L);
		
		Cookie cookie = createCookie("EMAILAUTHKEY", codeKey, 60*5, "/user/getSignUpView");
		
		response.addCookie(cookie);
		
		return ResponseEntity.ok(true);
	}
	
	
	@PostMapping("/checkAuthNum")
	public ResponseEntity<Boolean> checkAuthNum(@RequestBody Map<String, String> value) {
		
		String codeKey = value.get("codeKey");
		int codeValue = Integer.parseInt(value.get("codeValue"));
		
		System.out.println(redisUtilInteger.select(codeKey, Integer.class));
		if( codeValue == redisUtilInteger.select(codeKey, Integer.class))
			return ResponseEntity.ok(true);
		else
			return ResponseEntity.ok(false);
	}
	
	@GetMapping("/nkey")
    public ResponseEntity<String> getCaptchaKey(@RequestParam("code") String code) {
        try {
            String apiURL = "https://naveropenapi.apigw.ntruss.com/captcha/v1/nkey?code=" + code;
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
            con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            return new ResponseEntity<>(response.toString(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
 
    @GetMapping("/image")
    public void getCaptchaImage(@RequestParam("key") String key, HttpServletResponse response) throws Exception {
        try {
            String apiURL = "https://naveropenapi.apigw.ntruss.com/captcha-bin/v1/ncaptcha?key=" + key + "&X-NCP-APIGW-API-KEY-ID=" + clientId;
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                // 이미지를 응답으로 전송
                response.setContentType("image/jpeg"); // 이미지 형식에 맞게 Content-Type 설정
                InputStream is = con.getInputStream();
                OutputStream os = response.getOutputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.flush();
                os.close();
                is.close();
            } else {
                // 오류 응답 전송
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Failed to fetch captcha image");
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyCaptcha(@RequestParam("key") String key, @RequestParam("value") String value) {
        try {
            String code = "1";
            String apiURL = "https://naveropenapi.apigw.ntruss.com/captcha/v1/nkey?code=" + code + "&key=" + key + "&value=" + value;
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
            con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            return new ResponseEntity<>(response.toString(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/image/{uuid}")
    public byte[] getImage(@PathVariable String uuid) throws Exception {
        byte[] bytes = objectStorageUtil.getImageBytes(uuid, PROFILE_FOLDER_NAME);
        // System.out.println("바이트 입니다 ::::::::::::::::::::::" + Arrays.toString(bytes));
        return bytes;
    }
    
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	//// utility ////////////////////////////////////////////////////////////
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
    
    
    private void acceptLogin(String userId, byte role, HttpServletResponse response, boolean keep) throws Exception {

		String sessionId = UUID.randomUUID().toString();
		if ( !loginService.setSession(userId, role, sessionId, keep))
			throw new Exception("redis에 값이 저장되지 않음.");
		
		Cookie cookie = createLoginCookie(sessionId, keep);
		response.addCookie(cookie);
		
		userService.addLoginLog(userId);
		// boolean isLog = userService.addLoginLog(userId);
		// System.out.println("로그인 로그 등록 여부 : " + isLog);
		
		/*
		boolean wantToChangePassword = changePassword; 
		if(wantToChangePassword) {
			
			return "redirect:/user/getUpdatePasswordView";
			
		} else {
			
			if(role == 1) {
				
				return "redirect:/map";
				
			} else {
				
				return "redirect:/user/admin/adminMain";
				
			}
		}
		*/
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
    
    private Map<String, String> getSocialIdByCookie(HttpServletRequest request, HttpServletResponse response) {
		
		Map<String, String> map = new HashMap<>();
		
		Cookie[] cookies = request.getCookies();
		
		for(Cookie cookie : cookies) {
			
			String cookieName = cookie.getName();
			
			if(cookieName.equals("KAKAOKEY")) {
				
				String keyName = cookie.getValue();
				String socialId = redisUtilString.select(keyName, String.class);
				cookie.setMaxAge(0);
				response.addCookie(cookie);
				
				map.put("socialId", socialId);
				map.put("type", "2");
				
				return map;
				
			} else if(cookieName.equals("NAVERKEY")) {
				
				String keyName = cookie.getValue();
				String socialId = redisUtilString.select(keyName, String.class);
				cookie.setMaxAge(0);
				response.addCookie(cookie);
				
				map.put("socialId", socialId);
				map.put("type", "2");
				
				return map;
				
			} else if(cookieName.equals("GOOGLEKEY")) {
				
				String keyName = cookie.getValue();
				String socialId = redisUtilString.select(keyName, String.class);
				cookie.setMaxAge(0);
				response.addCookie(cookie);
				
				map.put("socialId", socialId);
				map.put("type", "2");
				
				return map;
				
			} else {
				System.out.println("social login 가입이 아님");
			}
		}
		
		return null;
	}
    
}
