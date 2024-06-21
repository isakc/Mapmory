package com.mapmory.controller.timeline;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mapmory.common.domain.Search;
import com.mapmory.common.domain.SessionData;
import com.mapmory.common.util.ContentFilterUtil;
import com.mapmory.common.util.RedisUtil;
import com.mapmory.common.util.TextToImage;
import com.mapmory.common.util.TimelineUtil;
import com.mapmory.services.community.service.CommunityService;
import com.mapmory.services.timeline.domain.Category;
import com.mapmory.services.timeline.domain.Record;
import com.mapmory.services.timeline.service.TimelineService;


@Controller("timelineController")
@RequestMapping("/timeline/*")
public class TimelineController {
	@Autowired
	@Qualifier("timelineService")
	private TimelineService timelineService;
	
	@Autowired
	@Qualifier("communityServiceImpl")
	private CommunityService communityService;
	
	@Autowired
	private TimelineUtil timelineUtil;
	
	@Autowired
	private TextToImage textToImage;
	
    @Autowired
    private RedisUtil<SessionData> redisUtil;
	
	@Value("${timeline.kakaomap.apiKey}")
	private String kakaoMapApiKey;
	
	@Value("${timeline.tmap.apiKey}")
	private String tMapApiKey;
	
	@Value("${default.time}")
	private String defaultTime;
	
	@Value("${object.timeline.image}")
	private String imageFileFolder;
	
	@Value("${object.timeline.media}")
	private String mediaFileFolder;
    
    @Value("${cdn.url}")
    private String cdnUrl;
    
    @Value("${timeline.kakaomap.restKey}")
    private String restKey;
    
    private int searchOption=2;
	
	public TimelineController(){
		System.out.println("TimelineController default Contrctor call : " + this.getClass());
	}

	
	@GetMapping({"/*"})
	public void getTimelineKey(Model model) {
	}
	
	@GetMapping("getTimelineList")
	public String getTimelineList(Model model,
			@RequestParam(value="userId", required = true) String userId,
			@RequestParam(value="selectDay", required = false) Date selectDay,
			@RequestParam(value="plus", required = false) Integer plus,
			HttpServletRequest request
			) throws Exception,IOException{
		
		if(selectDay==null) {
		LocalDate today = LocalDate.now();
//		LocalDate today = LocalDate.of(2024,5,29);
		selectDay=Date.valueOf(today);
		}
		if( !(plus==null) ) {
			selectDay=(Date.valueOf(selectDay.toLocalDate().plusDays(plus)));
		}
		LocalDate tomorrow = selectDay.toLocalDate();
		
		Search search = Search.builder()
				.userId(userId)
				.selectDay1(selectDay+" "+defaultTime)
				.selectDay2((Date.valueOf(tomorrow.plusDays(1)))+" "+defaultTime)
				.timecapsuleType(0)
				.build();
		
		List<Record> timelineList=timelineService.getTimelineList(search);
		if(!(timelineList==null || timelineList.isEmpty())) {
		List<Map<String,Object>> mapList=new ArrayList<Map<String,Object>>();
		for(Record r: timelineList) {
			Map<String,Object> map=new HashMap<String, Object>();
			map.put("title", r.getCheckpointAddress()+"<br/>"
			+r.getCheckpointDate().toString().substring(11));
			map.put("latitude", r.getLatitude());
			map.put("longitude", r.getLongitude());
			mapList.add(map);
		}
		ObjectMapper objectMapper = new ObjectMapper();
        String jsonPostions = objectMapper.writeValueAsString(mapList);

		Map<String,Object> map=new HashMap<String, Object>();
		mapList=new ArrayList<Map<String,Object>>();
		map.put("startName", "출발지");
		map.put("startX", timelineList.get(0).getLongitude().toString());
		map.put("startY", timelineList.get(0).getLatitude().toString());
		map.put("startTime", timelineList.get(0).getCheckpointDate()
				.replace("-", "")
				.replace(" ", "")
				.replace(":", "")
				.substring(0, 12));
		map.put("endName", "도착지");
		map.put("endX", timelineList.get(timelineList.size()-1).getLongitude().toString());
		map.put("endY", timelineList.get(timelineList.size()-1).getLatitude().toString());
		map.put("reqCoordType", "WGS84GEO");
//		map.put("resCoordType", "EPSG3857");
		map.put("resCoordType", "WGS84GEO");
		map.put("searchOption", searchOption);
        for(int i=1;i<(timelineList.size()-1);i++) {
    		Map<String,Object> map3=new HashMap<String, Object>();
        	map3.put("viaPointId", timelineList.get(i).getRecordUserId()+"_"+i);
        	map3.put("viaPointName", timelineList.get(i).getCheckpointDate());
        	map3.put("viaX", timelineList.get(i).getLongitude().toString());
        	map3.put("viaY", timelineList.get(i).getLatitude().toString());
        	mapList.add(map3);
        }
        map.put("viaPoints", mapList);
        String jsonParam=objectMapper.writeValueAsString(map);
        
		model.addAttribute("positions",jsonPostions);
		model.addAttribute("positionParam",jsonParam);
		}
		model.addAttribute("apiKey", kakaoMapApiKey);
		model.addAttribute("tMapApiKey",tMapApiKey);
		model.addAttribute("restKey",restKey);
		model.addAttribute("userId",redisUtil.getSession(request).getUserId());
		model.addAttribute("timelineList", timelineList);
		model.addAttribute("selectDay",selectDay);
		return "timeline/getTimelineList";
	}
	
