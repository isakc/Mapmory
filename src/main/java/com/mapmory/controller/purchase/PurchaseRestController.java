package com.mapmory.controller.purchase;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mapmory.common.domain.Search;
import com.mapmory.common.domain.SessionData;
import com.mapmory.common.util.RedisUtil;
import com.mapmory.services.purchase.domain.Subscription;
import com.mapmory.services.purchase.dto.PurchaseDTO;
import com.mapmory.services.purchase.service.PurchaseService;
import com.mapmory.services.purchase.service.SubscriptionService;

@RestController
@RequestMapping("/purchase/*")
public class PurchaseRestController {

	@Autowired
	private RedisUtil<SessionData> redisUtil;
	
	@Autowired
	@Qualifier("purchaseServiceImpl")
	private PurchaseService purchaseService;
	
	@Autowired
	@Qualifier("subscriptionServiceImpl")
	private SubscriptionService subscriptionService;
	
    @Value("${page.Size}")
    private int pageSize;
	
	@GetMapping(value="/rest/getPurchaseList")
	public List<PurchaseDTO> getPurchaseList(Search search, HttpServletRequest request) throws Exception {
		if (search.getCurrentPage() == 0) {
            search.setCurrentPage(1);
        }
		
		search.setSearchKeyword(redisUtil.getSession(request).getUserId());
		search.setPageSize(pageSize);
		
		return purchaseService.getPurchaseList(search);
	}// getPurchaseList
	
	@GetMapping(value="/rest/getSubscriptionList")
	public List<Subscription> getSubscriptionList(Search search, HttpServletRequest request) throws Exception {
		if (search.getCurrentPage() == 0) {
            search.setCurrentPage(1);
        }
		
		search.setSearchKeyword(redisUtil.getSession(request).getUserId());
		search.setPageSize(pageSize);
		
		return subscriptionService.getSubscriptionList(search);
	}// getPurchaseList
}
