package com.mapmory.services.purchase.service.impl;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mapmory.services.purchase.dao.SubscriptionDao;
import com.mapmory.services.purchase.domain.IamportToken;
import com.mapmory.services.purchase.domain.Purchase;
import com.mapmory.services.purchase.domain.Subscription;
import com.mapmory.services.purchase.service.PurchaseService;
import com.mapmory.services.purchase.service.SubscriptionScheduler;
import com.mapmory.services.purchase.service.SubscriptionService;

@Service("subscriptionServiceImpl")
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

	///// Field /////
	
	@Autowired
	private SubscriptionDao subscriptionDao;
	
	@Autowired
	@Qualifier("purchaseServiceImpl")
	private PurchaseService purchaseService;
	
	@Value("${portOne.imp_key}")
	private String impKey;

	@Value("${portOne.imp_secret}")
	private String portOneImpSecret;

	///// Method /////
	
	@Override
	public void addSubscription(Subscription subscription) throws Exception {
		Purchase purchase = Purchase.builder()
							.price(1000)
							.paymentMethod(subscription.getNextPaymentMethod())
							.cardType(subscription.getNextSubscriptionCardType())
							.lastFourDigits(subscription.getNextSubscriptionLastFourDigits())
							.userId(subscription.getUserId())
							.purchaseDate(subscription.getSubscriptionStartDate())
							.productNo(2)
							.build();
		
		purchaseService.addPurchase(purchase);
		subscriptionDao.addSubscription(subscription); // DB에 구독 정보 저장
	}//addSubscription

	@Override
	public Subscription getDetailSubscription(String userId) throws Exception {
		Subscription subscription = subscriptionDao.getDetailSubscription(userId);
		
		return subscription;
	}//getDetailSubscription

	@Override
	public void updatePaymentMethod(Subscription subscription) throws Exception {
		
		deleteSubscription(subscription.getUserId());
		
		subscriptionDao.updatePaymentMethod(subscription);
		
	}//updatePaymentMethod

	@Override
	public void deleteSubscription(String userId) throws Exception {
		String accessToken = getPortOneToken();
		
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(accessToken);
		
		Subscription subscription = getDetailSubscription(userId);
		Map<String, Object> map = new HashMap<>();
		map.put("customer_uid", subscription.getCustomerUid());
		map.put("merchant_uid", subscription.getMerchantUid());

		String requestJson = new Gson().toJson(map);
		
		HttpEntity<String> requestEntity = new HttpEntity<>(requestJson, headers);
		
		ResponseEntity<String> unscheduleResponseEntity = restTemplate.exchange(
			    "https://api.iamport.kr/subscribe/payments/unschedule",
			    HttpMethod.POST,
			    requestEntity,
			    String.class
			);
		
//		ResponseEntity<String> deleteResponseEntity = restTemplate.exchange(
//			    "https://api.iamport.kr/subscribe/customers/{customer_uid}",
//			    HttpMethod.DELETE,
//			    requestEntity,
//			    String.class,
//			    subscription.getCustomerUid()
//			);
		
		subscriptionDao.deleteSubscription(userId);
	}//deleteSubscription
	
	
	@Override
	public boolean requestSubscription(Subscription subscription) throws Exception {

		String accessToken = getPortOneToken();
		
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(accessToken);
		
		Map<String, Object> map = new HashMap<>();
		map.put("customer_uid", subscription.getCustomerUid());
		map.put("merchant_uid", "subscription_"+subscription.getUserId()+"_"+LocalDateTime.now());
		map.put("amount", "1000");
		map.put("name", "정기 구독 결제");

		String requestJson = new Gson().toJson(map);
		HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
		
		String resultJson = restTemplate.postForObject("https://api.iamport.kr/subscribe/payments/again", entity, String.class);
		System.out.println("requestSub: " + resultJson);
		
		ObjectMapper objectMapper = new ObjectMapper();
	    JsonNode rootNode = objectMapper.readTree(resultJson);
		String status = rootNode.get("response").get("status").asText();
		
		if(status.equals("paid")) {
			return true;
		}
		
		return false;
	}//구독 결제 요청
	
	public Subscription schedulePay(Subscription subscription) throws Exception {

//		LocalDateTime currentDateTime = LocalDateTime.now();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        String formattedDateTime = currentDateTime.format(formatter);
//		Calendar cal = Calendar.getInstance();
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
//		
//		//cal.add(Calendar.DATE, +31);
//		cal.add(Calendar.MINUTE, +1);
//		String date = sdf.format(cal.getTime());
//		
//		try {
//			Date stp = new Date();
//			stp = sdf.parse(date);
//			
//			timestamp = stp.getTime()/1000;
//		} catch (ParseException e) {
//			e.printStackTrace();
//		} 
		
		long timestamp = 0;
		LocalDateTime localDateTime = subscription.getNextSubscriptionPaymentDate();
		Instant instant = localDateTime.toInstant(ZoneOffset.UTC);
		timestamp = instant.toEpochMilli();
		 
		 String accessToken = getPortOneToken();
		 
		 RestTemplate restTemplate = new RestTemplate();
		 HttpHeaders headers = new HttpHeaders();
		 headers.setContentType(MediaType.APPLICATION_JSON);
		 headers.setBearerAuth(accessToken);//헤더에 access token 추가
		 
		 JsonObject scheduleJson = new JsonObject();
		 scheduleJson.addProperty("merchant_uid", "subscription_"+subscription.getUserId()+"_"+LocalDateTime.now() );
		 scheduleJson.addProperty("schedule_at", timestamp);
		 scheduleJson.addProperty("amount", 1000);
		 scheduleJson.addProperty("name", "정기 결제 구독");
		 
		 JsonArray jsonArr = new JsonArray();
		 jsonArr.add(scheduleJson);
		 
		 JsonObject requestSubscriptionJson = new JsonObject();
		 requestSubscriptionJson.addProperty("customer_uid", subscription.getCustomerUid()); 
		 requestSubscriptionJson.add("schedules", jsonArr);

		 String json = new Gson().toJson(requestSubscriptionJson);
		 HttpEntity<String> entity = new HttpEntity<>(json, headers);
		 
		 ObjectMapper objectMapper = new ObjectMapper();
	     JsonNode rootNode = objectMapper.readTree(restTemplate.postForObject("https://api.iamport.kr/subscribe/payments/schedule", entity, String.class));
	     System.out.println(rootNode);
	     
	     // response 배열에서 merchant_uid와 schedule_at 추출
//	     String merchantUid = rootNode.get("response").get(0).get("merchant_uid").asText();
//	     long scheduleAtUnixTime = rootNode.get("response").get(0).get("schedule_at").asLong()+3;
	     
	     // Unix 시간을 LocalDateTime으로 변환
//	     LocalDateTime nextSubscriptionPaymentDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(scheduleAtUnixTime), ZoneId.systemDefault());
//		 
//	     subscription.setMerchantUid(merchantUid);
//	     subscription.setNextSubscriptionPaymentDate(nextSubscriptionPaymentDate);
//	     subscription.setSubscriptionStartDate(nextSubscriptionPaymentDate.plusMinutes(-1));
//	     subscription.setSubscriptionEndDate(nextSubscriptionPaymentDate);
		 
	     addSubscription(subscription);
	     
		 return subscription;
	}//schedulePay: 정기결제 예약 등록하는 곳

	@Override
	public String getPortOneToken() throws Exception {
		RestTemplate restTemplate = new RestTemplate();
		
		//서버로 요청할 Header
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
		
	    Map<String, Object> map = new HashMap<>();
	    map.put("imp_key", impKey);
	    map.put("imp_secret", portOneImpSecret);
	   
	    Gson var = new Gson();
	    String json=var.toJson(map);
		//서버로 요청할 Body
	   
	    HttpEntity<String> entity = new HttpEntity<>(json, headers);
	    
	    String getAccessToken = restTemplate.postForObject("https://api.iamport.kr/users/getToken", entity, String.class);
		Gson str = new Gson();
		getAccessToken = getAccessToken.substring(getAccessToken.indexOf("response") + 10);
		getAccessToken = getAccessToken.substring(0, getAccessToken.length() - 1);

		IamportToken iamportToken = str.fromJson(getAccessToken, IamportToken.class);
		String accessToken = iamportToken.getAccess_token();
		
		return accessToken;
	}//getPortOneToken

	@Override
	public int countSubscription(String userId) throws Exception {
		return subscriptionDao.countSubscription(userId);
	}

	@Override
	public List<Subscription> getTodaySubscriptionList() throws Exception {
		return subscriptionDao.getTodaySubscriptionList();
	}
	
}