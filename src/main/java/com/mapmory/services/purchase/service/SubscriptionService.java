package com.mapmory.services.purchase.service;

import java.util.List;

import com.mapmory.services.purchase.domain.Subscription;

public interface SubscriptionService {
	
	//insert
	public boolean addSubscription(Subscription Subscription) throws Exception;
	
	//selectOne
	public Subscription getDetailSubscription(String userId) throws Exception;
	
	//update
	public boolean updatePaymentMethod(Subscription subscription) throws Exception;
	
	//delete
	public boolean deleteSubscription(String userId) throws Exception;
	
	///// 추가 메소드 /////
	
	public boolean requestSubscription(Subscription subscription) throws Exception;
	
	public boolean schedulePay(Subscription subscription) throws Exception;

	public List<Subscription> getTodaySubscriptionList() throws Exception;
}
