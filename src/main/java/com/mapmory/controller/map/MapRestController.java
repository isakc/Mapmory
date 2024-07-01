package com.mapmory.controller.map;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mapmory.common.domain.Search;
import com.mapmory.common.util.ObjectStorageUtil;
import com.mapmory.services.map.domain.ResultRouter;
import com.mapmory.services.map.domain.ResultTransitRouter;
import com.mapmory.services.map.domain.SearchRouter;
import com.mapmory.services.map.domain.SearchTransitRouter;
import com.mapmory.services.map.service.MapService;
import com.mapmory.services.purchase.domain.Subscription;
import com.mapmory.services.purchase.service.SubscriptionService;
import com.mapmory.services.timeline.domain.ImageTag;
import com.mapmory.services.timeline.domain.MapRecord;
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
	
	@Autowired
	@Qualifier("subscriptionServiceImpl")
	private SubscriptionService subscriptionService;
	
	@Autowired
	@Qualifier("objectStorageUtil")
	private ObjectStorageUtil objectStorageUtil;
	
	@Value("${object.map.folder.name}")
	private String mapFolderName;
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
		
		search.setLimit(30);
		search.setCurrentPage(1);
		
		List<MapRecord> mapRecordList = timelineService.getMapRecordList(search);
		List<FollowMap> followList = userService.getFollowList(null, search.getUserId(), null, 0, 0, 0);
		List<String> followUserId = new ArrayList<String>();
		
		for(FollowMap follow : followList) {
			followUserId.add(follow.getUserId());
		}
		
		for(MapRecord record : mapRecordList) {
			if(record.getRecordUserId().equals(search.getUserId())) { // 기록의 작성자가 사용자의 ID인 경우 private
				record.setMarkerType(0);
				record.setMarkerTypeString("나의 기록");
			}else if(search.getFollowType() == 1 && followUserId.contains(record.getRecordUserId()) ) {// 사용자의 ID의 팔로우 리스트 중 포함되어 있으면 follow
				record.setMarkerType(2);
				record.setMarkerTypeString("팔로우");
			}else{// 나머지는 공유 기록 public
				record.setMarkerType(1);
				record.setMarkerTypeString("공유");
			}
			//1 private // 2 follow // 3 public
			
			Subscription checkSub = subscriptionService.getDetailSubscription(record.getRecordUserId());
			
			if(checkSub != null && checkSub.isSubscribed() ) {
				record.setSubscribed(true);
			}
			
			record.setRecordAddDate(LocalDateTime.parse(record.getRecordAddDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		}
		
		return mapRecordList;
	}// getMapRecordList: map에서 들고오기
	
//	@ResponseBody
//	@PostMapping(value="rest/getMapRecordList")
//	public ResponseEntity<Map<String, Object>> getMapRecordList(@RequestBody Search search) throws Exception {
//		
//		search.setLimit(10);
//		search.setCurrentPage(1);
//		
//		List<MapRecord> mapRecordList = timelineService.getMapRecordList(search);
//		List<FollowMap> followList = userService.getFollowList(null, search.getUserId(), null, 0, 0, 0);
//		List<String> followUserId = new ArrayList<String>();
//		
//		for(FollowMap follow : followList) {
//			followUserId.add(follow.getUserId());
//		}
//		
//		for(MapRecord record : mapRecordList) {
//			if(record.getRecordUserId().equals(search.getUserId())) { // 기록의 작성자가 사용자의 ID인 경우 private
//				record.setMarkerType(0);
//				record.setMarkerTypeString("나의 기록");
//			}else if(search.getFollowType() == 1 && followUserId.contains(record.getRecordUserId()) ) {// 사용자의 ID의 팔로우 리스트 중 포함되어 있으면 follow
//				record.setMarkerType(2);
//				record.setMarkerTypeString("팔로우");
//			}else{// 나머지는 공유 기록 public
//				record.setMarkerType(1);
//				record.setMarkerTypeString("공유");
//			}
//			//1 private // 2 follow // 3 public
//			
//			Subscription checkSub = subscriptionService.getDetailSubscription(record.getRecordUserId());
//			
//			if(checkSub != null && checkSub.isSubscribed() ) {
//				record.setSubscribed(true);
//			}
//			
//			record.setRecordAddDate(LocalDateTime.parse(record.getRecordAddDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
//                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
//			
//			String profileImage = userService.getImage("profile", record.getProfileImageName());
//			record.setProfileImageName(profileImage);
//			
//			for(ImageTag imageName : record.getImageName()) {
//				String imageFileName = userService.getImage("thumbnail", imageName.getImageTagText());
//				imageName.setImageTagText(imageFileName);
//			}
//			
//			if(record.getCategoryImoji() != null) {
//				String categoryImage = userService.getImage("emoji", record.getCategoryImoji());
//				record.setCategoryImoji(categoryImage);
//			}
//		}
//		
//		String badgeImage = userService.getImage("profile", "sub.png");
//		
//		Map<String, Object> map = new HashMap<>();
//		map.put("mapRecordList", mapRecordList);
//		map.put("badgeImage", badgeImage);
//		
//		return ResponseEntity.ok(map);
//	}// getMapRecordList: map에서 들고오기
}
