package com.mapmory.unit.purchase;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import com.mapmory.services.purchase.domain.Purchase;
import com.mapmory.services.purchase.service.PurchaseService;

@SpringBootTest
public class PurchaseServiceTest {
	
	@Autowired
	@Qualifier("purchaseServiceImpl")
	private PurchaseService purchaseService;
	
	//@Test
	public void testAddPurchase() throws Exception {
		Purchase purchase = Purchase.builder()
				.userId("user1")
				.productNo(1)
				.paymentMethod(1)
				.purchaseDate(LocalDateTime.now())
				.price(1000).build();
		
		purchaseService.addPurchase(purchase);
		
		Assert.assertEquals("user1", purchaseService.getPurchase(purchase.getPurchaseNo()).getUserId());
		Assert.assertEquals(1, purchaseService.getPurchase(purchase.getPurchaseNo()).getProductNo());
		Assert.assertEquals(1, purchaseService.getPurchase(purchase.getPurchaseNo()).getPaymentMethod());
		Assert.assertEquals(1000, purchaseService.getPurchase(purchase.getPurchaseNo()).getPrice());
	}//addProduct Test
	
	//@Test
	public void testGetPurchase() throws Exception {
		Purchase purchase = purchaseService.getPurchase(1);
		
		System.out.println(purchase);
		
		Assert.assertEquals("user2", purchase.getUserId());
		Assert.assertEquals(1, purchase.getProductNo());
		Assert.assertEquals(0, purchase.getPaymentMethod());
		Assert.assertEquals(10000, purchase.getPrice());
	}//addProduct Test
		
	@Test
	public void testGetPurchaseList() throws Exception {
		String userId = "user2";
		List<Purchase> purchaseList = purchaseService.getPurchaseList(userId);
		
		System.out.println(purchaseList);
		
		Assert.assertEquals(1, purchaseList.size());
		
	}//getPurchaseList Test
	
	//@Test
	public void testGetPurchaseTotalCount() throws Exception {
		String userId = "user2";
		
		Assert.assertEquals(1, purchaseService.getPurchaseTotalCount(userId));
		
	}//testGetPurchaseTotalCount Test
}