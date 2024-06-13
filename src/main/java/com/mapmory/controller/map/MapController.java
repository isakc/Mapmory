package com.mapmory.controller.map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MapController {
	///// Field /////
	@Autowired
	
	///// Constructor /////
	
	///// Method /////
	@GetMapping(value="/map")
	public String mapView() throws Exception {
		
		return "map/map";
	}// addPurchaseView
}
