package com.mapmory.common.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.vane.badwordfiltering.BadWordFiltering;

@Component("contentFilterUtil")
public class ContentFilterUtil {

	@Value("${greenEye.GREEN.EYE.URI}")
	private String apiUrl;
	
	@Value("${greenEye.SECRET.KEY}")
	private String key;
	
	//기록 내용, 댓글, 닉네임 등 텍스트에 대한 비속어 필터링 메서드
	public static boolean checkBadWord(String text) {
		BadWordFiltering badWordFiltering = new BadWordFiltering();
		
		String replaceText = badWordFiltering.change(text.replaceAll(" |\\* ", ""), 
					new String [] {"!", "@", "#", "$", "%", "^", "&", "_", "-", "0", "1", "2", "3", "4" ,"5", "6", "7", "8", "9"});
		
		if(replaceText.contains("*") == true) {
			System.out.println("비속어 등록 불가");
			return true;
		} 
		return false;
	}	
	
	public boolean checkBadImage(MultipartFile file) {

		try {
			
			//base64로 인코딩
			byte [] imageBytes = file.getBytes();
			String encodedImage = Base64.getEncoder().encodeToString(imageBytes);
			
			//JSON 객체 생성
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("version", "V1");
			jsonObject.put("requestId", "checkBadImageMapMory");
			jsonObject.put("timestamp", System.currentTimeMillis());

			//이미지 객체 생성
			JSONObject imagesObject = new JSONObject();
			imagesObject.put("name", "checkImage");
			imagesObject.put("data", encodedImage);
			
			//이미지 배열 생성
			JSONArray imagesArray = new JSONArray();
			imagesArray.put(imagesObject);
			
			//JSON 객체에 이미지 배열 추기
			jsonObject.put("images", imagesArray);
			String jsonInputString = jsonObject.toString();			
			
			//URL 객체 생성
			URL url = new URL(apiUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			
			//요청 메소드 설정(POST)
			connection.setRequestMethod("POST");
			
			//요청 헤더 설정
			connection.setRequestProperty("X-GREEN-EYE-SECRET", key);
			connection.setRequestProperty("Content-Type", "application/json");
			
			//출력 스트림 사용 가능 설정
			connection.setDoOutput(true);
						
			//JSON 입력 문자열을 출력 스트팀으로 변환
			try (OutputStream os = connection.getOutputStream()) {
				byte [] input = jsonInputString.getBytes("UTF-8");
				os.write(input, 0 , input.length);
			}
			
			//응답 코드 받기
			int responseCode = connection.getResponseCode();
			System.out.println("Response Code : "+responseCode);
			
			//응답 읽기
			StringBuilder response = new StringBuilder();
			try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
				String responseLine;
				while ((responseLine = br.readLine()) != null) {
					response.append(responseLine.trim());
				}
				
				//응답 출력
				System.out.println(response.toString());
			}
			
			return parseJsonResponse(response.toString());

		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
				
	}

	private boolean parseJsonResponse(String jsonResponse) throws JSONException {

		//JSON 객체 생성
		JSONObject jsonObject = new JSONObject(jsonResponse);
		
		//JSON 배열 images 받기
		JSONArray imagesArray = jsonObject.getJSONArray("images");
		
		//이미지 객체 가져오기
		JSONObject imageObject = imagesArray.getJSONObject(0);
		
		//이미지 객체에서 결과값 가져오기
		JSONObject resultObject = imageObject.getJSONObject("result");
		
		//결과값의 각 필드값 가져오기
		double adultConfidence = resultObject.getJSONObject("adult").getDouble("confidence");
		double normalConfidence = resultObject.getJSONObject("normal").getDouble("confidence");
		double pornConfidence = resultObject.getJSONObject("porn").getDouble("confidence");
		double sexyConfidence = resultObject.getJSONObject("sexy").getDouble("confidence");		
		
		//각 필드값 출력
		System.out.println("Adult Confidence : "+adultConfidence);
		System.out.println("Normal Confidence : "+normalConfidence);
		System.out.println("Porn Confidence : "+pornConfidence);
		System.out.println("Sexy Confidence : "+sexyConfidence);
		
		if(adultConfidence >= 0.2 || pornConfidence >= 0.2) {
			return true;
		} else {
			return false;
		}
			
	}
}
