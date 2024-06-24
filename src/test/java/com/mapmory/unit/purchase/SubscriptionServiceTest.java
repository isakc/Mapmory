package com.mapmory.unit.purchase;

import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import com.mapmory.services.product.service.ProductService;
import com.mapmory.services.purchase.domain.Subscription;
import com.mapmory.services.purchase.service.SubscriptionService;

@SpringBootTest
public class SubscriptionServiceTest {
	
	@Autowired
	@Qualifier("subscriptionServiceImpl")
	private SubscriptionService subscriptionService;
	
	@Autowired
	@Qualifier("productServiceImpl")
	private ProductService productService;
	
	//@Test
	public void testAddSubscription() throws Exception{
		Subscription subscription = Subscription.builder()
				.userId("user1")
				.nextSubscriptionPaymentMethod(0)
				.nextSubscriptionCardType("국민카드")
				.nextSubscriptionLastFourDigits("1234")
				.nextSubscriptionPaymentDate(LocalDateTime.now())
				.subscriptionStartDate(LocalDateTime.now())
				.subscriptionEndDate(LocalDateTime.now().plusMonths(1))
				.customerUid("test1234")
				.merchantUid("monthly01")
				.build();
				
		//subscriptionService.addSubscription(subscription);
		//Assert.assertEquals("user1", subscription);
	}
	
	//@Test
	public void testGetDetailSubscription() throws Exception{
		String userId = "user1";
		
		Subscription subscription = subscriptionService.getDetailSubscription(userId);
		
		Assert.assertEquals(userId, subscription.getUserId());
		Assert.assertEquals(0, subscription.getNextSubscriptionPaymentMethod());
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
				.nextSubscriptionPaymentMethod(0)
				.nextSubscriptionCardType("BC 카드")
				.nextSubscriptionLastFourDigits("5678")
				.customerUid("test02")
				.merchantUid("monthly02")
				.build();
		
		subscriptionService.updatePaymentMethod(subscription);
		Subscription updateSubscription = subscriptionService.getDetailSubscription(userId);
		
		Assert.assertEquals(userId, updateSubscription.getUserId());
		Assert.assertEquals(0, updateSubscription.getNextSubscriptionPaymentMethod());
		Assert.assertEquals("BC 카드", subscription.getNextSubscriptionCardType());
		Assert.assertEquals("5678", subscription.getNextSubscriptionLastFourDigits());
		Assert.assertEquals("test02", subscription.getCustomerUid());
		Assert.assertEquals("monthly02", subscription.getMerchantUid());
	}
	
	//@Test
	public void testCancleSubscription() throws Exception{
			
		String userId = "user1";
		
		subscriptionService.cancelSubscription(userId);

		Subscription subscription = subscriptionService.getDetailSubscription(userId);
		
		Assert.assertEquals(null, subscription.getNextSubscriptionPaymentDate());
	}
	
	//@Test
	public void testRequestSubscription() throws Exception {
		
		Subscription subscription = Subscription.builder()
									.userId("user7")
									.customerUid("user7")
									.merchantUid("subscription_user7_"+LocalDateTime.now())
									.build();
		
		boolean flag = subscriptionService.requestSubscription(subscription, productService.getSubscription());
		
		Assert.assertEquals(flag, true);
	}
	
	//@Test
	public void testSchedulePay() throws Exception {
		Subscription subscription = Subscription.builder()
									.userId("user7")
									.customerUid("user7")
									.merchantUid("subscription_user7_"+LocalDateTime.now())
									.nextSubscriptionPaymentDate(LocalDateTime.of(2024, 6, 28, 0, 0, 0))
									.build();

		Assert.assertEquals(true, subscriptionService.schedulePay( subscription, productService.getSubscription()) );
	}
	
	//@Test
	public void testGetTodaySubscriptionList() throws Exception {
		Assert.assertEquals(0, subscriptionService.getTodaySubscriptionList().size());
	}
}
