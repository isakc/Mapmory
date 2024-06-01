package com.mapmory.services.purchase.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mapmory.services.purchase.domain.Purchase;

@Mapper
public interface PurchaseDao {
	
	//insert
	public void addPurchase(Purchase purchase) throws Exception;
	
	//selectList
	public Purchase getPurchase(int purchaseNo) throws Exception;
	
	//selectList
	public List<Purchase> getPurchaseList(String userId) throws Exception;
	
	//count
	public int getPurchaseTotalCount(String userId) throws Exception;
}
