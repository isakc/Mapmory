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
	
	//delete
	public void deleteSubscription(String userId) throws Exception;
	
	///// 추가 메소드 /////
	
	public List<Subscription> getTodaySubscriptionList() throws Exception;
	
	//count
	public int countSubscription(String userId) throws Exception;
}
