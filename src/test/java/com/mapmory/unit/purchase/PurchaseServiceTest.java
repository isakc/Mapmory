package com.mapmory.unit.purchase;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import com.mapmory.common.domain.Search;
import com.mapmory.services.purchase.domain.Purchase;
import com.mapmory.services.purchase.dto.PurchaseDTO;
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
				.price(1000)
				.build();
		
		 purchaseService.addPurchase(purchase);
		
		Assert.assertEquals("user1", purchaseService.getDetailPurchase(purchase.getPurchaseNo()).getUserId());
		Assert.assertEquals(1, purchaseService.getDetailPurchase(purchase.getPurchaseNo()).getProductNo());
		Assert.assertEquals(1, purchaseService.getDetailPurchase(purchase.getPurchaseNo()).getPaymentMethod());
		Assert.assertEquals(1000, purchaseService.getDetailPurchase(purchase.getPurchaseNo()).getPrice());
	}//addProduct Test
	
	//@Test
	public void testAddNullPurchase() throws Exception {
		Purchase purchase = null;
		
		assertFalse(purchaseService.addPurchase(purchase));
		
	}// addNullPurchase Test: Null일 경우
	
	@Test
    @Transactional
    void testAddInvalidPricePurchase() {
		Purchase invalidPurchase = Purchase.builder()
									.userId("user1")
									.productNo(1)
									.paymentMethod(1)
									.purchaseDate(LocalDateTime.now())
									.price(-1000)
									.build();

        assertThrows(DataIntegrityViolationException.class, () -> {
            purchaseService.addPurchase(invalidPurchase);
        });
    }// testAddInvalidPricePurchase: 가격이 음수일 경우
	
	//@Test
	public void testGetDetailPurchase() throws Exception {
		PurchaseDTO purchase = purchaseService.getDetailPurchase(1);
		
		System.out.println(purchase);
		
		Assert.assertEquals("user1", purchase.getUserId());
		Assert.assertEquals(1, purchase.getProductNo());
		Assert.assertEquals(0, purchase.getPaymentMethod());
		Assert.assertEquals(10000, purchase.getPrice());
	}//addProduct Test
		
	//@Test
	public void testGetPurchaseList() throws Exception {
		String userId = "user2";
		int currentPage = 1;
		int limit = 3;
		
		Search search = Search.builder()
						.searchKeyword(userId)
						.limit(limit)
						.currentPage(currentPage)
						.build();
		
		List<PurchaseDTO> purchaseList = purchaseService.getPurchaseList(search);
		
		Assert.assertEquals(1, purchaseList.size());
		
	}//getPurchaseList Test
	
	//@Test
	public void testGetPurchaseTotalCount() throws Exception {
		String userId = "user2";
		int currentPage = 1;
		int limit = 3;
		
		Search search = Search.builder()
						.searchKeyword(userId)
						.limit(limit)
						.currentPage(currentPage)
						.build();
		
		Assert.assertEquals(1, purchaseService.getPurchaseTotalCount(search));
		
	}//testGetPurchaseTotalCount Test
}