package com.mapmory.services.timeline.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mapmory.common.domain.Search;
import com.mapmory.common.util.GeoUtil;
import com.mapmory.common.util.TimelineUtil;
import com.mapmory.services.timeline.dao.TimelineDao;
import com.mapmory.services.timeline.domain.Category;
import com.mapmory.services.timeline.domain.ImageTag;
import com.mapmory.services.timeline.domain.KeywordData;
import com.mapmory.services.timeline.domain.MapRecord;
import com.mapmory.services.timeline.domain.Record;
import com.mapmory.services.timeline.domain.SharedRecord;
import com.mapmory.services.timeline.dto.CountAddressDto;
import com.mapmory.services.timeline.dto.NotifyTimecapsuleDto;
import com.mapmory.services.timeline.dto.SearchDto;
import com.mapmory.services.timeline.dto.SharedRecordDto;
import com.mapmory.services.timeline.dto.SummaryRecordDto;
import com.mapmory.services.timeline.service.TimelineService;


@Service("timelineService")
@Transactional
public class TimelineServiceImpl implements TimelineService {
	
	@Autowired
	@Qualifier("timelineDao")
	private TimelineDao timelineDao;
	
	@Value("${summary.record.time}")
	private String checkpointTime;
	
	private DateTimeFormatter DATE_TIME_FORMATTER=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	//Record CRUD
	public int addTimeline(Record record) throws Exception{
		Map<String, Object> map=new HashMap<String, Object>();
		timelineDao.insertTimeline(record);
		System.out.println(record);
		if((record.getImageName()!=null && !record.getImageName().isEmpty())
				||(record.getHashtag() != null && !record.getHashtag().isEmpty())) {
			map.put("recordNo",record.getRecordNo());
			map.put("imageTagList",TimelineUtil.imageTagToList(record.getImageName(),record.getHashtag()));
			timelineDao.insertImageTag(map);
		}
		return record.getRecordNo();
	}
	
	@Override
	public Record getDetailTimeline(int recordNo) throws Exception{
		return timelineDao.selectDetailTimeline(recordNo);
	}
	
	@Override
	public List<Record> getTimelineList(Search search) throws Exception{
//		List<Record> recordList=new ArrayList<Record>();
//		for(Map<String,Object> map:timelineDao.selectTimelineList(search)) {
//			recordList.add(TimelineUtil.mapToRecord(map));
//		}
		return timelineDao.selectTimelineList(search);
	}

	@Override
	public void updateTimeline(Record record) throws Exception {
		Map<String, Object> map=new HashMap<String, Object>();
		timelineDao.updateTimeline(record);
		timelineDao.deleteImageTag(ImageTag
				.builder()
				.recordNo(record.getRecordNo())
				.imageTagType(0)
				.build());
		if((record.getImageName()!=null && !record.getImageName().isEmpty())
				||(record.getHashtag() != null && !record.getHashtag().isEmpty())) {
			map.put("recordNo",record.getRecordNo());
			map.put("imageTagList",TimelineUtil.imageTagToList(record.getImageName(),record.getHashtag()));
			timelineDao.insertImageTag(map);
		}
	}

	@Override
	public void deleteTimeline(int recordNo) throws Exception {
		timelineDao.deleteTimeline(recordNo);
	}
	
	//image만 삭제
	@Override
	public int deleteImage(int imageNo) throws Exception {
		return timelineDao.deleteImageToImageNo(imageNo);
	}
	
	@Override
	public int deleteMedia(int recordNo) throws Exception {
		return timelineDao.updateMedia(recordNo);
	}

	//Category CRUD
	@Override
	public int addCategory(Category category) throws Exception {
		return timelineDao.insertCategory(category);
	}

	@Override
	public Category getCategory(int categoryNo) throws Exception {
		return timelineDao.selectCategory(categoryNo);
	}

	@Override
	public List<Category> getCategoryList() throws Exception {
		List<Category> categoryList=new ArrayList<Category>();
		for(Category category:timelineDao.selectCategoryList()) {
			categoryList.add(category);
		}
		return categoryList;
	}

	@Override
	public int updateCategory(Category category) throws Exception {
		return timelineDao.updateCategory(category);
	}

	@Override
	public int deleteCategory(int categoryNo) throws Exception {
		return timelineDao.deleteCategory(categoryNo);
	}
	
	@Override
	public CountAddressDto getCountAddress(Record record) throws Exception{
		return timelineDao.selectCountAddress(record);
	}
	
