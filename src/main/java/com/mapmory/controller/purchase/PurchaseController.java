package com.mapmory.controller.purchase;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import com.mapmory.common.domain.Search;
import com.mapmory.services.product.domain.Product;
import com.mapmory.services.product.service.ProductService;
import com.mapmory.services.purchase.domain.Purchase;
import com.mapmory.services.purchase.domain.Subscription;
import com.mapmory.services.purchase.service.PurchaseService;
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
	
	@Autowired
	@Qualifier("productServiceImpl")
	private ProductService productService;
	
	///// Method /////
	
	@GetMapping(value="/addPurchaseView/{productNo}")
	public String addPurchaseView(@PathVariable("productNo") int proudctNo, Model model) throws Exception {
		
		Product product = productService.getDetailProduct(proudctNo);
		
		model.addAttribute("product", product);
		
		return "purchase/addPurchase";
	}// addPurchaseView
	
	@GetMapping("/getPurchaseList")
	public String getPurchaseList(@ModelAttribute Search search, Model model) throws Exception {
		
		model.addAttribute("purchaseList", purchaseService.getPurchaseList(search));
		
        return "purchase/getPurchaseList";
    }// getPurchaseList
	
	@PostMapping(value="/addPurchase")
	public RedirectView addPurchase(@RequestBody Purchase purchase) {
		try {
			purchaseService.addPurchase(purchase);
		} catch (Exception e) {
			return new RedirectView("/index");
		}
		
		return new RedirectView("/purchase/getPurchaseList");
	}// addPurchase
	
	@PostMapping(value="/addSubscription")
	public String requestSubscription(@RequestBody Subscription subscription) throws Exception {
		
		if(subscriptionService.requestSubscription(subscription)) {
			subscriptionService.addSubscription(subscription);
			
			subscription.setMerchantUid("subscription_" + subscription.getUserId() + "_" + LocalDateTime.now());
			subscriptionService.schedulePay(subscription);
		}

		return "index";
	}// requestSubscription: 구독 시작한 날 결제 추가
	
	@PostMapping(value="updatePaymentMethod")
	public String updatePaymentMethod(@RequestBody Subscription subscription) throws Exception {
		
		subscriptionService.updatePaymentMethod(subscription);
		subscriptionService.schedulePay(subscription);
		
		return "index";
	}// updatePaymentMethod: 구독 결제 수단 변경

	@GetMapping(value="/deleteSubscription/{userId}")
	public RedirectView deleteSubscription(@PathVariable("userId") String userId) throws Exception {
		
		subscriptionService.deleteSubscription(userId);
		
		return new RedirectView("/index");
	}// deleteSubscription: 구독 해지
}