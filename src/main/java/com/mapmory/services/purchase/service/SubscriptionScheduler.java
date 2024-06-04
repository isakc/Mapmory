package com.mapmory.services.purchase.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;

import com.mapmory.services.purchase.domain.Subscription;

@Component
public class SubscriptionScheduler {
	//스케줄러
    private ThreadPoolTaskScheduler scheduler;
    
	@Autowired
	@Qualifier("subscriptionServiceImpl")
	private SubscriptionService subscriptionService;
	
    public void stopScheduler() {
        scheduler.shutdown(); //구독 취소 시 scheduler shutdown을 통해 결제 요청 멈춤
    }
 
    public void startScheduler(Subscription subscription) {
        scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize();
        scheduler.schedule(getRunnable(subscription), getTrigger()); //스케쥴러 시작
    }
    
    private Runnable getRunnable(Subscription subscription){
        return () -> {
        	try {
        		Subscription paidSubscription = subscriptionService.schedulePay(subscription);
        		subscriptionService.addSubscription(paidSubscription);
        		
			} catch (Exception e) {
				e.printStackTrace();
			}
        };
    }
 
    private Trigger getTrigger() {
    	return new PeriodicTrigger(1, TimeUnit.MINUTES); // 작업 주기 설정 
    }
}