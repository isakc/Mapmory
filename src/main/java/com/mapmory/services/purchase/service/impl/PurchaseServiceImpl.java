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
import com.mapmory.common.util.PurchaseUtil;
import com.mapmory.exception.purchase.PaymentValidationException;
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
	public boolean addPurchase(Purchase purchase) throws Exception{
		if (purchase == null) {
			throw new IllegalArgumentException();
		}

		if (purchase.getPrice() < 0) {
			throw new DataIntegrityViolationException("가격이 음수입니다");
		}

		if (purchase.getPaymentMethod() != 1) {
			purchase.setCardType(null);
			purchase.setLastFourDigits(null);
		}

		return purchaseDao.addPurchase(purchase) == 1;
	}// addPurchase: 결제 추가

	@Override
	public PurchaseDTO getDetailPurchase(int purchaseNo) throws Exception{
		
		return purchaseDao.getDetailPurchase(purchaseNo);
	}// getDetailPurchase: 결제 상세 정보

	@Override
	public List<PurchaseDTO> getPurchaseList(Search search) throws Exception {
		List<PurchaseDTO> purchaseList = purchaseDao.getPurchaseList(search);

		for (PurchaseDTO purchase : purchaseList) {
			purchase.setPaymentMethodString(PurchaseUtil.paymentChange(purchase.getPaymentMethod()));
			purchase.setPurchaseDateString(PurchaseUtil.purchaseDateChange(purchase.getPurchaseDate()));
		}

		return purchaseList;
	}// getPurchaseList: 구매 목록 가져오기

	@Override
	public int getPurchaseTotalCount(Search search) throws Exception {

		return purchaseDao.getPurchaseTotalCount(search);
	}// getPurchaseTotalCount: 구매 총 개수

	@Override
	public boolean validatePurchase(String impUid, Purchase purchase) {
		try {
			IamportResponse<Payment> validation = iamportClient.paymentByImpUid(impUid);
			
			return validation.getResponse().getAmount().compareTo(new BigDecimal(purchase.getPrice())) == 0;
			
		} catch (IamportResponseException e) {
			throw new PaymentValidationException("Iamport response error: " + e.getMessage(), e);
		} catch (IOException e) {
			throw new PaymentValidationException("IO error: " + e.getMessage(), e);
		} catch (Exception e) {
			throw new PaymentValidationException("Validation error: " + e.getMessage(), e);
		}
	}// validatePurchase: 구매 검증

	@Override
	public boolean deletePurchase(int purchaseNo) throws Exception{
		
		return purchaseDao.deletePurchase(purchaseNo) == 1;
	}// deletePurchase: 구매 삭제

	@Override
	public PurchaseDTO getSubscriptionPurchase(Purchase purchase) throws Exception {
		PurchaseDTO subscriptionPurchase = purchaseDao.getSubscriptionPurchase(purchase);

		subscriptionPurchase.setPaymentMethodString(PurchaseUtil.paymentChange(subscriptionPurchase.getPaymentMethod()));
		
		return subscriptionPurchase;
	}
}