	@Override
	public List<SummaryRecordDto> getSummaryRecord(Search search) throws Exception{
		SearchDto searchDto=SearchDto.builder()
			.selectDate(search.getSelectDate())
			.checkpointTime(checkpointTime)
			.userId(search.getUserId())
			.build();
		List<SummaryRecordDto> recordList=new ArrayList<SummaryRecordDto>();
		List<SummaryRecordDto> mediaList=timelineDao.selectSummaryRecordMedia(searchDto);
		if(!(mediaList==null)) {
			for(SummaryRecordDto s: mediaList) {
				recordList.add(s);
			}
		}
		List<SummaryRecordDto> imageList=timelineDao.selectSummaryRecordImage(searchDto);
		if(!(imageList==null)) {
			for(SummaryRecordDto s: imageList) {
				recordList.add(s);
			}
		}
		
		return recordList;
	}
	
	@Override
	public String getSummaryDate(Search search) throws Exception {
		LocalDateTime dateTime = LocalDateTime.parse(search.getSearchKeyword());
		LocalDateTime vaildateDateTime = LocalDateTime.parse(dateTime.toLocalDate()+"T"+checkpointTime);
		String stringDateTime;
		if(vaildateDateTime.isBefore(dateTime) || vaildateDateTime.isEqual(dateTime)) {
			stringDateTime=vaildateDateTime.format(DATE_TIME_FORMATTER);
		}else {

			stringDateTime=vaildateDateTime.plusDays(-1).format(DATE_TIME_FORMATTER);
		}
		
		return timelineDao.selectSummaryDate(SearchDto.builder()
				.userId(search.getUserId())
				.searchCondition(search.getSearchCondition())
				.searchKeyword(stringDateTime)
				.build());
	}

	@Override
	public List<String> getSummaryDateList(String userId) throws Exception {
		return timelineDao.selectSummaryDateList(userId);
	}

	@Override
	public List<NotifyTimecapsuleDto> getNotifyTimecapsule() throws Exception {
		return timelineDao.selectNotifyTimecapsule();
	}

	@Override
	public List<KeywordData> getKeywordList(String userId) throws Exception {
		return timelineDao.selectKeywordList(userId);
	}

	@Override
	public int addKeyword(KeywordData keywordData) throws Exception {
		KeywordData updateData=timelineDao.selectKeyword(keywordData);
		int i=0;
		if(updateData==null) {
			if(keywordData.getKeywordCount()>0) {
				i=timelineDao.insertKeyword(keywordData);
			}
		}else {
			int adjustedCount=updateData.getKeywordCount()+keywordData.getKeywordCount();
			if(adjustedCount<=0) {
				i=timelineDao.deleteKeyword(updateData.getKeywordNo());
			}else {
				updateData.setKeywordCount(adjustedCount);
				i=timelineDao.updateKeyword(updateData);
			}
		}
		return i;
	}

	@Override
	public SharedRecord getDetailSharedRecord(int recordNo, String userId) throws Exception{
		return TimelineUtil.mapToSharedRecord(timelineDao.selectDetailSharedRecord(recordNo, userId));
	}
	
	@Override
	public List<SharedRecordDto> getSharedRecordList(Search search) throws Exception{
		return timelineDao.selectSharedRecordList(search);
	}

	@Override
	public List<MapRecord> getMapRecordList(Search search) throws Exception {
		SearchDto searchDto=GeoUtil.calculateRadius(search.getLatitude(), search.getLongitude(), search.getRadius());
		searchDto.setUserId(search.getUserId() );
		searchDto.setFollowType(search.getFollowType() );
//		searchDto.setUserIdList(timelineDao.selectFollowUserId(search.getUserId()) );
//		System.out.println("searchDto.getUserIdList() : "+searchDto.getUserIdList());
		searchDto.setLimit(search.getLimit() );
		searchDto.setSharedType(search.getSharedType() );
		searchDto.setOffset(search.getOffset() );
		searchDto.setPrivateType(search.getPrivateType());
		searchDto.setSearchKeyword(search.getSearchKeyword());
		searchDto.setCategoryNo(search.getCategoryNo());
		List<MapRecord> recordList=new ArrayList<MapRecord>();
		for(Map<String,Object> tempMap:timelineDao.selectMapRecordList(searchDto)) {
			MapRecord mapRecord =TimelineUtil.mapToMapRecord(tempMap);
			mapRecord.setDistance(GeoUtil.calculateCloseDistance(search.getLatitude(), search.getLongitude(), mapRecord.getLatitude(), mapRecord.getLongitude()));
			mapRecord.setStringDistance(GeoUtil.getStringDistance(mapRecord.getDistance()));
			recordList.add(mapRecord);
		}
		GeoUtil.sortByDistance(recordList);
		return recordList;
	}

