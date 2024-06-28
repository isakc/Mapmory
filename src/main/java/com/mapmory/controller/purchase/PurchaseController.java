package com.mapmory.controller.purchase;

import java.time.LocalDateTime;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mapmory.common.domain.Search;
import com.mapmory.common.domain.SessionData;
import com.mapmory.common.util.RedisUtil;
import com.mapmory.services.product.domain.Product;
import com.mapmory.services.product.service.ProductService;
import com.mapmory.services.purchase.domain.Purchase;
import com.mapmory.services.purchase.domain.Subscription;
import com.mapmory.services.purchase.dto.PurchaseDTO;
import com.mapmory.services.purchase.service.PurchaseFacadeService;
import com.mapmory.services.purchase.service.PurchaseService;
import com.mapmory.services.purchase.service.SubscriptionService;

@Controller
@RequestMapping("/purchase/*")
public class PurchaseController {
	
	///// Field /////
	
	@Autowired
	private RedisUtil<SessionData> redisUtil;
	
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
	
	@Value("${object.purchase.folder.name}")
	private String folderName;

    @Value("${page.Size}")
    private int pageSize;
	
	///// Method /////
	
	@GetMapping(value="/addPurchaseView/{productNo}")
	public String addPurchaseView(@PathVariable("productNo") int productNo, Model model, HttpServletRequest request) throws Exception {
		Subscription checkSubscription = subscriptionService.getDetailSubscription(redisUtil.getSession(request).getUserId());
		
		if(productNo == productService.getSubscription().getProductNo() && checkSubscription!=null && checkSubscription.isSubscribed() ) {
			model.addAttribute("message", "현재 구독 중입니다.");
		}
		
		Product product = productService.getDetailProduct(productNo);
		
		model.addAttribute("userId", redisUtil.getSession(request).getUserId());
		model.addAttribute("product", product);
		
		return "purchase/addPurchase";
	}// addPurchaseView
	
	@GetMapping(value="addPurchase/{productNo}")
	public String addPurchase(
			@PathVariable("productNo") int productNo,
			@RequestParam(required = false) String imp_uid, 
			@RequestParam(required = false) String merchant_uid, 
			@RequestParam(required = false) boolean imp_success,
			HttpServletRequest request,
			Model model) throws Exception {

		if(imp_success) {
			Purchase purchase = Purchase.builder()
					.userId(redisUtil.getSession(request).getUserId())
					.productNo(productNo)
					.impUid(imp_uid)
					.build();
			
			purchaseService.addPurchase(purchase);
			
			return "redirect:/purchase/getPurchaseList";
		}else {
			model.addAttribute("errorMessage", "검증하는데 실패하였습니다.");
			
			return "common/error";
		}
	}// addPurchase: 구매 검증 후 추가 메소드
	
	@GetMapping("/getPurchaseList")
	public String getPurchaseList(@ModelAttribute(value = "search") Search search, Model model, HttpServletRequest request) throws Exception {
		
		if (search.getCurrentPage() == 0) {
			search.setCurrentPage(1);
		}
		
		search.setSearchKeyword(redisUtil.getSession(request).getUserId());
		search.setPageSize(pageSize);
		
		model.addAttribute("purchaseList", purchaseService.getPurchaseList(search));
		
        return "purchase/getPurchaseList";
    }// getPurchaseList
	
	@GetMapping(value="/addSubscription/{productNo}")
	public String addSubscription(
			@PathVariable("productNo") int productNo, 
			@RequestParam(required = false) String imp_uid, 
			@RequestParam(required = false) String merchant_uid, 
			@RequestParam(required = false) boolean imp_success,
			HttpServletRequest request, Model model) throws Exception {
		
		Product product = productService.getSubscription();
		String userId = redisUtil.getSession(request).getUserId();
		
		Purchase purchase = Purchase.builder()
							.productNo(productNo)
							.userId(userId)
							.impUid(imp_uid)
							.build();
		
		if(imp_success) {
			if(purchaseFacadeService.addSubscription(purchase, productService.getDetailProduct(purchase.getProductNo()))) {
				Subscription addSubscription = subscriptionService.getDetailSubscription(userId);
				
				try {
					addSubscription.setMerchantUid("subscription_"+userId+"_"+LocalDateTime.now());
					subscriptionService.schedulePay(addSubscription, product);
				}
				catch(Exception e) {
					subscriptionService.deleteSubscription(addSubscription.getSubscriptionNo());
					purchaseService.deletePurchase(purchase.getPurchaseNo());
						
					model.addAttribute("errorMessage", "결제중 에러 발생");
					
					return "common/error";
				}//try~catch
				
				model.addAttribute("subscription", subscriptionService.getDetailSubscription(userId));
			}// db 저장하는 것이 성공하면
			else {
				model.addAttribute("errorMessage", "결제중 에러 발생");
				
				return "common/error";
			}// db 저장하는 것이 실패하면
		}//만약 success면
		else {
			model.addAttribute("errorMessage", "결제중 에러 발생");
			
			return "common/error";
		}

		return "redirect:/purchase/getDetailSubscription";
	}// requestSubscription: 구독 시작한 날 결제 추가
	
