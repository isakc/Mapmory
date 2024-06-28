package com.mapmory.services.purchase.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mapmory.common.util.RedisLockUtil;
import com.mapmory.services.product.service.ProductService;
import com.mapmory.services.purchase.domain.Subscription;

@Component
public class SubscriptionScheduler {
	@Autowired
	private SubscriptionService subscriptionService;

	@Autowired
	private ProductService productService;

	@Autowired
    private RedisLockUtil redisLockUtil;

//    private static final String LOCK_KEY = "subscriptionSchedulerLock";
//    private static final long LOCK_EXPIRE_TIME = 300000; // 5 minutes in milliseconds

	@Scheduled(cron = "0 */5 * * * *") 
	public void processSubscriptions() throws Exception {
//		if (redisLockUtil.acquireLock(LOCK_KEY, LOCK_EXPIRE_TIME)) {
//            try {
                List<Subscription> subscriptions = subscriptionService.getTodaySubscriptionList(); // 오늘 구독 결제일인 레코드 리스트
                
                for (Subscription subscription : subscriptions) {
                    try {
                        Subscription updatedSubscription = updateSubscription(subscription);
                        
                        subscriptionService.schedulePay(updatedSubscription, productService.getSubscription());
                        subscriptionService.addSubscriptionFromScheduler(updatedSubscription);
                    } catch (Exception e) {
                        e.printStackTrace(); //결제 실패 처리 로직
                    }//try~catch
                }//for end
//            } finally {
//                redisLockUtil.releaseLock(LOCK_KEY);
//            }
//        }
	}//processSubscriptions: 매일 자정 결제일인 실행
	
	private Subscription updateSubscription(Subscription subscription) throws UnknownHostException {
		InetAddress ip = InetAddress.getLocalHost();
        String ipAddress = ip.getHostAddress();
        
        subscription.setMerchantUid("subscription_" + subscription.getUserId() + "_" + ipAddress);
        subscription.setSubscriptionStartDate(subscription.getSubscriptionEndDate());
        subscription.setSubscriptionEndDate(subscription.getSubscriptionEndDate().plusMinutes(5));
        subscription.setNextSubscriptionPaymentDate(subscription.getSubscriptionEndDate());
        
        return subscription;
    }//updateSubscription: merchantUid, 결제일, 구독 시작일, 구독 종료일 업데이트
}
