package com.mapmory.controller.user;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mapmory.common.domain.Search;
import com.mapmory.common.domain.SessionData;
import com.mapmory.common.util.ContentFilterUtil;
import com.mapmory.common.util.RedisUtil;
import com.mapmory.services.timeline.domain.Record;
import com.mapmory.services.timeline.service.TimelineService;
import com.mapmory.services.user.domain.FollowMap;
import com.mapmory.services.user.domain.FollowSearch;
import com.mapmory.services.user.domain.Login;
import com.mapmory.services.user.domain.LoginDailyLog;
import com.mapmory.services.user.domain.LoginMonthlyLog;
import com.mapmory.services.user.domain.LoginSearch;
import com.mapmory.services.user.domain.User;
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
	
	@Autowired
	private RedisUtil<Integer> redisUtilAuthCode;
	
	@Autowired
	private ContentFilterUtil contentFilterUtil;
	
	@Value("${page.Size}")
	private int pageSize;
	
	////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////
	
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

	/*
	@PostMapping("/signUp")
	public ResponseEntity<Boolean> postSignUpView(@ModelAttribute User user, Model model) throws Exception {
		
		boolean isDone = userService.addUser(user.getUserId(), user.getUserPassword(), user.getUserName(), user.getNickname(), user.getBirthday(), user.getSex(), user.getEmail(), user.getPhoneNumber());
		
		if( !isDone) {
			
			throw new Exception("회원가입에 실패했습니다.");
		}
		
		return ResponseEntity.ok(true);
	}
	*/
	
	
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
		boolean selectFollow = true;
		
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
		boolean selectFollow = false;
		
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
	public ResponseEntity<Boolean> updatePassword() {
		
		return ResponseEntity.ok(true);
	}
	
	@PostMapping("/updateSecondaryAuth")
	public ResponseEntity<Boolean> updateSecondaryAuth() {
		
		return ResponseEntity.ok(true);
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
		
		redisUtilAuthCode.insert(codeKey, codeValue, 3L);
		
		Cookie cookie = createCookie("PHONEAUTHKEY", codeKey);
		
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
		
		redisUtilAuthCode.insert(codeKey, codeValue, 3L);
		
		Cookie cookie = createCookie("EMAILAUTHKEY", codeKey);
		
		response.addCookie(cookie);
		
		return ResponseEntity.ok(true);
	}
	
	
	@PostMapping("/checkAuthNum")
	public ResponseEntity<Boolean> checkAuthNum(@RequestBody Map<String, String> value) {
		
		String codeKey = value.get("codeKey");
		int codeValue = Integer.parseInt(value.get("codeValue"));
		
		System.out.println(redisUtilAuthCode.select(codeKey, Integer.class));
		if( codeValue == redisUtilAuthCode.select(codeKey, Integer.class))
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
	
    private Cookie createCookie(String codeKeyName, String codeKey) {
		
		Cookie cookie = new Cookie(codeKeyName, codeKey);
		cookie.setPath("/user/getSignUpView");
		// cookie.setDomain("mapmory.co.kr");
		// cookie.setSecure(true);
		cookie.setHttpOnly(false);
		cookie.setMaxAge(3 * 60);
		
		return cookie;
	}
}
