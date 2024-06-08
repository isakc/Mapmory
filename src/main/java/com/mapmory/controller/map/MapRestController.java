package com.mapmory.controller.map;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mapmory.services.map.domain.ResultRouter;
import com.mapmory.services.map.domain.ResultTransitRouter;
import com.mapmory.services.map.domain.SearchRouter;
import com.mapmory.services.map.domain.SearchTransitRouter;
import com.mapmory.services.map.service.MapService;

@RestController
@RequestMapping("/map/*")
public class MapRestController {
///// Field /////
	@Autowired
	@Qualifier("mapServiceImpl")
	private MapService mapService;
	
	///// Constructor /////
	
	///// Method /////
	
	@ResponseBody
	@PostMapping(value="rest/getPedestrianRoute")
	public ResultRouter getSearchRoute(@RequestBody SearchRouter searchRouter ) throws Exception  {
		ResultRouter resultRouter = mapService.getPedestrianRoute(searchRouter);
		
		return resultRouter;
	}// getSearchRoute: 경로 요청
	
	@ResponseBody
	@PostMapping(value="rest/getCarRoute")
	public ResultRouter getCarRoute(@RequestBody SearchRouter searchRouter ) throws Exception  {
		ResultRouter resultRouter = mapService.getCarRoute(searchRouter);
		
		return resultRouter;
	}// getSearchRoute: 경로 요청
	
	@ResponseBody
	@PostMapping(value="rest/getTransitRouteList")
	public List<ResultTransitRouter> getTransitRouteList(@RequestBody SearchTransitRouter searchTransitRouter ) throws Exception  {
		List<ResultTransitRouter> resultTransitRouterList = mapService.getTransitRoute(searchTransitRouter);
		
		return resultTransitRouterList;
	}// getSearchRoute: 경로 요청
}
