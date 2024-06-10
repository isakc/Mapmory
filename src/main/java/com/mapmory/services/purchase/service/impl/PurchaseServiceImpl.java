package com.mapmory.services.purchase.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	    
	    if(purchase.getPaymentMethod() != 1) {
	    	purchase.setCardType(null);
	    	purchase.setLastFourDigits(null);
	    }

		return purchaseDao.addPurchase(purchase) == 1 ? true : false;
		
	}// addPurchase

	@Override
	public PurchaseDTO getDetailPurchase(int purchaseNo) throws Exception {
		
		return purchaseDao.getDetailPurchase(purchaseNo);
		
	}// getDetailPurchase
	
	@Override
	public List<PurchaseDTO> getPurchaseList(Search search) throws Exception {
		List<PurchaseDTO> purchaseList = purchaseDao.getPurchaseList(search);
		
		 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

		for(PurchaseDTO purchase : purchaseList) {
			purchase.setPurchaseDateString( purchase.getPurchaseDate().format(formatter) );
		}
		
		return purchaseList;
		
	}// getPurchaseList

	@Override
	public int getPurchaseTotalCount(Search search) throws Exception {
		
		return purchaseDao.getPurchaseTotalCount(search);
		
	}// getPurchaseTotalCount

	@Override
	public boolean validatePurchase(String impUid, Purchase purchase) throws IamportResponseException, IOException {
		IamportResponse<Payment> validation = iamportClient.paymentByImpUid(impUid);
		
		return validation.getResponse().getAmount().compareTo( new BigDecimal(purchase.getPrice())) == 0 ? true : false;
		
	}// validatePurchase: 위변조 검증 메소드
}