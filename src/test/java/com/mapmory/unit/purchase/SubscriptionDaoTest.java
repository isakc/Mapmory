package com.mapmory.unit.purchase;

import java.time.LocalDateTime;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import com.mapmory.services.purchase.dao.SubscriptionDao;
import com.mapmory.services.purchase.domain.Subscription;

@SpringBootTest
public class SubscriptionDaoTest {
	
	@Autowired
	@Qualifier("subscriptionDao")
	private SubscriptionDao subscriptionDao;
	
	//@Test
	public void testAddSubscription() throws Exception{
		Subscription subscription = Subscription.builder()
				.userId("user1")
				.nextPaymentMethod(0)
				.nextSubscriptionCardType("국민카드")
				.nextSubscriptionLastFourDigits("1234")
				.nextSubscriptionPaymentDate(LocalDateTime.now())
				.subscriptionStartDate(LocalDateTime.now())
				.subscriptionEndDate(LocalDateTime.now().plusMonths(1))
				.customerUid("test1234")
				.merchantUid("monthly01")
				.build();
				
		subscriptionDao.addSubscription(subscription);
		//Assert.assertEquals("user1", subscription);
		
	}
	
	//@Test
	public void testGetDetailSubscription() throws Exception{
		String userId = "user1";
		
		Subscription subscription = subscriptionDao.getDetailSubscription(userId);
		
		Assert.assertEquals(userId, subscription.getUserId());
		Assert.assertEquals(0, subscription.getNextPaymentMethod());
		Assert.assertEquals("Visa", subscription.getNextSubscriptionCardType());
		Assert.assertEquals("1234", subscription.getNextSubscriptionLastFourDigits());
		Assert.assertEquals("CUST001", subscription.getCustomerUid());
		Assert.assertEquals("ORD001", subscription.getMerchantUid());
	}
	
	@Test
	public void testUpdatePaymentMethod() throws Exception{
		String userId = "user1";
		
		Subscription subscription = Subscription.builder()
				.userId(userId)
				.nextPaymentMethod(0)
				.nextSubscriptionCardType("BC 카드")
				.nextSubscriptionLastFourDigits("5678")
				.customerUid("test02")
				.merchantUid("monthly02")
				.build();
		
		subscriptionDao.updatePaymentMethod(subscription);
		Subscription updateSubscription = subscriptionDao.getDetailSubscription(userId);
		
		Assert.assertEquals(userId, updateSubscription.getUserId());
		Assert.assertEquals(0, updateSubscription.getNextPaymentMethod());
		Assert.assertEquals("BC 카드", subscription.getNextSubscriptionCardType());
		Assert.assertEquals("5678", subscription.getNextSubscriptionLastFourDigits());
		Assert.assertEquals("test02", subscription.getCustomerUid());
		Assert.assertEquals("monthly02", subscription.getMerchantUid());
	}
	
	//@Test
	public void testDeleteSubscription() throws Exception{
			
		String userId = "user1";
		
		subscriptionDao.deleteSubscription(userId);

		Subscription subscription = subscriptionDao.getDetailSubscription(userId);
		
		Assert.assertEquals(null, subscription.getNextSubscriptionPaymentDate());
		
	}
}
