package com.mapmory.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mapmory.services.user.domain.SocialLoginInfo;
import com.mapmory.services.user.service.UserService;
import com.mapmory.services.user.service.UserServiceJM;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/captcha")
public class UserRestControllerJM {

    @Value("${captcha.api.url}")
    private String clientId;

    @Value("${captcha.api.secret.key}")
    private String clientSecret;
    
    @Autowired
    @Qualifier("userServiceImplJM")
    UserServiceJM userServiceJM;
    
    @Autowired
    UserService userService;

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
    
//	@GetMapping(value = "/json/kakaoLogin")
//    public ResponseEntity<String> kakaoLogin(@RequestParam(value = "code", required = false) String code, HttpSession session, RedirectAttributes redirectAttributes) throws Exception{
//        try {
//            String access_Token = userServiceJM.getKakaoAccessToken(code);
//            String kakaoId = userServiceJM.getKakaoUserInfo(access_Token);
//
//            if (kakaoId == null) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("유저없음");
//            }
//            
//            String sociaLoginInfo = userService.getSocialId(kakaoId, 2);
//
//            if (sociaLoginInfo == null) {
//                // 카카오 사용자 정보가 없으면 회원가입 페이지로 리다이렉트
//                session.setAttribute("kakaoId", kakaoId);
//                redirectAttributes.addAttribute("kakaoId", kakaoId);
//                return ResponseEntity.status(HttpStatus.FOUND).body("redirect:/user/addUser");
//            }
//
//            // 로그인 성공 처리
//            session.setAttribute("user", sociaLoginInfo);
//            return ResponseEntity.status(HttpStatus.FOUND).body("redirect:/index.jsp");
//        } catch (HttpClientErrorException e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
//        }
//    }

}
