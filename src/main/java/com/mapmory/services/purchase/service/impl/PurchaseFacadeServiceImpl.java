package com.mapmory.services.purchase.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mapmory.exception.purchase.SubscriptionException;
import com.mapmory.services.product.domain.Product;
import com.mapmory.services.purchase.domain.Purchase;
import com.mapmory.services.purchase.domain.Subscription;
import com.mapmory.services.purchase.service.PurchaseFacadeService;
import com.mapmory.services.purchase.service.PurchaseService;
import com.mapmory.services.purchase.service.SubscriptionService;

@Service("purchaseFacadeServiceImpl")
@Transactional
public class PurchaseFacadeServiceImpl implements PurchaseFacadeService {

	@Autowired
	@Qualifier("purchaseServiceImpl")
	private PurchaseService purchaseService;
	
	@Autowired
	@Qualifier("subscriptionServiceImpl")
	private SubscriptionService subscriptionService;
	
	@Override
	public boolean addSubscription(Purchase purchase, Subscription subscription) throws Exception {
		if(purchaseService.addPurchase(purchase) && subscriptionService.addSubscription(subscription)) {
			
			return true;
		}else {
			
			throw new SubscriptionException();
		}
	}// addSubscription

	@Override
	public boolean updatePaymentMethod(Subscription subscription, Product product) throws Exception {
		
		if(subscriptionService.updatePaymentMethod(subscription)) {
			if( subscriptionService.cancelSubscriptionPortOne(subscription.getUserId()) ) {
				Subscription currentSubscription = subscriptionService.getDetailSubscription(subscription.getUserId());
				
				subscription.setNextSubscriptionPaymentDate(currentSubscription.getNextSubscriptionPaymentDate());
				subscription.setMerchantUid("subscription_" + subscription.getUserId() + "_" + LocalDateTime.now());
				
				return subscriptionService.schedulePay(subscription, product);
			}else {
				throw new SubscriptionException();
			}
		}else {
			throw new SubscriptionException();
		}
	}// updatePaymentMethod

	@Override
	public boolean cancelSubscription(String userId) throws Exception{
		
		if(subscriptionService.cancelSubscription(userId)) {
			return subscriptionService.cancelSubscriptionPortOne(userId);
		}
		
		return false;
	}// cancelSubscription: 결제 취소
}
