package com.mapmory.services.purchase.service.impl;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mapmory.services.product.domain.Product;
import com.mapmory.services.product.service.ProductService;
import com.mapmory.services.purchase.dao.SubscriptionDao;
import com.mapmory.services.purchase.domain.Purchase;
import com.mapmory.services.purchase.domain.Subscription;
import com.mapmory.services.purchase.service.PurchaseService;
import com.mapmory.services.purchase.service.SubscriptionService;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.request.AgainPaymentData;
import com.siot.IamportRestClient.request.ScheduleData;
import com.siot.IamportRestClient.request.ScheduleEntry;
import com.siot.IamportRestClient.request.UnscheduleData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.siot.IamportRestClient.response.Schedule;

@Service("subscriptionServiceImpl")
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

	///// Field /////

	private IamportClient iamportClient;
	
	@Autowired
	private SubscriptionDao subscriptionDao;
	
	@Autowired
	@Qualifier("purchaseServiceImpl")
	private PurchaseService purchaseService;
	
	@Autowired
	@Qualifier("productServiceImpl")
	private ProductService productService;
	
	@Value("${portOne.imp_key}")
	private String portOneImpkey;

	@Value("${portOne.imp_secret}")
	private String portOneImpSecret;
	
	///// Constructor /////
	@PostConstruct
    private void init() {
        this.iamportClient = new IamportClient(portOneImpkey, portOneImpSecret);
    }

	///// Method /////
	
	@Override
	public boolean addSubscription(Subscription subscription) throws Exception {
		
		Product sub = productService.getSubscription();
		
		Purchase purchase = Purchase.builder()
							.price(sub.getPrice())
							.paymentMethod(subscription.getNextSubscriptionPaymentMethod())
							.cardType(subscription.getNextSubscriptionCardType())
							.lastFourDigits(subscription.getNextSubscriptionLastFourDigits())
							.userId(subscription.getUserId())
							.purchaseDate(subscription.getSubscriptionStartDate())
							.productNo(sub.getProductNo())
							.build();
		
		purchaseService.addPurchase(purchase);
		
	    if(subscription.getNextSubscriptionPaymentMethod() != 1) {
	    	purchase.setCardType(null);
	    	purchase.setLastFourDigits(null);
	    }
	    
		return subscriptionDao.addSubscription(subscription) == 1 ? true : false; // DB에 구독 정보 저장
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
		
		UnscheduleData unscheduleData = new UnscheduleData("user7"); 
		IamportResponse<List<Schedule>> reponse = iamportClient.unsubscribeSchedule(unscheduleData);
		
		if(reponse.getCode() == 0) {
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
		
		UnscheduleData unscheduleData = new UnscheduleData("user7"); 
		IamportResponse<List<Schedule>> scheduleResponse = iamportClient.unsubscribeSchedule(unscheduleData);
		
		if(scheduleResponse.getCode() == 0) {
			return subscriptionDao.deleteSubscription(userId) == 1 ? true : false;
		}else {
			return false;
		}
		
	}// deleteSubscription
	
	@Override
	public boolean requestSubscription(Subscription subscription) throws Exception {
		
		Product sub = productService.getSubscription();
		
		AgainPaymentData againPaymentData = new AgainPaymentData( subscription.getCustomerUid(), subscription.getMerchantUid(), BigDecimal.valueOf(sub.getPrice()) ); 
		againPaymentData.setName(sub.getProductTitle());
		IamportResponse<Payment> againPayment = iamportClient.againPayment(againPaymentData);
		
		return againPayment.getCode() == 0 ? true : false;
		
	}// requestSubscription: 첫 구독 시 결제 요청
	
	public boolean schedulePay(Subscription subscription) throws Exception {
		Product sub = productService.getSubscription();
		
		LocalDateTime localDateTime = LocalDateTime.now().plusMonths(1);
		Instant instant = localDateTime.atZone(ZoneId.of("Asia/Seoul")).toInstant(); //한국시간으로 변경
		
		ScheduleData scheduleData = new ScheduleData(subscription.getCustomerUid());
		ScheduleEntry scheduleEntry = new ScheduleEntry(subscription.getMerchantUid(), Date.from(instant), BigDecimal.valueOf(sub.getPrice()) );
		scheduleEntry.setName(sub.getProductTitle());
		scheduleData.addSchedule(scheduleEntry);
		
		IamportResponse<List<Schedule>> subscribeSchedule = iamportClient.subscribeSchedule(scheduleData);
		
		return subscribeSchedule.getCode() == 0 ? true : false; 
		
	}// schedulePay: 정기결제 예약 등록하는 곳

	@Override
	public List<Subscription> getTodaySubscriptionList() throws Exception {
		
		return subscriptionDao.getTodaySubscriptionList();
		
	}// getTodaySubscriptionList: 오늘 결제일 유저 리스트
	
}