	@GetMapping("getSummaryRecord")
	public String getSummaryRecord(Model model,
			@RequestParam(value="userId", required = true) String userId,
			@RequestParam(value="selectDate", required = false) Date selectDate) throws Exception,IOException{
		if(selectDate==null) {
//		LocalDate today = LocalDate.now();
//		selectDate = Date.valueOf(today);
		selectDate = Date.valueOf("2024-05-29");
		}
		Search search = Search.builder()
				.userId(userId)
				.selectDate(selectDate)
				.build();
		model.addAttribute("record", timelineService.getSummaryRecord(search));
		return "timeline/getSummaryRecord";
	}
	
	@GetMapping("getSimpleTimeline")
	public String getSimpleTimeline(Model model,
			@RequestParam(value="recordNo", required = true) int recordNo,
			HttpServletRequest request) throws Exception,IOException {
		Record record=timelineService.getDetailTimeline(recordNo);
		record=timelineUtil.imojiNameToUrl(record);
		System.out.println("record.getCheckpointDate().toString().substring(0, 10) :"+record.getCheckpointDate().toString().substring(0, 10));
		model.addAttribute("apiKey", kakaoMapApiKey);
		model.addAttribute("restKey",restKey);
		model.addAttribute("userId",redisUtil.getSession(request).getUserId());
		model.addAttribute("record",record);
		model.addAttribute("selectDay",record.getCheckpointDate().toString().substring(0, 10));
		return "timeline/getSimpleTimeline";
	}
	
	@GetMapping("getDetailTimeline")
	public String getDetailTimeline(Model model,
			@RequestParam(value="recordNo", required = true) int recordNo,
			HttpServletRequest request
			) throws Exception,IOException {
		Record record=timelineUtil.imageNameToUrl(timelineService.getDetailTimeline(recordNo));
		record=timelineUtil.imojiNameToUrl(record);
		record=timelineUtil.mediaNameToUrl(record);
		record.setRecordText(textToImage.processImageTags(record.getRecordText()));
		
		model.addAttribute("apiKey", kakaoMapApiKey);
		model.addAttribute("restKey",restKey);
		model.addAttribute("userId",redisUtil.getSession(request).getUserId());
		model.addAttribute("updateCountText", TimelineUtil.updateCountToText(record.getUpdateCount()));
		model.addAttribute("record",record);
		return "timeline/getDetailTimeline";
	}
	
	@GetMapping("updateTimeline")
	public String updateTimelineView(Model model,
			@RequestParam(value="recordNo", required = true) int recordNo) throws Exception,IOException {
		Record record=timelineUtil.imageNameToUrl(timelineService.getDetailTimeline(recordNo));
		record=timelineUtil.mediaNameToUrl(record);
		model.addAttribute("hashtagText",TimelineUtil.hashtagListToText(record.getHashtag()));
		model.addAttribute("category", timelineService.getCategoryList());
		model.addAttribute("apiKey", kakaoMapApiKey);
		model.addAttribute("updateCountText", TimelineUtil.updateCountToText(record.getUpdateCount()));
		model.addAttribute("record",record);
		return "timeline/updateTimeline";
	}
	
