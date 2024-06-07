package com.mapmory.services.purchase.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
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
	}// addSubscription: 구독 DB에 저장

	@Override
	public Subscription getDetailSubscription(String userId) throws Exception {
		Subscription subscription = subscriptionDao.getDetailSubscription(userId);
		
		return subscription;
	}// getDetailSubscription

	@Override
	public void updatePaymentMethod(Subscription subscription) throws Exception {
		
		Subscription currentSubscription = getDetailSubscription(subscription.getUserId());
		
		subscription.setSubscriptionStartDate(currentSubscription.getSubscriptionStartDate());
		subscription.setSubscriptionEndDate(currentSubscription.getSubscriptionEndDate());
		subscription.setNextSubscriptionPaymentDate(currentSubscription.getNextSubscriptionPaymentDate());
		
		String accessToken = getPortOneToken();
		
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(accessToken);
		
		Map<String, Object> map = new HashMap<>();
		map.put("customer_uid", subscription.getCustomerUid());
		
		String requestJson = new Gson().toJson(map);
		HttpEntity<String> requestEntity = new HttpEntity<>(requestJson, headers);
		String resultJson = restTemplate.postForObject("https://api.iamport.kr/subscribe/payments/unschedule", requestEntity, String.class);
		System.out.println("update resultJson: " + requestJson);
		
        subscription.setMerchantUid("subscription_" + subscription.getUserId() + "_" + LocalDateTime.now());
		schedulePay(subscription);
		
		subscriptionDao.updatePaymentMethod(subscription);
		
	}// updatePaymentMethod

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

		String requestJson = new Gson().toJson(map);
		HttpEntity<String> requestEntity = new HttpEntity<>(requestJson, headers);
		String resultJson = restTemplate.postForObject("https://api.iamport.kr/subscribe/payments/unschedule", requestEntity, String.class);
		System.out.println("delete resultJson: " + requestJson);
		
		subscriptionDao.deleteSubscription(userId);
	}// deleteSubscription
	
	@Override
	public boolean requestSubscription(Subscription subscription) throws Exception {

		String accessToken = getPortOneToken();
		
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(accessToken);
		
		Map<String, Object> map = new HashMap<>();
		map.put("customer_uid", subscription.getCustomerUid());
		map.put("merchant_uid", subscription.getMerchantUid());
		map.put("amount", "1000");
		map.put("name", "정기 구독 결제");

		//첫 구독 결제 요청
		String requestJson = new Gson().toJson(map);
		HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
		String resultJson = restTemplate.postForObject("https://api.iamport.kr/subscribe/payments/again", entity, String.class);
		
		System.out.println("request resultJson: " + resultJson);
		
		ObjectMapper objectMapper = new ObjectMapper();
	    JsonNode rootNode = objectMapper.readTree(resultJson);
		String status = rootNode.get("response").get("status").asText();
		
		if(status.equals("paid")) {
			return true;
		}
		
		return false;
	}// requestSubscription: 첫 구독 시 결제 요청
	
	public boolean schedulePay(Subscription subscription) throws Exception {
		
		long timestamp = 0;
		LocalDateTime localDateTime = subscription.getNextSubscriptionPaymentDate();
		Instant instant = localDateTime.atZone(ZoneId.of("Asia/Seoul")).toInstant(); //한국시간으로 변경
		timestamp = instant.getEpochSecond();
		 
		 String accessToken = getPortOneToken();
		 
		 RestTemplate restTemplate = new RestTemplate();
		 HttpHeaders headers = new HttpHeaders();
		 headers.setContentType(MediaType.APPLICATION_JSON);
		 headers.setBearerAuth(accessToken);//헤더에 access token 추가
		 
		 JsonObject scheduleJson = new JsonObject();
		 scheduleJson.addProperty("merchant_uid", subscription.getMerchantUid() );
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
	     String status = rootNode.get("code").asText();
	     
	     System.out.println("schedulepay: " + rootNode);
	     
	     if(status.equals("0")) {
	    	 return true;
	     }
	     
		 return false;
	}// schedulePay: 정기결제 예약 등록하는 곳

	@Override
	public String getPortOneToken() throws Exception {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders(); //서버로 요청할 Header
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
	}// getPortOneToken: PortOne 토큰 가져오기

	@Override
	public int countSubscription(String userId) throws Exception {
		return subscriptionDao.countSubscription(userId);
	}// countSubscription: 구독 개수 가져오기

	@Override
	public List<Subscription> getTodaySubscriptionList() throws Exception {
		return subscriptionDao.getTodaySubscriptionList();
	}// getTodaySubscriptionList: 오늘 결제일 유저 리스트
	
}