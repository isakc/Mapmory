package com.mapmory.services.purchase.service.impl;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.mapmory.common.domain.Search;
import com.mapmory.services.purchase.dao.PurchaseDao;
import com.mapmory.services.purchase.domain.Purchase;
import com.mapmory.services.purchase.dto.PurchaseDTO;
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
	public boolean addPurchase(Purchase purchase) throws Exception {
		
		 // 객체 자체가 null인 경우 예외 발생
	    if (purchase == null) {
	        throw new IllegalArgumentException("Purchase != null");
	    }

	    // 가격이 음수인 경우 예외 발생
	    if (purchase.getPrice() < 0) {
	        throw new DataIntegrityViolationException("Price < 0");
	    }
	    
		return purchaseDao.addPurchase(purchase) == 1 ? true : false;
		
	}// addPurchase

	@Override
	public PurchaseDTO getDetailPurchase(int purchaseNo) throws Exception {
		
		return purchaseDao.getDetailPurchase(purchaseNo);
		
	}// getDetailPurchase
	
	@Override
	public List<PurchaseDTO> getPurchaseList(Search search) throws Exception {
		
		return purchaseDao.getPurchaseList(search);
		
	}// getPurchaseList

	@Override
	public int getPurchaseTotalCount(Search search) throws Exception {
		
		return purchaseDao.getPurchaseTotalCount(search);
		
	}// getPurchaseTotalCount

	@Override
	public IamportResponse<Payment> validatePurchase(String impUid) throws IamportResponseException, IOException {
		
		return iamportClient.paymentByImpUid(impUid);
		
	}// validatePurchase
}