package com.mapmory.services.purchase.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mapmory.common.domain.Search;
import com.mapmory.services.purchase.domain.Purchase;
import com.mapmory.services.purchase.dto.PurchaseDTO;

@Mapper
public interface PurchaseDao {
	
	//insert
	public int addPurchase(Purchase purchase) throws Exception;
	
	//selectOne
	public PurchaseDTO getDetailPurchase(int purchaseNo) throws Exception;
	
	//selectList
	public List<PurchaseDTO> getPurchaseList(Search search) throws Exception;
	
	//count
	public int getPurchaseTotalCount(Search search) throws Exception;
}