	@PostMapping("updateTimeline")
	public String updateTimeline(Model model,
			@ModelAttribute("record") Record record,
			@RequestParam(name="hashtagText",required = false) String hashtagText,
			@RequestParam(name="sharedDateType",required = false) Integer sharedDateType,
			@RequestParam(name="mediaFile",required = false) MultipartFile mediaFile,
			@RequestParam(name="imageFile",required = false) List<MultipartFile> imageFile,
			HttpServletRequest request
			) throws Exception,IOException {
		
		record.setMediaName( timelineUtil.mediaUrlToName(record.getMediaName()) );
		record = timelineUtil.uploadImageFile(record, imageFile);
		record = timelineUtil.uploadMediaFile(record, mediaFile);
		
		record.setHashtag(TimelineUtil.hashtagTextToList(hashtagText, record.getRecordNo()));
		
		record.setUpdateCount(record.getUpdateCount()+1);
		
		if(record.getMediaName()!=null || !record.getMediaName().isEmpty() 
				|| record.getImageName()!=null || !record.getImageName().isEmpty()
				|| record.getRecordText()!=null || !record.getRecordText().trim().equals("") ) {
			if(record.getRecordAddDate()==null || record.getRecordAddDate().trim().equals("")) {
				record.setRecordAddDate(LocalDateTime.now().toString().replace("T", " ").split("\\.")[0]);
			}
			record.setTempType(1);
		}else {
			record.setTempType(0);
		}
		
		if(sharedDateType!=null&&sharedDateType==1) {
			if(ContentFilterUtil.checkBadWord(record.getRecordText()) 
					|| ContentFilterUtil.checkBadWord(record.getRecordTitle())
					|| ContentFilterUtil.checkBadWord(hashtagText) ) {
				record.setSharedDate(null);
			}else {
				record.setSharedDate(LocalDateTime.now().toString().replace("T", " ").split("\\.")[0]);
			}
		}else {
			record.setSharedDate(null);
		}
		
		record=TimelineUtil.validateRecord(record);
		
//		System.out.println("record.getImageName() : "+record.getImageName());
//		System.out.println("record.getHashtag() : "+record.getHashtag());
		timelineService.updateTimeline(record);
		return getDetailTimeline(model,record.getRecordNo(),request);
	}

	@GetMapping("deleteTimeline")
	public String deleteTimeline(Model model,
			@RequestParam(value="recordNo", required = true) int recordNo,
			@RequestParam(value="userId", required = true) String userId,
			HttpServletRequest request
			) throws Exception,IOException {
		timelineService.deleteTimeline(recordNo);
		return getTimelineList(model, userId, null, null,request);
	}
	
	@GetMapping("getTimecapsuleList")
	public String getTimecapsuleList(Model model,
			@RequestParam(value="userId", required = true) String userId,
			HttpServletRequest request
			) throws Exception,IOException{
		Search search = Search.builder()
				.userId(userId)
				.tempType(1)
				.timecapsuleType(1)
				.build();
		
		model.addAttribute("apiKey", kakaoMapApiKey);
		model.addAttribute("restKey",restKey);
		model.addAttribute("userId",redisUtil.getSession(request).getUserId());
		model.addAttribute("timecapsuleList", timelineService.getTimelineList(search));
		return "timeline/getTimecapsuleList";
	}
	
	@GetMapping("getTempTimecapsuleList")
	public String getTempTimecapsuleList(Model model,
			@RequestParam(value="userId", required = true) String userId,
			HttpServletRequest request
			) throws Exception,IOException{
		Search search = Search.builder()
				.userId(userId)
				.tempType(0)
				.timecapsuleType(1)
				.build();
		
		model.addAttribute("apiKey", kakaoMapApiKey);
		model.addAttribute("restKey",restKey);
		model.addAttribute("userId",redisUtil.getSession(request).getUserId());
		model.addAttribute("timecapsuleList", timelineService.getTimelineList(search));
		return "timeline/getTempTimecapsuleList";
	}
	
