package com.mapmory.controller.purchase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import com.mapmory.services.purchase.domain.Purchase;
import com.mapmory.services.purchase.domain.Subscription;
import com.mapmory.services.purchase.service.PurchaseService;
import com.mapmory.services.purchase.service.SubscriptionScheduler;
import com.mapmory.services.purchase.service.SubscriptionService;

@Controller
@RequestMapping("/purchase/*")
public class PurchaseController {
	
	///// Field /////
	
	@Autowired
	@Qualifier("purchaseServiceImpl")
	private PurchaseService purchaseService;
	
	@Autowired
	@Qualifier("subscriptionServiceImpl")
	private SubscriptionService subscriptionService;
	
	///// Method /////
	
	@GetMapping(value="/addPurchaseView")
	//public String addPurchaseView(int proudctNo) throws Exception {
	public String addPurchaseView() throws Exception {
		
		return "purchase/addPurchase";
	}//addPurchaseView
	
	@PostMapping(value="/addPurchase")
	public RedirectView addPurchase(@RequestBody Purchase purchase) throws Exception {
		
		purchaseService.addPurchase(purchase);
		return new RedirectView("/index");
	}//addPurchase
	
	@GetMapping(value="/deleteSubscription")
	public RedirectView deleteSubscription(String userId) throws Exception {
		
		subscriptionService.deleteSubscription(userId);
		return new RedirectView("/index");
	}//deleteSubscription
}
