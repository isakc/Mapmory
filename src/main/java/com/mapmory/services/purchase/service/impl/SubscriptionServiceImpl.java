package com.mapmory.services.purchase.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
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
							.paymentMethod(subscription.getNextSubscriptionPaymentMethod())
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
	public boolean updatePaymentMethod(Subscription subscription) throws Exception {
		
		Subscription currentSubscription = getDetailSubscription(subscription.getUserId());
		
		subscription.setSubscriptionStartDate(currentSubscription.getSubscriptionStartDate());
		subscription.setSubscriptionEndDate(currentSubscription.getSubscriptionEndDate());
		subscription.setNextSubscriptionPaymentDate(currentSubscription.getNextSubscriptionPaymentDate());
		
		Map<String, Object> map = new HashMap<>();
		map.put("customer_uid", subscription.getCustomerUid());
		
		String resultJson = getPortOneResultJson("https://api.iamport.kr/subscribe/payments/unschedule", map, getPortOneToken());
		
		if(getPortOneResultFlag(resultJson)) {
	        subscription.setMerchantUid("subscription_" + subscription.getUserId() + "_" + LocalDateTime.now());
	        
			if(schedulePay(subscription)) {
				return subscriptionDao.updatePaymentMethod(subscription) == 1 ? true : false;
			}
		}
		
		return false;
	}// updatePaymentMethod

	@Override
	public boolean deleteSubscription(String userId) throws Exception {
		
		Subscription subscription = getDetailSubscription(userId);
		Map<String, Object> map = new HashMap<>();
		map.put("customer_uid", subscription.getCustomerUid());
		
		String resultJson = getPortOneResultJson("https://api.iamport.kr/subscribe/payments/unschedule", map, getPortOneToken());
		
		return getPortOneResultFlag(resultJson);
	}// deleteSubscription
	
	@Override
	public boolean requestSubscription(Subscription subscription) throws Exception {
		
		Map<String, Object> map = new HashMap<>();
		map.put("customer_uid", subscription.getCustomerUid());
		map.put("merchant_uid", subscription.getMerchantUid());
		map.put("amount", "1000");
		map.put("name", "정기 구독 결제");

		String resultJson = getPortOneResultJson("https://api.iamport.kr/subscribe/payments/again", map, getPortOneToken());
		
		return getPortOneResultFlag(resultJson);
	}// requestSubscription: 첫 구독 시 결제 요청
	
	public boolean schedulePay(Subscription subscription) throws Exception {
		
		long timestamp = 0;
		LocalDateTime localDateTime = subscription.getNextSubscriptionPaymentDate();
		Instant instant = localDateTime.atZone(ZoneId.of("Asia/Seoul")).toInstant(); //한국시간으로 변경
		timestamp = instant.getEpochSecond();
		
		Map<String, Object> scheduleMap = new HashMap<>();
		scheduleMap.put("merchant_uid", subscription.getMerchantUid());
		scheduleMap.put("schedule_at", timestamp);
		scheduleMap.put("amount", 1000);
		scheduleMap.put("name", "정기 결제 구독");
		
        List< Map<String, Object> > schedules = new ArrayList<>(); // schedules 리스트 생성
        schedules.add(scheduleMap);

		Map<String, Object> requestSubscriptionMap = new HashMap<>();
		requestSubscriptionMap.put("customer_uid", subscription.getCustomerUid());
		requestSubscriptionMap.put("schedules", schedules);

		 String resultJson = getPortOneResultJson("https://api.iamport.kr/subscribe/payments/schedule", requestSubscriptionMap, getPortOneToken());
	     
		 return getPortOneResultFlag(resultJson);
	}// schedulePay: 정기결제 예약 등록하는 곳

	@Override
	public String getPortOneToken() throws Exception {
	    Map<String, Object> map = new HashMap<>();
	    map.put("imp_key", impKey);
	    map.put("imp_secret", portOneImpSecret);
	    
	    String getAccessToken = getPortOneResultJson("https://api.iamport.kr/users/getToken", map, null);
	    
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
	
	public String getPortOneResultJson(String requestUrl, Map<String, Object> map, String accessToken) throws Exception {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders(); //서버로 요청할 Header
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    if(accessToken != null) {
			headers.setBearerAuth(accessToken);
	    }
		
	    String requestJson = new Gson().toJson(map);
		HttpEntity<String> requestEntity = new HttpEntity<>(requestJson, headers);
		String resultJson = restTemplate.postForObject(requestUrl, requestEntity, String.class);
		
		return resultJson;
	}// getPortoneResult: PortOne으로 API 요청
	
	public boolean getPortOneResultFlag(String resultJson) throws JsonMappingException, JsonProcessingException {
		
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readTree(resultJson);
		String status = rootNode.get("code").asText();
		
		return status.equals("0") ? true : false;
	}// getPortOneResultFlag: API 통신 성공 유무
}