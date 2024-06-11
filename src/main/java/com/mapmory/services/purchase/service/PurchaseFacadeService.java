package com.mapmory.services.purchase.service;

import com.mapmory.services.product.domain.Product;
import com.mapmory.services.purchase.domain.Purchase;
import com.mapmory.services.purchase.domain.Subscription;

public interface PurchaseFacadeService {

	//insert
	public boolean addSubscription(Purchase purchase, Subscription subscription) throws Exception;
	
	//update
	public boolean updatePaymentMethod(Subscription subscription, Product product) throws Exception;
	
	//cancel
	public boolean cancelSubscription(String userId) throws Exception;
	
}