	@GetMapping(value="/reSubscription")
	public String reSubscription(HttpServletRequest request) throws Exception {
		
		String userId = redisUtil.getSession(request).getUserId();
		
		Subscription subscription = subscriptionService.getDetailSubscription(userId);
		subscription.setMerchantUid("subscription_" + userId + "_" + LocalDateTime.now());
		subscription.setNextSubscriptionPaymentDate(subscription.getSubscriptionEndDate());
		
		if(subscriptionService.reSubscription(subscription)) {
			subscriptionService.schedulePay(subscription, productService.getSubscription());
		}

		return "redirect:/purchase/getDetailSubscription";
	}// requestSubscription: 구독 시작한 날 결제 추가
	
	@GetMapping(value="/getDetailSubscription")
	public String getDetailSubscription(Model model, HttpServletRequest request) throws Exception {
		String userId = redisUtil.getSession(request).getUserId();
		
		PurchaseDTO purchase = purchaseService.getSubscriptionPurchase(
				Purchase.builder().userId(userId).productNo(productService.getSubscription().getProductNo()).build());

		if(purchase != null) {
			model.addAttribute("purchase", purchase);
		}
		model.addAttribute("subscription", subscriptionService.getDetailSubscription(userId));
		model.addAttribute("productSubscription", productService.getSubscription());
		
		return "purchase/getDetailSubscription";
	}// getDetailSubscription: 구독 상세 보기

	@GetMapping(value="/getSubscriptionList")
	public String getSubscriptionList(@ModelAttribute(value = "search") Search search, Model model, HttpServletRequest request) throws Exception {
		if (search.getCurrentPage() == 0) {
			search.setCurrentPage(1);
		}
		
		search.setSearchKeyword(redisUtil.getSession(request).getUserId());
		search.setPageSize(pageSize);
		
		List<Subscription> subscriptionList = subscriptionService.getSubscriptionList(search);
		
		model.addAttribute("subscriptionList", subscriptionList);
		
		return "purchase/getSubscriptionList";
	}// getDetailSubscription: 구독 상세 보기
	
	@GetMapping(value="/updatePaymentMethodView")
	public String updatePaymentMethodView(Model model, HttpServletRequest request) throws Exception {
		String userId = redisUtil.getSession(request).getUserId();
		
		model.addAttribute("userId", userId);
		model.addAttribute("currentSubscription", subscriptionService.getDetailSubscription(userId));
		model.addAttribute("productSubscription", productService.getSubscription());
		
		return "purchase/updatePaymentMethod";
		
	}// updatePaymentMethodView: 구독 결제 수단 변경 네비게이션
	
	@GetMapping(value="/updatePaymentMethod")
	public String updatePaymentMethod(
			@RequestParam(required = false) String imp_uid, 
			@RequestParam(required = false) String merchant_uid, 
			@RequestParam(required = false) boolean imp_success,
			HttpServletRequest request,
			Model model) throws Exception {
		
		String userId = redisUtil.getSession(request).getUserId();
		Purchase purchase = Purchase.builder()
							.userId(userId)
							.impUid(imp_uid)
							.build();
		
		// 결제한 방법으로 결제하기
		if(imp_success) {
			purchaseFacadeService.updatePaymentMethod(purchase, productService.getSubscription());
		}else {
			model.addAttribute("errorMessage", "결제중 에러 발생");
			
			return "common/error";
		}
		
		return "redirect:/purchase/getDetailSubscription";
	}// updatePaymentMethod: 구독 결제 수단 변경

	@GetMapping(value="/cancelSubscription")
	public String cancelSubscription(HttpServletRequest request) throws Exception {
		
		purchaseFacadeService.cancelSubscription(redisUtil.getSession(request).getUserId());
		
		return "redirect:/purchase/getDetailSubscription";
	}// cancelSubscription: 구독 해지
	
	@GetMapping(value="/subscriptionMenu")
	public String purchaseMenu(Model model) throws Exception {
		
		model.addAttribute("subscription", productService.getSubscription());
		
		return "purchase/subscriptionMenu";
	}// purchaseMenu: 구독 메뉴
	
	@GetMapping("/image/{uuid}")
    @ResponseBody
    public byte[] getImage(@PathVariable("uuid") String uuid) throws Exception {
        return productService.getImageBytes(uuid, folderName);
    }
}