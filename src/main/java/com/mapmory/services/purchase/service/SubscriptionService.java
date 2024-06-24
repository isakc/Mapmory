package com.mapmory.services.purchase.service;

import java.util.List;

import com.mapmory.common.domain.Search;
import com.mapmory.services.product.domain.Product;
import com.mapmory.services.purchase.domain.Subscription;

public interface SubscriptionService {
	
	//insert
	public boolean addSubscription(Subscription Subscription) throws Exception;
	
	//selectOne
	public Subscription getDetailSubscription(String userId) throws Exception;
	
	//selectList
	public List<Subscription> getSubscriptionList(Search search) throws Exception;
	
	//update
	public boolean updatePaymentMethod(Subscription subscription) throws Exception;
	
	public boolean reSubscription(Subscription subscription) throws Exception;
	
	//delete
	public boolean cancelSubscription(String userId) throws Exception;
	
	public boolean cancelSubscriptionPortOne(String userId) throws Exception;
	
	///// 추가 메소드 /////
	
	public boolean requestSubscription(Subscription subscription, Product product) throws Exception;
	
	public boolean schedulePay(Subscription subscription, Product product) throws Exception;

	public List<Subscription> getTodaySubscriptionList() throws Exception;
	
	public boolean deleteSubscription(int subscriptionNo) throws Exception;
}