	@GetMapping("getDetailTimecapsule")
	public String getDetailTimecapsule(Model model,
			@RequestParam(value="recordNo", required = true) int recordNo,
			HttpServletRequest request
			) throws Exception,IOException {
		Record record=timelineUtil.imageNameToUrl(timelineService.getDetailTimeline(recordNo));
		record=timelineUtil.imojiNameToUrl(record);
		record=timelineUtil.mediaNameToUrl(record);
		record.setRecordText(textToImage.processImageTags(record.getRecordText()));
		
		model.addAttribute("apiKey", kakaoMapApiKey);
		model.addAttribute("restKey",restKey);
		model.addAttribute("userId",redisUtil.getSession(request).getUserId());
		model.addAttribute("record",record);
		return "timeline/getDetailTimecapsule";
	}
	
	@GetMapping("addTimecapsule")
	public String addTimecapsuleView(Model model,
			@RequestParam(value="userId", required = false) String userId) throws Exception,IOException {
		model.addAttribute("category", timelineService.getCategoryList());
		model.addAttribute("userId",userId);
		return "timeline/addTimecapsule";
	}
	
	@PostMapping("addTimecapsule")
	public String addTimecapsule(Model model,@ModelAttribute(value="record") Record record) throws Exception,IOException {
		System.out.println("record.getImageName() : "+record.getImageName());
		System.out.println("record.getHashtag() : "+record.getHashtag());
		timelineService.updateTimeline(record);
		model.addAttribute("record",timelineService.getDetailTimeline(record.getRecordNo()));
		return "timeline/getDetailTimeline";
	}
	
	@GetMapping("updateTimecapsule")
	public String updateTimecapsuleView(Model model,
			@RequestParam(value="recordNo", required = false) Integer recordNo) throws Exception,IOException {
		Record record=timelineUtil.imageNameToUrl(timelineService.getDetailTimeline(recordNo));
		record=timelineUtil.mediaNameToUrl(record);
		model.addAttribute("hashtagText",TimelineUtil.hashtagListToText(record.getHashtag()));
		model.addAttribute("category", timelineService.getCategoryList());
		model.addAttribute("apiKey", kakaoMapApiKey);
		model.addAttribute("record",record);
		return "timeline/updateTimecapsule";
	}
	
	@PostMapping("updateTimecapsule")
	public String updateTimecapsule(Model model,
			@ModelAttribute("record") Record record,
			@RequestParam(name="hashtagText",required = false) String hashtagText,
			@RequestParam(name="mediaFile",required = false) MultipartFile mediaFile,
			@RequestParam(name="imageFile",required = false) List<MultipartFile> imageFile,
			HttpServletRequest request
			) throws Exception,IOException {
		record.setMediaName( timelineUtil.mediaUrlToName(record.getMediaName()) );
		record = timelineUtil.uploadImageFile(record, imageFile);
		record = timelineUtil.uploadMediaFile(record, mediaFile);
		
		record.setHashtag(TimelineUtil.hashtagTextToList(hashtagText, record.getRecordNo()));
		
		if(record.getTempType()==1 
				|| record.getMediaName()!=null || !record.getMediaName().isEmpty() 
				|| record.getImageName()!=null || !record.getImageName().isEmpty()
				|| record.getRecordText()!=null || !record.getRecordText().trim().equals("") ) {
			if(record.getRecordAddDate()==null || record.getRecordAddDate().trim().equals("")) {
				record.setRecordAddDate(LocalDateTime.now().toString().replace("T", " ").split("\\.")[0]);
			}
		}
		
		record=TimelineUtil.validateRecord(record);
		
		timelineService.updateTimeline(record);
		if(record.getTempType()==1) {
			return getDetailTimecapsule(model,record.getRecordNo(),request);
		}else {
			return getTempTimecapsuleList(model, record.getRecordUserId(),request);
		}
	}

