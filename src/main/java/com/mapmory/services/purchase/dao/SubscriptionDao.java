package com.mapmory.services.purchase.dao;

import org.apache.ibatis.annotations.Mapper;

import com.mapmory.services.purchase.domain.Subscription;

@Mapper
public interface SubscriptionDao {
	
	//insert
	public void addSubscription(Subscription Subscription) throws Exception;
	
	//selectOne
	public Subscription getDetailSubscription(String userId) throws Exception;
	
	//update
	public void updatePaymentMethod(Subscription subscription) throws Exception;
	
	//delete
	public void deleteSubscription(String userId) throws Exception;
}