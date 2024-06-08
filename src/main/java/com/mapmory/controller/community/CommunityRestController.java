package com.mapmory.controller.community;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mapmory.common.domain.Search;
import com.mapmory.services.community.service.CommunityService;

@RestController
@RequestMapping("community/*")
public class CommunityRestController {

	@Autowired
	@Qualifier("communityServiceImpl")
	private CommunityService communityService;
	
	public CommunityRestController() {
		System.out.println(this.getClass());
	}
	
	@Value("${page.Unit}")
	int PageUnit;
	
	@Value("${page.Size}")
	int PageSize;
	
}