	@Override
	public List<Map<String, Object>> getProfileTimelineList(Search search) throws Exception {
		return timelineDao.selectProfileTimelineList(search);
	}

	@Override
	public int getProfileTimelineCount(Search search) throws Exception {
		Integer result = timelineDao.selectProfileTimelineCount(search);
		
		if(result == null)
			return 0;
		else
			return result; 
	}

//	@Override
//	public void deleteHashtag(int recordNo) throws Exception {
//	}
	
	//record select시 imageNo 못가져와서 가져오는 image select
//	@Override
//	public List<ImageTagDto> getImageForDelete(int recordNo) throws Exception {
//		return timelineDao.selectImageForDelete(recordNo);
//	}
	
	//아래 미사용
//	@Override
//	public RecordDto getDetailTimeline2(int recordNo) throws Exception {
//		// TODO Auto-generated method stub
//		return recordDtoToRecord(timelineDao.selectDetailTimeline2(recordNo)
//				, timelineDao.selectImage2(recordNo)
//				, timelineDao.selectHashtag2(recordNo));
//	}
//	
//	@Override
//	public RecordDto recordDtoToRecord(Record2 record,List<String> image,List<String> heshtag) throws Exception {
//		RecordDto recordDto=RecordDto.builder()
//				.recordNo(record.getRecordNo())
//				.recordUserId(record.getRecordUserId())
//				.recordTitle(record.getRecordTitle())
//				.latitude(record.getLatitude())
//				.longitude(record.getLongitude())
//				.checkpointAddress(record.getCheckpointAddress())
//				.checkpointDate(record.getCheckpointDate())
//				.mediaName(record.getMediaName())
//				.imageName(image)
//				.Hashtag(heshtag)
//				.sharedType(record.getSharedType())
//				.categoryNo(record.getCategoryNo())
//				.recordText(record.getRecordText())
//				.tempType(record.getTempType())
//				.recordAddDate(record.getRecordAddDate())
//				.sharedDate(record.getSharedDate())
//				.updateCount(record.getUpdateCount())
//				.d_DayDate(record.getD_DayDate())
//				.timecapsuleType(record.getTimecapsuleType())
//				.build();
//		
//		return recordDto;
//	}
//	
//	@Override
//	public Record2 recordToRecordDto(RecordDto recordDto) throws Exception{
//		Record2 record=Record2.builder()
//				.recordNo(recordDto.getRecordNo())
//				.recordUserId(recordDto.getRecordUserId())
//				.recordTitle(recordDto.getRecordTitle())
//				.latitude(recordDto.getLatitude())
//				.longitude(recordDto.getLongitude())
//				.checkpointAddress(recordDto.getCheckpointAddress())
//				.checkpointDate(recordDto.getCheckpointDate())
//				.mediaName(recordDto.getMediaName())
//				.sharedType(recordDto.getSharedType())
//				.categoryNo(recordDto.getCategoryNo())
//				.recordText(recordDto.getRecordText())
//				.tempType(recordDto.getTempType())
//				.recordAddDate(recordDto.getRecordAddDate())
//				.sharedDate(recordDto.getSharedDate())
//				.updateCount(recordDto.getUpdateCount())
//				.d_DayDate(recordDto.getD_DayDate())
//				.timecapsuleType(recordDto.getTimecapsuleType())
//				.build();
//		
//		return record;
//	}
//
//	@Override
//	public List<ImageTag> imageToRecordDto(RecordDto recordDto) throws Exception{
//		List<ImageTag> imageList=new ArrayList<ImageTag>();
//		for(String text:recordDto.getImageName()) {
//			imageList.add(ImageTag.builder()
//					.recordNo(recordDto.getRecordNo())
//					.imageTagType(1)
//					.imageTagText(text)
//					.build());
//		}
//		return imageList;
//	}
//	
//	@Override
//	public List<ImageTag> hashtagToRecordDto(RecordDto recordDto) throws Exception{
//		List<ImageTag> hashtagList=new ArrayList<ImageTag>();
//		for(String text:recordDto.getImageName()) {
//			hashtagList.add(ImageTag.builder()
//					.recordNo(recordDto.getRecordNo())
//					.imageTagType(0)
//					.imageTagText(text)
//					.build());
//		}
//		return hashtagList;
//	}
	
	

}
