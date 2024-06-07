package com.mapmory.services.purchase.service.impl;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mapmory.services.purchase.dao.PurchaseDao;
import com.mapmory.services.purchase.domain.Purchase;
import com.mapmory.services.purchase.service.PurchaseService;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

@Service("purchaseServiceImpl")
@Transactional
public class PurchaseServiceImpl implements PurchaseService {
	
	///// Field /////
	
	@Autowired
	private PurchaseDao purchaseDao;

	private IamportClient iamportClient;
	
	@Value("${portOne.imp_key}")
	private String portOneImpKey;

	@Value("${portOne.imp_secret}")
	private String portOneImpSecret;

	///// Constructor //////
	
	@PostConstruct
    private void init() {
        this.iamportClient = new IamportClient(portOneImpKey, portOneImpSecret);
    }
	
	///// Method /////
	
	@Override
	public void addPurchase(Purchase purchase) throws Exception {
		
		purchaseDao.addPurchase(purchase);
		
	}// addPurchase

	@Override
	public Purchase getPurchase(int purchaseNo) throws Exception {
		
		return purchaseDao.getPurchase(purchaseNo);
		
	}// getPurchase

	@Override
	public List<Purchase> getPurchaseList(String userId) throws Exception {
		
		return purchaseDao.getPurchaseList(userId);
		
	}// getPurchaseList

	@Override
	public int getPurchaseTotalCount(String userId) throws Exception {
		
		return purchaseDao.getPurchaseTotalCount(userId);
		
	}// getPurchaseTotalCount

	@Override
	public IamportResponse<Payment> validatePurchase(String impUid) throws IamportResponseException, IOException {
		
		return iamportClient.paymentByImpUid(impUid);
		
	}// validatePurchase
}