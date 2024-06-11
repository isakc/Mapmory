package com.mapmory.services.purchase.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mapmory.services.purchase.domain.Subscription;

@Mapper
public interface SubscriptionDao {
	
	//insert
	public int addSubscription(Subscription Subscription) throws Exception;
	
	//selectOne
	public Subscription getDetailSubscription(String userId) throws Exception;
	
	//update
	public int updatePaymentMethod(Subscription subscription) throws Exception;
	
	public int cancelSubscription(String userId) throws Exception;
	
	///// 추가 메소드 /////

	//delete
	public int deleteSubscription(int subscriptionNo) throws Exception;
	
	public List<Subscription> getTodaySubscriptionList() throws Exception;
	
}
