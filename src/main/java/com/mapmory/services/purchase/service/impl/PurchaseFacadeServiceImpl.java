package com.mapmory.services.purchase.service.impl;

import java.time.LocalDateTime;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mapmory.common.util.PurchaseUtil;
import com.mapmory.exception.purchase.SubscriptionException;
import com.mapmory.services.product.domain.Product;
import com.mapmory.services.purchase.domain.Purchase;
import com.mapmory.services.purchase.domain.Subscription;
import com.mapmory.services.purchase.service.PurchaseFacadeService;
import com.mapmory.services.purchase.service.PurchaseService;
import com.mapmory.services.purchase.service.SubscriptionService;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

@Service("purchaseFacadeServiceImpl")
@Transactional
public class PurchaseFacadeServiceImpl implements PurchaseFacadeService {

	///// Field /////
	
	private IamportClient iamportClient;

	@Value("${portOne.imp_key}")
	private String portOneImpKey;

	@Value("${portOne.imp_secret}")
	private String portOneImpSecret;
	
	@Autowired
	@Qualifier("purchaseServiceImpl")
	private PurchaseService purchaseService;
	
	@Autowired
	@Qualifier("subscriptionServiceImpl")
	private SubscriptionService subscriptionService;
	
	///// Constructor /////
	@PostConstruct
	private void init() {
		this.iamportClient = new IamportClient(portOneImpKey, portOneImpSecret, true);
	}
	
	///// Method /////
	
	@Override
	public boolean addSubscription(Purchase purchase) throws Exception {
		if(purchaseService.addPurchase(purchase) && subscriptionService.addSubscription(purchase)) {
			
			return true;
		}else {
			
			throw new SubscriptionException();
		}
	}// addSubscription: purchase와 subscription을 동시에 db에 저장하는 메소드

	@Override
	public boolean updatePaymentMethod(Purchase purchase, Product product) throws Exception {
		
		IamportResponse<Payment> returnPayment = iamportClient.paymentByImpUid(purchase.getImpUid());
		int paymentMethod = PurchaseUtil.paymentChangeToInt(returnPayment.getResponse().getPgProvider());
		
		Subscription updateSubscription = Subscription.builder()
										.nextSubscriptionPaymentMethod(paymentMethod)
										.customerUid(purchase.getUserId())
										.userId(purchase.getUserId())
										.build();
		if(paymentMethod == 1) {
			updateSubscription.setNextSubscriptionCardType(returnPayment.getResponse().getCardName());
			updateSubscription.setNextSubscriptionLastFourDigits(returnPayment.getResponse().getCardNumber().substring(0, 4));
		}
		
		subscriptionService.updatePaymentMethod(updateSubscription);
		subscriptionService.cancelSubscriptionPortOne(purchase.getUserId());
		
		Subscription currentSubscription = subscriptionService.getDetailSubscription(purchase.getUserId());
				
		updateSubscription.setNextSubscriptionPaymentDate(currentSubscription.getNextSubscriptionPaymentDate());
		updateSubscription.setMerchantUid("subscription_" + purchase.getUserId() + "_" + LocalDateTime.now());
				
		return subscriptionService.schedulePay(updateSubscription, product);
	}// updatePaymentMethod: 결제 수단 변경

	@Override
	public boolean cancelSubscription(String userId) throws Exception{
		
		if(subscriptionService.cancelSubscription(userId)) {
			return subscriptionService.cancelSubscriptionPortOne(userId);
		}
		
		return false;
	}// cancelSubscription: 결제 취소
}
