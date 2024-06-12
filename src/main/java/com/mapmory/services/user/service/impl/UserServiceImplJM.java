package com.mapmory.services.user.service.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mapmory.services.user.dao.UserDaoJM;
import com.mapmory.services.user.domain.User;
import com.mapmory.services.user.service.UserServiceJM;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;

@Transactional
@Service("userServiceImplJM")
public class UserServiceImplJM implements UserServiceJM {
	
	@Autowired
	UserDaoJM userDaoJM;
	
	@Value("${coolsms.apikey}")
	private String coolsmsApiKey;
	
	@Value("${coolsms.apisecret}")
	private String coolsmsSecret;
	
	@Value("${coolsms.fromnumber}")
	private String phoneNum;
	
	@Value("${kakao.client}")
	private String kakaoCilent;
	
	@Override
    public String getKakaoAccessToken (String authorize_code) {
        String access_Token = "";
        String refresh_Token = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //    POST 요청을 위해 기본값이 false인 setDoOutput을 true로

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //    POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id="+kakaoCilent );  //본인이 발급받은 key
            sb.append("&redirect_uri=http://localhost:8000/user/kakaoLogin&response_type=code");     // 본인이 설정해 놓은 경로
            sb.append("&code=" + authorize_code);
            System.out.println("authorize_code : " + authorize_code);
            bw.write(sb.toString());
            bw.flush();

            //    결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //    요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //    Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            System.out.println("access_token : " + access_Token);
            System.out.println("refresh_token : " + refresh_Token);

            br.close();
            bw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return access_Token;
    }
	
	@Override
    public String getKakaoUserInfo (String access_Token) throws Exception {

        //    요청하는 클라이언트마다 가진 정보가 다를 수 있기에 HashMap타입으로 선언
        HashMap<String, Object> kakaoInfo = new HashMap<String, Object>();
        String reqURL = "https://kapi.kakao.com/v2/user/me";
        String kakaoId = null;
        
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            conn.setRequestProperty("Authorization", "Bearer " + access_Token);

            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
            JsonObject kakao_account = element.getAsJsonObject().get("kakao_account").getAsJsonObject();

            kakaoId = element.getAsJsonObject().get("id").getAsString();
            System.out.println("kakaoId : " + kakaoId);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return kakaoId; // 예외 발생 시 null 반환
    }
	
	public String PhoneNumberCheck(String to) throws Exception {
		String smsProvider = "https://api.coolsms.co.kr";
		DefaultMessageService messageService = NurigoApp.INSTANCE.initialize(coolsmsApiKey, coolsmsSecret, smsProvider);

		Random rand = new Random();
		String numStr = "";
		for(int i=0; i<4; i++) {
			String ran = Integer.toString(rand.nextInt(10));
			numStr += ran;
		}

		Message message = new Message();
		message.setFrom(phoneNum);    	// 발신전화번호. 테스트시에는 발신,수신 둘다 본인 번호로 하면 됨
		message.setTo(to);    				// 수신전화번호 (ajax로 view 화면에서 받아온 값으로 넘김)
		message.setText("인증번호는 ?? [" + numStr + "] 입니다.");

		SingleMessageSendingRequest request = new SingleMessageSendingRequest(message);
		SingleMessageSentResponse response = messageService.sendOne(request); // 메시지 전송

		return numStr;
	}
	
	//==========================화상통화==================================================
	public User findByUserId(String userId) throws Exception{
        return userDaoJM.findByUserId(userId);
    }

}
