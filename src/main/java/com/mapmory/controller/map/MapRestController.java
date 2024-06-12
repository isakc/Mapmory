package com.mapmory.controller.map;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mapmory.common.domain.Search;
import com.mapmory.services.map.domain.ResultRouter;
import com.mapmory.services.map.domain.ResultTransitRouter;
import com.mapmory.services.map.domain.SearchRouter;
import com.mapmory.services.map.domain.SearchTransitRouter;
import com.mapmory.services.map.service.MapService;
import com.mapmory.services.timeline.domain.Record;
import com.mapmory.services.timeline.service.TimelineService;

@RestController
@RequestMapping("/map/*")
public class MapRestController {
///// Field /////
	@Autowired
	@Qualifier("mapServiceImpl")
	private MapService mapService;
	
	@Autowired
	@Qualifier("timelineService")
	private TimelineService timelineService;
	
	///// Constructor /////
	
	///// Method /////
	
	@ResponseBody
	@PostMapping(value="rest/getPedestrianRoute")
	public ResultRouter getSearchRoute(@RequestBody SearchRouter searchRouter ) throws Exception  {
		ResultRouter resultRouter = mapService.getPedestrianRoute(searchRouter);
		
		return resultRouter;
	}// getSearchRoute: 도보 경로 요청
	
	@ResponseBody
	@PostMapping(value="rest/getCarRoute")
	public ResultRouter getCarRoute(@RequestBody SearchRouter searchRouter ) throws Exception  {
		ResultRouter resultRouter = mapService.getCarRoute(searchRouter);
		
		return resultRouter;
	}// getCarRoute: 자전거 경로 요청
	
	@ResponseBody
	@PostMapping(value="rest/getTransitRouteList")
	public List<ResultTransitRouter> getTransitRouteList(@RequestBody SearchTransitRouter searchTransitRouter ) throws Exception  {
		List<ResultTransitRouter> resultTransitRouterList = mapService.getTransitRoute(searchTransitRouter);
		
		return resultTransitRouterList;
	}// getTransitRouteList: 대중교통 경로 요청
	
	@ResponseBody
	@PostMapping(value="rest/test")
	public List<Record> test(@RequestBody Search search) throws Exception {
		
		search.setLimit(10);
		search.setUserId("user1");
		search.setFollowType(0);
		search.setCurrentPage(1);
		
		return timelineService.getMapRecordList(search);
	}
}
