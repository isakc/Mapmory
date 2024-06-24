package com.mapmory.services.purchase.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mapmory.common.domain.Search;
import com.mapmory.common.util.PurchaseUtil;
import com.mapmory.services.purchase.dao.PurchaseDao;
import com.mapmory.services.purchase.domain.Purchase;
import com.mapmory.services.purchase.dto.PurchaseDTO;
import com.mapmory.services.purchase.service.PurchaseService;
import com.siot.IamportRestClient.IamportClient;
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
		IamportResponse<Payment> returnPayment = iamportClient.paymentByImpUid(purchase.getImpUid());
		
		int paymentMethod = PurchaseUtil.paymentChangeToInt(returnPayment.getResponse().getPgProvider());
		
		purchase.setPrice(returnPayment.getResponse().getAmount().intValue());
		purchase.setPaymentMethod(paymentMethod);
		purchase.setPurchaseDate(LocalDateTime.ofInstant(returnPayment.getResponse().getPaidAt().toInstant(), ZoneId.systemDefault()));
		
		if(paymentMethod == 1) {
			purchase.setCardType(returnPayment.getResponse().getCardName());
			purchase.setLastFourDigits(returnPayment.getResponse().getCardNumber().substring(0, 4));
		}

		return purchaseDao.addPurchase(purchase) == 1;
	}// addPurchase: 결제 추가

	@Override
	public PurchaseDTO getDetailPurchase(int purchaseNo) throws Exception{
		
		return purchaseDao.getDetailPurchase(purchaseNo);
	}// getDetailPurchase: 결제 상세 정보

	@Override
	public List<PurchaseDTO> getPurchaseList(Search search) throws Exception {
		int offset = (search.getCurrentPage() - 1) * search.getPageSize();
   	 	search.setOffset(offset);
        search.setLimit(search.getPageSize());
        
		List<PurchaseDTO> purchaseList = purchaseDao.getPurchaseList(search);

		for (PurchaseDTO purchase : purchaseList) {
			purchase.setPaymentMethodString(PurchaseUtil.paymentChangeToString(purchase.getPaymentMethod()));
			purchase.setPurchaseDateString(PurchaseUtil.purchaseDateChange(purchase.getPurchaseDate()));
		}

		return purchaseList;
	}// getPurchaseList: 구매 목록 가져오기

	@Override
	public int getPurchaseTotalCount(Search search) throws Exception {

		return purchaseDao.getPurchaseTotalCount(search);
	}// getPurchaseTotalCount: 구매 총 개수

//	@Override
//	public boolean validatePurchase(String impUid, Purchase purchase) {
//		try {
//			IamportResponse<Payment> validation = iamportClient.paymentByImpUid(impUid);
//			
//			return validation.getResponse().getAmount().compareTo(new BigDecimal(purchase.getPrice())) == 0;
//			
//		} catch (IamportResponseException e) {
//			throw new PaymentValidationException("Iamport response error: " + e.getMessage(), e);
//		} catch (IOException e) {
//			throw new PaymentValidationException("IO error: " + e.getMessage(), e);
//		} catch (Exception e) {
//			throw new PaymentValidationException("Validation error: " + e.getMessage(), e);
//		}
//	}// validatePurchase: 구매 검증

	@Override
	public boolean deletePurchase(int purchaseNo) throws Exception{
		
		return purchaseDao.deletePurchase(purchaseNo) == 1;
	}// deletePurchase: 구매 삭제

	@Override
	public PurchaseDTO getSubscriptionPurchase(Purchase purchase) throws Exception {
		PurchaseDTO subscriptionPurchase = purchaseDao.getSubscriptionPurchase(purchase);
		
		if(subscriptionPurchase != null) {
			subscriptionPurchase.setPaymentMethodString(PurchaseUtil.paymentChangeToString(subscriptionPurchase.getPaymentMethod()));
		}
		
		return subscriptionPurchase;
	}
}