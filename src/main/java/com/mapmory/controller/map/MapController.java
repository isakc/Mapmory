package com.mapmory.controller.map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MapController {
	///// Field /////
	
	///// Constructor /////
	
	///// Method /////
	@GetMapping(value="/map")
	public String mapView() throws Exception {
		
		return "map/map";
	}// addPurchaseView
}
