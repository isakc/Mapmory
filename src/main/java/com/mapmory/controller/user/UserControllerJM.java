package com.mapmory.controller.user;

import java.util.Arrays;
import java.util.Random;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.mapmory.common.util.RedisUtil;
import com.mapmory.services.user.domain.SocialLoginInfo;
import com.mapmory.services.user.domain.User;
import com.mapmory.services.user.service.UserService;
import com.mapmory.services.user.service.UserServiceJM;


@Controller
@RequestMapping("/user/*")
public class UserControllerJM {
	
	@Autowired
    @Qualifier("userServiceImplJM")
    UserServiceJM userServiceJM;
    
    @Autowired
    UserService userService;
    
    @Autowired
    private RedisUtil<String> redisUtil;
    
    @Autowired
	JavaMailSenderImpl mailSender;
    
    @Value("${kakao.client.Id}")
    private String clientId;
    
    @Value("${kakao.redirect.uri}")
    private String redirectUri;
    
    @Value("${spring.mail.username}")
    private String emailId;
    
    @GetMapping("/addUser")
	public String addUser(Model model) throws Exception {
	    System.out.println("/user/addUser : GET");
	    return "user/addUser";
	}

    @PostMapping("/signUp")
    public String postSignUpView(@ModelAttribute User user, Model model) throws Exception {
        boolean isDone = userService.addUser(user.getUserId(), user.getUserPassword(), user.getUserName(), user.getNickname(), user.getBirthday(), user.getSex(), user.getEmail(), user.getPhoneNumber());

        if (!isDone) {
            throw new Exception("회원가입에 실패했습니다.");
        }

        String kakaoId = redisUtil.select("tempSocialId", String.class);

        if (kakaoId != null) {
            System.out.println("Calling addSocialLoginLink");
            userService.addSocialLoginLink(user.getUserId(), kakaoId);
            System.out.println("Finished addSocialLoginLink");
        }

        return "forward:/user/ok";
    }
    
    @PostMapping("/getSignUpView")
    public void getSignUpView(Model model, @RequestParam String[] checked) {
        // refactoring 필요... -> 무엇이 check되었는지를 파악해야 함
        System.out.println("checked : "+ Arrays.asList(checked));

        String kakaoId = redisUtil.select("tempSocialId", String.class);
        System.out.println("kakaoId: " + kakaoId);

        model.addAttribute("user", User.builder().build());
    }

    @GetMapping("/kakaoCallback")
    public String kakaoLogin(@RequestParam(value = "code", required = false) String code) {
        try {
            String access_Token = userServiceJM.getKakaoAccessToken(code);
            String kakaoId = userServiceJM.getKakaoUserInfo(access_Token);
            
            if (kakaoId == null) {
                return "error"; // 카카오 사용자 정보가 없으면 에러 처리
            }

            SocialLoginInfo socialLoginInfo = userServiceJM.socialLoginBySocialId(kakaoId);
            if (socialLoginInfo == null) {
                // 소셜 로그인 정보가 없는 경우
                redisUtil.insert("tempSocialId", kakaoId, 30); // Redis에 임시로 카카오 아이디 저장 (30분 만료)
                return "redirect:/user/getAgreeTermsAndConditionsList"; // 회원 가입 페이지로 리다이렉트
            } else {
                // 소셜 로그인 정보가 있는 경우
                // 로그인 성공 처리
                redisUtil.insert("socialId", kakaoId, 30); // Redis에 카카오 아이디 저장 (30분 만료)
                redisUtil.delete(kakaoId);
                System.out.println("카카오 아이디 레디스 삭제 여부 확인용 : :: : : :: : : :" + kakaoId);
                return "redirect:/map"; // 메인 페이지로 리다이렉트                
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
    
    @GetMapping("/kakaoLogin")
    public String kakaoLogin() throws Exception {
        // 클라이언트 ID와 리다이렉트 URI를 서버 측 코드에 저장

        // 카카오 로그인 페이지로 리다이렉트
        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize?client_id=" + clientId + "&redirect_uri=" + redirectUri + "&response_type=code";
        
        return "redirect:" + kakaoAuthUrl;
    }
	
	@PostMapping("/memberPhoneCheck")
	public @ResponseBody String memberPhoneCheck(@RequestParam(value="to") String to) throws Exception {

		return userServiceJM.PhoneNumberCheck(to);
	}
	
	@PostMapping("/EmailAuth")
	@ResponseBody
	public int emailAuth(String email) {


		//난수의 범위 111111 ~ 999999 (6자리 난수)
		Random random = new Random();
		int checkNum = random.nextInt(888888)+111111;

		//이메일 보낼 양식
		String setFrom = emailId; //2단계 인증 x, 메일 설정에서 POP/IMAP 사용 설정에서 POP/SMTP 사용함으로 설정o
		String toMail = email;
		String title = "회원가입 인증 이메일";
		String content = "인증 코드는 : " + checkNum + "이거야" +
				"<br>" +
				"해당 코드 입력해 ";

		try {
			MimeMessage message = mailSender.createMimeMessage(); //Spring에서 제공하는 mail API
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			helper.setFrom(setFrom,"쇼핌몰");
			helper.setTo(toMail);
			helper.setSubject(title);
			helper.setText(content, true);
			mailSender.send(message);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("숫자 확인용 : " +checkNum);
		return checkNum;
	}
}
