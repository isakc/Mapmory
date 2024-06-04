package com.mapmory.controller.purchase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mapmory.services.purchase.domain.Subscription;
import com.mapmory.services.purchase.service.PurchaseService;
import com.mapmory.services.purchase.service.SubscriptionScheduler;
import com.mapmory.services.purchase.service.SubscriptionService;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

@RestController
@RequestMapping("/purchase/*")
public class PurchaseRestController {
	
	///// Field /////
	@Autowired
	@Qualifier("purchaseServiceImpl")
	private PurchaseService purchaseService;
	
	@Autowired
	@Qualifier("subscriptionServiceImpl")
	private SubscriptionService subscriptionService;
	
	@Autowired
	private SubscriptionScheduler subscriptionScheduler;
	
	///// Constructor /////
	
	///// Method /////
	@ResponseBody
	@RequestMapping(value="rest/verifyPurchase/{impUid}")
	public IamportResponse<Payment> verifyPurchase(@PathVariable String impUid) throws Exception  {
		
		return purchaseService.validatePurchase(impUid);
	}
	
	@PostMapping(value="rest/addSubscription")
	public boolean requestSubscription(@RequestBody Subscription subscription) throws Exception {
		return subscriptionService.requestSubscription(subscription);
	}//requestSubscription: 여기에 구독 시작한 날 결제 추가하는 메소드
	
	@PostMapping(value="rest/startScheduler")
	public void startScheduler(@RequestBody Subscription subscription) throws Exception {
		subscriptionScheduler.startScheduler(subscription);
	}//startScheduler: 한달마다 정기결제 등록
}
