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
import com.mapmory.services.purchase.service.PurchaseFacadeService;
import com.mapmory.services.purchase.service.PurchaseService;
import com.mapmory.services.purchase.service.SubscriptionService;
import com.mapmory.services.user.domain.User;

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
	@Qualifier("purchaseFacadeServiceImpl")
	private PurchaseFacadeService purchaseFacadeService;
	
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
	
	@PostMapping(value="addPurchase/{impUid}")
	public String addPurchase(@PathVariable String impUid, @ModelAttribute Purchase purchase, Model model) throws Exception {

		if(purchaseService.validatePurchase(impUid, purchase)) {
			purchaseService.addPurchase(purchase);
		}else {
			model.addAttribute("errorMessage", "검증하는데 실패하였습니다.");
			return "common/error";
		}
				
		return "redirect:/purchase/getPurchaseList";
	}// addPurchase: 구매 검증 후 추가 메소드
	
	@GetMapping("/getPurchaseList")
	public String getPurchaseList(@ModelAttribute(value = "search") Search search, Model model, HttpSession session) throws Exception {
		
		if (search.getCurrentPage() == 0) {
			search.setCurrentPage(1);
		}
		
		User user = (User) session.getAttribute("user");
		search.setSearchKeyword("user1");//@@@@@@@@@@@@@@@@@@@@@@@@@@@
		//search.setSearchKeyword(user.getUserId()); 
		search.setLimit(3);//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
		
		model.addAttribute("purchaseList", purchaseService.getPurchaseList(search));
		
        return "purchase/getPurchaseList";
    }// getPurchaseList
	
	@PostMapping(value="/addSubscription")
	public String requestSubscription(@ModelAttribute Subscription subscription, @ModelAttribute Purchase purchase, Model model) throws Exception {
		
		Product product = productService.getSubscription();
		
		purchaseFacadeService.addSubscription(purchase, subscription); 
		
		try {
			subscriptionService.requestSubscription(subscription, product);
			
			subscription.setMerchantUid("subscription_" + subscription.getUserId() + "_" + LocalDateTime.now());
			subscriptionService.schedulePay(subscription, product);
		}
		catch(Exception e) {
			subscriptionService.deleteSubscription(subscription.getSubscriptionNo());
			purchaseService.deletePurchase(purchase.getPurchaseNo());
				
			model.addAttribute("errorMessage", "결제중 에러 발생");
				
			return "common/error";
		}
		
		model.addAttribute("subscription", subscriptionService.getDetailSubscription(subscription.getUserId()));

		return "redirect:/purchase/getDetailSubscription";
	}// requestSubscription: 구독 시작한 날 결제 추가
	
	@GetMapping(value="/getDetailSubscription")
	public String getDetailSubscription(Model model, HttpSession session) throws Exception {
		User user = (User) session.getAttribute("user");
		
		model.addAttribute("subscription", subscriptionService.getDetailSubscription("user7")); // user.getUserId() @@@@@@@@@@@@@

		return "purchase/getDetailSubscription";
	}// getDetailSubscription: 구독 상세 보기
	
	@GetMapping(value="/updatePaymentMethod")
	public String updatePaymentMethodView(Model model, HttpSession session) throws Exception {
		
		model.addAttribute("currentSubscription", subscriptionService.getDetailSubscription("user7"));// user.getUserId() @@@@@@@@@@@@@
		
		return "purchase/updatePaymentMethod";
		
	}// updatePaymentMethodView: 구독 결제 수단 변경 네비게이션
	
	@PostMapping(value="/updatePaymentMethod")
	public String updatePaymentMethod(@ModelAttribute Subscription subscription) throws Exception {
		
		purchaseFacadeService.updatePaymentMethod(subscription, productService.getSubscription());
		
		return "redirect:/purchase/getDetailSubscription";
	}// updatePaymentMethod: 구독 결제 수단 변경

	@GetMapping(value="/cancelSubscription")
	public String cancelSubscription(HttpSession session) throws Exception {

		User user = (User) session.getAttribute("user");
		
		purchaseFacadeService.cancelSubscription("user7"); // user.getUserId() @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
		
		return "redirect:/purchase/getDetailSubscription";
	}// deleteSubscription: 구독 해지
	
	@GetMapping(value="/purchaseMenu")
	public String purchaseMenu(Model model) throws Exception {
		
		model.addAttribute("subscription", productService.getSubscription());
		
		return "purchase/purchaseMenu";
	}// purchaseMenu: 구독 메뉴
}