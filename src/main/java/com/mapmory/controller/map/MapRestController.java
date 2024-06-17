package com.mapmory.controller.map;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import com.mapmory.services.timeline.domain.MapRecord;
import com.mapmory.services.timeline.domain.Record;
import com.mapmory.services.timeline.service.TimelineService;
import com.mapmory.services.user.domain.FollowMap;
import com.mapmory.services.user.service.UserService;

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
	
	@Autowired
	@Qualifier("userServiceImpl")
	private UserService userService;
	
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
	@PostMapping(value="rest/getMapRecordList")
	public List<MapRecord> getMapRecordList(@RequestBody Search search) throws Exception {
		
		search.setLimit(10);
		search.setCurrentPage(1);
		
		List<MapRecord> mapRecordList = timelineService.getMapRecordList(search);
		List<FollowMap> followList = userService.getFollowList(null, search.getUserId(), null, 0, 0, 0);
		List<String> followUserId = new ArrayList<String>();
		
		for(FollowMap follow : followList) {
			followUserId.add(follow.getUserId());
		}
		
		for(MapRecord record : mapRecordList) {
			if(record.getRecordUserId().equals(search.getUserId())) { // 기록의 작성자가 사용자의 ID인 경우 private
				record.setRecordType(0);
			}else if(followUserId.contains(record.getRecordUserId())) {// 사용자의 ID의 팔로우 리스트 중 포함되어 있으면 follow
				record.setRecordType(2);
			}else {// 나머지는 공유 기록 public
				record.setRecordType(1);
			}
		}
		
		//1 private // 2 follow // 3 public
		
		return mapRecordList;
	}

	@ResponseBody
	@PostMapping(value="rest/getDetailRecord")
	public Record getDetailRecord(@RequestBody Record record) throws Exception {
		return timelineService.getDetailTimeline(record.getRecordNo());
	}
}