	@GetMapping("deleteTimecapsule")
	public String deleteTimecapsule(Model model,
			@RequestParam(value="recordNo", required = true) int recordNo,
			@RequestParam(value="userId", required = true) String userId,
			HttpServletRequest request
			) throws Exception,IOException {
		timelineService.deleteTimeline(recordNo);
		return getTimecapsuleList(model, userId, request);
	}
	
	@GetMapping("addVoiceToText")
	public String addVoiceToText() throws Exception,IOException {
		return "timeline/addVoiceToText";
	}
	
	@GetMapping("getUserCategoryList")
	public String getUserCategoryList(Model model) throws Exception,IOException{
		model.addAttribute("categoryList", 
				timelineUtil.categoryImojiListToUrl(
						timelineService.getCategoryList()
						)
				);
		return "timeline/getUserCategoryList";
	}
	
	@GetMapping("getAdminCategoryList")
	public String getAdminCategoryList(Model model) throws Exception,IOException{
		List<Category> categoryList = timelineUtil.categoryImojiListToUrl(timelineService.getCategoryList());
		System.out.println(categoryList);
		model.addAttribute("categoryList", categoryList);
		return "timeline/admin/getAdminCategoryList";
	}
	
	@GetMapping("addCategory")
	public String addCategoryView() throws Exception,IOException {
		return "timeline/admin/addCategory";
	}
	
	@GetMapping("updateCategory")
	public String updateCategoryView(Model model,@RequestParam(name="categoryNo",required = true) int categoryNo) throws Exception,IOException {
		model.addAttribute("category",timelineUtil.categoryImojiNameToUrl(timelineService.getCategory(categoryNo)));
		return "timeline/admin/updateCategory";
	}
	
	@GetMapping("footer")
	public String footer() throws Exception,IOException {
		return "common/footer";
	}
	
	
	@GetMapping({"getDetailTimeline3"})
	public void getDetailTimeline3(Model model, String userId) throws Exception,IOException {
		model.addAttribute("record",timelineService.getDetailTimeline(1));
		model.addAttribute("record2",timelineService.getDetailSharedRecord(1, userId));
	}
	
