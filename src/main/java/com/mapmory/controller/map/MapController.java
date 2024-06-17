package com.mapmory.controller.map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mapmory.common.domain.Search;
import com.mapmory.common.domain.SessionData;
import com.mapmory.common.util.RedisUtil;

@Controller
public class MapController {
	///// Field /////
	@Autowired
	private RedisUtil<SessionData> redisUtil;
	
	///// Constructor /////
	
	///// Method /////
	@GetMapping(value="/map")
	public String mapView(Model model, HttpServletRequest request,@RequestParam(name="searchKeyword",required = false) String searchKeyword) throws Exception {
		//model.addAttribute("userId", redisUtil.getSession(request).getUserId());
		model.addAttribute("userId", "user1");
		if(searchKeyword!=null) {
			model.addAttribute("searchKeyword",searchKeyword);
		}
		return "map/map";
	}// mapView
}
