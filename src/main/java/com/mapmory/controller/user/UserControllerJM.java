package com.mapmory.controller.user;

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
	JavaMailSenderImpl mailSender;
    
    @Value("${spring.mail.username}")
    private String emailId;
    
    @GetMapping("/addUser")
	public String addUser(Model model) throws Exception {
	    System.out.println("/user/addUser : GET");
	    return "user/addUser";
	}

    @PostMapping("/addUser")
    public String addUser(@ModelAttribute("user") User user,
                          @ModelAttribute("socialInfo") SocialLoginInfo socialLoginInfo,
                          HttpSession session) throws Exception {
        System.out.println("/user/addUser : POST");

        String kakaoId = (String) session.getAttribute("tempSocialId");
        if (kakaoId != null) {
            socialLoginInfo.setSocialId(kakaoId);
        }

        boolean result = userService.addUser(
            user.getUserId(),
            user.getUserPassword(),
            user.getUserName(),
            user.getNickname(),
            user.getBirthday(),
            user.getSex(),
            user.getEmail(),
            user.getPhoneNumber()
        );

        session.removeAttribute("tempSocialId"); // 세션에서 카카오 아이디 제거

        if (result) {
            return "index";
        } else {
            return "redirect:/user/addUser";
        }
    }

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