	@GetMapping({"getDetailTimeline2"})
	public void getTimelineList2(Model model) throws Exception,IOException {
		Search search;
//		search=Search.builder()
//				.currentPage(1)
//				.limit(3)
//				.build();
//		model.addAttribute("list",timelineService.getTimelineList(search));
//		search=Search.builder()
//				.currentPage(1)
//				.limit(3)
//				.sharedType(1)
//				.tempType(1)
//				.timecapsuleType(0)
//				.build();
//		model.addAttribute("list2",timelineService.getTimelineList(search));
//		search=Search.builder()
//				.currentPage(1)
//				.limit(3)
//				.sharedType(0)
//				.tempType(0)
//				.timecapsuleType(0)
//				.build();
//		model.addAttribute("list3",timelineService.getTimelineList(search));
//		search=Search.builder()
//				.currentPage(3)
//				.limit(3)
//				.sharedType(0)
//				.tempType(1)
//				.timecapsuleType(0)
//				.build();
//		model.addAttribute("list4",timelineService.getTimelineList(search));
//		search=Search.builder()
//				.currentPage(3)
//				.limit(3)
//				.sharedType(1)
//				.tempType(1)
//				.timecapsuleType(0)
//				.build();
//		model.addAttribute("list5",timelineService.getTimelineList(search));
//		search=Search.builder()
//				.currentPage(1)
//				.limit(5)
//				.sharedType(0)
//				.tempType(0)
//				.timecapsuleType(1)
//				.build();
//		model.addAttribute("list6",timelineService.getTimelineList(search));
//		//d_day보다 현재 날짜가 위에 있으면 갖고오는 조건식
//		search=Search.builder()
//				.currentPage(1)
//				.limit(3)
//				.sharedType(0)
//				.tempType(1)
//				.timecapsuleType(1)
//				.build();
//		model.addAttribute("list7",timelineService.getTimelineList(search));
		//대민 지원
		search=Search.builder()
				.userId("user1")
				.currentPage(1)
				.limit(5)
				.logsType(0)
				.build();
		model.addAttribute("list8",timelineService.getProfileTimelineList(search));
		search=Search.builder()
				.userId("user1")
				.currentPage(1)
				.limit(5)
				.logsType(1)
				.build();
		model.addAttribute("list8_1",timelineService.getProfileTimelineList(search));
		search=Search.builder()
				.userId("user1")
				.currentPage(1)
				.limit(5)
				.logsType(2)
				.build();
		model.addAttribute("list8_2",timelineService.getProfileTimelineList(search));
//		//재용 지원
//		search=Search.builder()
//				.currentPage(1)
//				.limit(5)
//				.build();
//		model.addAttribute("list9",timelineService.getSharedRecordList(search));
//		search=Search.builder()
//				.currentPage(2)
//				.limit(5)
//				.build();
//		model.addAttribute("list10",timelineService.getSharedRecordList(search));
//		search=Search.builder()
//				.timecapsuleType(0)
//				.selectDay1("2024-06-04 00:00:00")
//				.selectDay2("2024-06-05 00:00:00")
//				.build();
//		model.addAttribute("list11",timelineService.getTimelineList(search));
//		
		//성문 지원===========================================
//		search=Search.builder()
//	 			.latitude(33.450701)
//	 			.longitude(126.570667)
//	 			.radius(110)
//	 			.limit(10)
//	 			.userId("user1")
//	 			.followType(1)
//				.build();
//		model.addAttribute("mapList1",timelineService.getMapRecordList(search));
//		search=Search.builder()
//				.latitude(33.450701)
//	 			.longitude(126.570667)
//	 			.radius(110)
//	 			.limit(10)
//	 			.userId("user1")
//	 			.sharedType(1)
//	 			.searchKeyword("22")
//				.build();
//		model.addAttribute("mapList2",timelineService.getMapRecordList(search));
//		search=Search.builder()
//				.latitude(37.56789)
//	 			.longitude(127.345678)
//	 			.radius(1100)
//	 			.limit(10)
//	 			.userId("user1")
//	 			.privateType(1)
//				.build();
//		model.addAttribute("mapList3",timelineService.getMapRecordList(search));
//		search=Search.builder()
//				.latitude(37.56789)
//	 			.longitude(127.345678)
//	 			.radius(1100)
//	 			.limit(10)
//	 			.userId("user1")
//	 			.privateType(1)
//	 			.searchKeyword("나가")
//				.build();
//		model.addAttribute("mapList3_2",timelineService.getMapRecordList(search));
//		search=Search.builder()
//				.latitude(37.56789)
//	 			.longitude(127.345678)
//	 			.radius(1100)
//	 			.limit(10)
//	 			.userId("user1")
//	 			.privateType(1)
//	 			.searchKeyword("나가")
//	 			.categoryNo(2)
//				.build();
//		model.addAttribute("mapList3_3",timelineService.getMapRecordList(search));
//		search=Search.builder()
//				.latitude(37.56789)
//	 			.longitude(127.345678)
//	 			.radius(1100)
//	 			.limit(10)
//	 			.userId("user1")
//	 			.privateType(1)
//	 			.categoryNo(3)
//				.build();
//		model.addAttribute("mapList3_4",timelineService.getMapRecordList(search));
//		search=Search.builder()
//				.latitude(37.4794143)
//	 			.longitude(127.020817)
//	 			.privateType(1)
//	 			.followType(1)
//	 			.sharedType(1)
//	 			.userId("user1")
//	 			.radius(1100)
//	 			.limit(100)
//				.build();
//		model.addAttribute("mapList4",timelineService.getMapRecordList(search));
//		//===========================================
//		search=Search.builder()
//				.userId("user1")
//				.selectDate(Date.valueOf("2024-05-29"))
//				.build();
//		model.addAttribute("list16",timelineService.getSummaryRecord(search));
	}
	
	public void updateTimeline(Model model) throws Exception,IOException{
//		model.addAttribute("record",timelineService.getDetailTimeline(1));
	}
	
	
	//아래 미사용
//	@GetMapping({"getDetailTimeline2"})
//	public void getDetailTimeline2(Model model) throws Exception,IOException {
//		model.addAttribute("record",timelineService.getDetailTimeline2(1));
//	}
}
