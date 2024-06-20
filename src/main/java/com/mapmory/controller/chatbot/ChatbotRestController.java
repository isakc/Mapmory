package com.mapmory.controller.chatbot;


import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/bot/*")
public class ChatbotRestController {
		//ㅎㅎ
		@Value("${chatbot.api.url}")
		private String apiUrl;
		@Value("${chatbot.api.secret.key}")
		private String secretKey;
		
		    private static final Logger logger = LoggerFactory.getLogger(ChatbotRestController.class);
		    private static Properties config;

		    public ChatbotRestController() {
		        // Load API configuration properties
		        try {
		            String filePath = Objects.requireNonNull(getClass().getClassLoader().getResource("application.properties")).getFile();
		            FileInputStream fis = new FileInputStream(filePath);
		            config = new Properties();
		            config.load(fis);
		            fis.close();
		        } catch (IOException e) {
		            logger.error("Failed to load API configuration properties", e);
		        }
		    }

		    // NAVER Cloud ChatBot
		    @RequestMapping("chat")
		    public ResponseEntity<String> chatBotconn(@RequestBody String text) {

		        // 최종 결과값 리턴시 사용할 변수 선언
		        JSONObject chatbotMessage = new JSONObject();

//		        String apiUrl = config.getProperty("api.bot.url");
//		        String secretKey = config.getProperty("api.bot.client.secret");

		        // 사용자 질문 텍스트 Request
		        String message = getReqMessage(text);

		        logger.info("입력 질문: " + message);

		        String encodeBase64String = makeSignature(message, secretKey);

		        try {

		            HttpURLConnection con = (HttpURLConnection) new URL(apiUrl).openConnection();

		            con.setDoOutput(true);
		            con.setRequestMethod("POST");
		            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		            con.setRequestProperty("X-NCP-CHATBOT_SIGNATURE", encodeBase64String);

		            OutputStream outputStream = con.getOutputStream();

		            outputStream.write(message.getBytes(StandardCharsets.UTF_8));
		            outputStream.flush();
		            outputStream.close();

		            BufferedReader br;
		            String decodedString;

		            int responseCode = con.getResponseCode();

		            if (responseCode == 200) {    // 정상 호출
		                br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
		            } else {                    // 오류 발생
		                logger.error("API request failed with response code: " + responseCode);
		                br = new BufferedReader(new InputStreamReader(con.getErrorStream(), StandardCharsets.UTF_8));

		                // 예외 처리 후 바로 반환
		                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("API request failed with response code: " + responseCode);
		            }

		            StringBuilder response = new StringBuilder();
		            while ((decodedString = br.readLine()) != null) {
		                response.append(decodedString);
		            }
		            br.close();

		            // 챗봇 텍스트 결과 출력
		            logger.info("챗봇 응답: " + response);

		            chatbotMessage = new JSONObject(response.toString());
		        } catch (Exception e) {
		            logger.error("Failed to perform chatbot request", e);

		            // 예외 처리 후 바로 반환
		            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to perform chatbot request");
		        }

		        return ResponseEntity.ok()
		                .contentType(MediaType.APPLICATION_JSON)
		                .body(chatbotMessage.toString());
		    }

		    public static String makeSignature(String message, String secretKey) {
		        try {
		            byte[] secretKeyBytes = secretKey.getBytes(StandardCharsets.UTF_8);

		            SecretKeySpec signingKey = new SecretKeySpec(secretKeyBytes, "HmacSHA256");
		            Mac mac = Mac.getInstance("HmacSHA256");
		            mac.init(signingKey);

		            byte[] rawHmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
		            return Base64.getEncoder().encodeToString(rawHmac);
		        } catch (Exception e) {
		            throw new RuntimeException("Failed to generate signature", e);
		        }
		    }

		    public static String getReqMessage(String text) {

		        /// 최종 결과값 리턴시 사용할 변수 선언
		        String requestBody = "";

		        // userId 랜덤 생성
		        String userId = UUID.randomUUID().toString();

		        try {

		            JSONObject obj = new JSONObject();

		            long timestamp = new Date().getTime();

		            System.out.println("::");
		            logger.info("대화 시간: " + timestamp);

		            obj.put("version", "v2");
		            obj.put("userId", userId);
		            obj.put("timestamp", timestamp);

		            JSONObject bubbles_obj = new JSONObject();

		            bubbles_obj.put("type", "text");

		            JSONObject data_obj = new JSONObject();
		            data_obj.put("description", text);

		            bubbles_obj.put("data", data_obj);

		            JSONArray bubbles_array = new JSONArray();
		            bubbles_array.put(bubbles_obj);

		            obj.put("bubbles", bubbles_array);

//		            if (Objects.equals(text, "동영상 보여줘")) {
//		                obj.put("event", "open");
//		            } else {
		                obj.put("event", "send");
//		            }
		            requestBody = obj.toString();
		        } catch (Exception e) {
		            logger.error("Failed to create the request message", e);
		        }
		        return requestBody;
		    }
		    
		    @RequestMapping(value = "welcome", produces = "application/json; charset=UTF-8")
		    public ResponseEntity<String> welcomeMessage() {
		        String welcomeMessage = "안녕하세요! 무엇을 도와드릴까요?";
		        JSONObject response = new JSONObject();
		        response.put("message", welcomeMessage);
		        return ResponseEntity.ok(response.toString());
		    }
		    // 페이지 내비게이션 서비스
		    @RequestMapping("navi")
		    public ResponseEntity<Map<String, String[]>> pageNavigation(@RequestBody(required = false) Map<String, String[]> data, HttpServletRequest request) throws Exception {
		        if (data != null && data.containsKey("url")) {
		            String[] urls = data.get("url");
		            if (urls != null && urls.length > 0) {
		                String[] responseData = new String[]{urls[0]};

		                return ResponseEntity.ok().body(Collections.singletonMap("url", responseData));
		            }
		        }
		        return ResponseEntity.badRequest().build();
		    }
		}
