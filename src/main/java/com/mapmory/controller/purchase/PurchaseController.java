package com.mapmory.controller.purchase;

import java.time.LocalDateTime;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
		System.out.println(product.getPeriod());
		
		model.addAttribute("product", product);
		
		return "purchase/addPurchase";
	}// addPurchaseView
	
	@PostMapping(value="addPurchase/{impUid}")
	public String addPurchase(@PathVariable String impUid, @ModelAttribute Purchase purchase) throws Exception  {
		
		if( purchaseService.validatePurchase(impUid, purchase) ) {
			if(purchaseService.addPurchase(purchase)) {
				return "redirect:/purchase/getPurchaseList";
			}
		}
		
		return "index";
	}// verifyPurchase: 구매 검증 메소드
	
	@GetMapping("/getPurchaseList")
	public String getPurchaseList(@ModelAttribute(value = "search") Search search, Model model, HttpSession session) throws Exception {
		
		if (search.getCurrentPage() == 0) {
			search.setCurrentPage(1);
		}
		
		search.setSearchKeyword("user1");
		search.setLimit(3);
		
		model.addAttribute("purchaseList", purchaseService.getPurchaseList(search));
		
        return "purchase/getPurchaseList";
    }// getPurchaseList
	
	@PostMapping(value="/addSubscription")
	public String requestSubscription(@ModelAttribute Subscription subscription, Model model) throws Exception {
		
		if(subscriptionService.requestSubscription(subscription)) {
			subscriptionService.addSubscription(subscription);
			
			subscription.setMerchantUid("subscription_" + subscription.getUserId() + "_" + LocalDateTime.now());
			subscriptionService.schedulePay(subscription);
		}
		
		model.addAttribute("subscription", subscriptionService.getDetailSubscription(subscription.getUserId()));

		return "redirect:/purchase/getDetailSubscription";
	}// requestSubscription: 구독 시작한 날 결제 추가
	
	@GetMapping(value="/getDetailSubscription")
	public String getDetailSubscription(Model model) throws Exception {
		
		model.addAttribute("subscription", subscriptionService.getDetailSubscription("user7"));//&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

		return "purchase/getDetailSubscription";
	}// requestSubscription: 구독 시작한 날 결제 추가
	
	@GetMapping(value="/updatePaymentMethod")
	public String updatePaymentMethodView() throws Exception {
		
		return "purchase/updatePaymentMethod";
		
	}// updatePaymentMethodView: 구독 결제 수단 변경 네비게이션
	
	@PostMapping(value="/updatePaymentMethod")
	public String updatePaymentMethod(@ModelAttribute Subscription subscription) throws Exception {
		
		subscriptionService.updatePaymentMethod(subscription);
		subscriptionService.schedulePay(subscription);
		
		return "redirect:/purchase/getDetailSubscription";
	}// updatePaymentMethod: 구독 결제 수단 변경

	@GetMapping(value="/deleteSubscription/{userId}")
	public String deleteSubscription(@PathVariable("userId") String userId) throws Exception {
		
		subscriptionService.deleteSubscription(userId);
		
		return "redirect:/purchase/getDetailSubscription";
	}// deleteSubscription: 구독 해지
}