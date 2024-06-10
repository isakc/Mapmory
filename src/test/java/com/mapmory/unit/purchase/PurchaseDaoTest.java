package com.mapmory.unit.purchase;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import com.mapmory.common.domain.Search;
import com.mapmory.services.purchase.dao.PurchaseDao;
import com.mapmory.services.purchase.domain.Purchase;
import com.mapmory.services.purchase.dto.PurchaseDTO;

@SpringBootTest
public class PurchaseDaoTest {
	
	@Autowired
	@Qualifier("purchaseDao")
	private PurchaseDao purchaseDao;
	
	@Test
	public void testAddPurchase() throws Exception {
		String userId = "user1";
		int productNo = 1;
		int paymentMethod = 1;
		int price = 1000;
		
		Purchase purchase = Purchase.builder()
				.userId(userId)
				.productNo(productNo)
				.paymentMethod(paymentMethod)
				.purchaseDate(LocalDateTime.now())
				.price(price)
				.build();
		
		Assert.assertEquals(1, purchaseDao.addPurchase(purchase));
	}// addProduct Test : 유저 추가
	
	//@Test
	public void testGetDetailPurchase() throws Exception {
		int purchaseNo = 1;
		
		PurchaseDTO purchase = purchaseDao.getDetailPurchase(purchaseNo);
		
		Assert.assertEquals("user1", purchase.getUserId());
		Assert.assertEquals(1, purchase.getProductNo());
		Assert.assertEquals(0, purchase.getPaymentMethod());
		Assert.assertEquals(10000, purchase.getPrice());
	}// getDetailPurchase Test: 구매 상세내역
		
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
		
		List<PurchaseDTO> purchaseList = purchaseDao.getPurchaseList(search);
		
		Assert.assertEquals(1, purchaseList.size());
		
	}// getPurchaseList Test: 구매 리스트
	
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
		
		Assert.assertEquals(1, purchaseDao.getPurchaseTotalCount(search));
		
	}// testGetPurchaseTotalCount Test: 리스트 개수
}
