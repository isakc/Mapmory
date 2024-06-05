package com.mapmory.services.purchase.service;

import java.util.List;

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
	
	///// 추가 메소드 /////
	
	//other
	public String getPortOneToken() throws Exception;
	
	public boolean requestSubscription(Subscription subscription) throws Exception;
	
	public Subscription schedulePay(Subscription subscription) throws Exception;

	public List<Subscription> getTodaySubscriptionList() throws Exception;
	
	//count
	public int countSubscription(String userId) throws Exception;
}
