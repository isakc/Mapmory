package com.mapmory.services.purchase.service;

import com.mapmory.services.purchase.domain.Subscription;

public interface SubscriptionService {
	
	//insert
	public void addSubscription(Subscription Subscription) throws Exception;
	
	//selectOne
	public Subscription getDetailSubscription(String userId) throws Exception;
	
	//update
	public void updatePaymentMethod(Subscription subscription) throws Exception;
	
	//delete
	public void deleteSubscription(String userId) throws Exception;
	
	//other
	public String getPortOneToken() throws Exception;
	
	public boolean requestSubscription(Subscription subscription) throws Exception;
	
	public Subscription schedulePay(Subscription subscription) throws Exception;
}
