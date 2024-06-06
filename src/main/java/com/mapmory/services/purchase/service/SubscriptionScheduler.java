package com.mapmory.services.purchase.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;

import com.mapmory.services.purchase.domain.Subscription;

@Component
public class SubscriptionScheduler {
	 @Autowired
	    private SubscriptionService subscriptionService;

	    @Scheduled(cron = "0 * * * * *") // 매일 자정에 실행
	    public void processSubscriptions() throws Exception {
	        List<Subscription> subscriptions = subscriptionService.getTodaySubscriptionList();
	        for (Subscription subscription : subscriptions) {
	            try {
	            	subscription.setNextSubscriptionPaymentDate(subscription.getNextSubscriptionPaymentDate().plusMinutes(1));
	            	subscription.setSubscriptionStartDate(subscription.getNextSubscriptionPaymentDate());
	            	subscription.setSubscriptionEndDate(subscription.getNextSubscriptionPaymentDate().plusMinutes(1));
	            		
	                subscriptionService.schedulePay(subscription);
	                subscriptionService.addSubscription(subscription);
	            } catch (Exception e) {
	                // 결제 실패 처리 로직
	                e.printStackTrace();
	            }
	        }
	    }
}
	    
	//스케줄러
//    private ThreadPoolTaskScheduler scheduler;
//    
//	@Autowired
//	@Qualifier("subscriptionServiceImpl")
//	private SubscriptionService subscriptionService;
//	
//    public void stopScheduler() {
//        scheduler.shutdown();
//    }
// 
//    public void startScheduler(Subscription subscription) {
//        scheduler = new ThreadPoolTaskScheduler();
//        scheduler.initialize();
//        scheduler.schedule(getRunnable(subscription), getTrigger()); //스케쥴러 시작
//    }
//    
//    private Runnable getRunnable(Subscription subscription){
//        return () -> {
//        	try {
//        		Subscription paidSubscription = subscriptionService.schedulePay(subscription);
//        		if(subscriptionService.countSubscription(subscription.getUserId()) == 0) {
//            		subscriptionService.addSubscription(paidSubscription);
//        		}else {
//        			subscriptionService.updatePaymentMethod(paidSubscription);
//        		}
//        		
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//        };
//    }
// 
//    private Trigger getTrigger() {
//    	return new PeriodicTrigger(1, TimeUnit.MINUTES); // 작업 주기 설정 
//    	//return new PeriodicTrigger(31, TimeUnit.DAYS); // 작업 주기 설정 
//    }
//}