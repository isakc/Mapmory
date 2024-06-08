package com.mapmory.services.timeline.service.impl;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mapmory.common.domain.Search;
import com.mapmory.common.util.GeoUtil;
import com.mapmory.common.util.TimelineUtil;
import com.mapmory.services.timeline.dao.TimelineDao;
import com.mapmory.services.timeline.domain.Category;
import com.mapmory.services.timeline.domain.CountAddressDto;
import com.mapmory.services.timeline.domain.ImageTag;
import com.mapmory.services.timeline.domain.ImageTagDto;
import com.mapmory.services.timeline.domain.Record2;
import com.mapmory.services.timeline.domain.Record;
import com.mapmory.services.timeline.domain.RecordDto;
import com.mapmory.services.timeline.domain.SearchDto;
import com.mapmory.services.timeline.domain.SharedRecord;
import com.mapmory.services.timeline.service.TimelineService;


@Service("timelineService")
@Transactional
public class TimelineServiceImpl implements TimelineService {
	
	@Autowired
	@Qualifier("timelineDao")
	private TimelineDao timelineDao;
	
	//Record CRUD
	public void addTimeline(Record record) throws Exception{
		Map<String, Object> map=new HashMap<String, Object>();
		timelineDao.insertTimeline(record);
		System.out.println(record);
		map.put("recordNo",record.getRecordNo());
//		System.out.println("record.getImageTagList():"+record.getImageTagList());
		map.put("imageTagList",TimelineUtil.listToImageTag(record.getImageName(),record.getHashtag()));
//		map.put("imageName",record.getImageName());
//		map.put("hashtag",record.getHashtag());
		
		timelineDao.insertImageTag(map);
//		timelineDao.insertImageName(map);
//		timelineDao.insertHashtag(map);
	}
	
	@Override
	public Record getDetailTimeline(int recordNo) throws Exception{
		return TimelineUtil.recordToMap(timelineDao.selectDetailTimeline(recordNo));
	}
	
	@Override
	public List<Record> getTimelineList(Search search) throws Exception{
		List<Record> recordList=new ArrayList<Record>();
		for(Map<String,Object> map:timelineDao.selectTimelineList(search)) {
			recordList.add(TimelineUtil.recordToMap(map));
		}
		return recordList;
	}

	@Override
	public void updateTimeline(Record record) throws Exception {
		Map<String, Object> map=new HashMap<String, Object>();
		timelineDao.updateTimeline(record);
		map.put("recordNo",record.getRecordNo());
		map.put("imageTagList",TimelineUtil.listToImageTag(record.getImageName(),record.getHashtag()));
//		map.put("imageName",record.getImageName());
//		map.put("hashtag",record.getHashtag());
		
		timelineDao.insertImageTag(map);
//		timelineDao.insertImageName(map);
//		timelineDao.insertHashtag(map);
		
	}

	@Override
	public void deleteTimeline(int recordNo) throws Exception {
//		timelineDao.deleteHashtag(recordNo);
//		timelineDao.deleteImageToRecordNo(recordNo);
		timelineDao.deleteImageTag(recordNo);
		timelineDao.deleteTimeline(recordNo);
	}
	
	//record select시 imageNo 못가져와서 가져오는 image select
	@Override
	public List<ImageTagDto> getImageForDelete(int recordNo) throws Exception {
		return timelineDao.selectImageForDelete(recordNo);
	}
	//image만 삭제
	@Override
	public void deleteImage(int imageNo) throws Exception {
		timelineDao.deleteImageToImageNo(imageNo);
	}

//	@Override
//	public void deleteHashtag(int recordNo) throws Exception {
//	}
	//Category CRUD
	@Override
	public void addCategory(Category category) throws Exception {
		timelineDao.insertCategory(category);
	}

	@Override
	public Category getCategory(int categoryNo) throws Exception {
		return timelineDao.selectCategory(categoryNo);
	}

	@Override
	public List<Category> getCategoryList() throws Exception {
		return timelineDao.selectCategoryList();
	}

	@Override
	public void updateCategory(Category category) throws Exception {
		timelineDao.updateCategory(category);
	}

	@Override
	public void deleteCategory(int categoryNo) throws Exception {
		timelineDao.deleteCategory(categoryNo);
	}
	
	@Override
	public CountAddressDto getCountAddress(Record record) throws Exception{
		return timelineDao.selectCountAddress(record);
	}
	
	@Override
	public List<SharedRecord> getSharedRecordList(Search search) throws Exception{
		return timelineDao.selectSharedRecordList(search);
	}

	@Override
	public List<Record> getMapRecordList(Search search) throws Exception {
		SearchDto searchDto=GeoUtil.calculateRadius(search.getLatitude(), search.getLongitude(), search.getRadius());
		searchDto.setUserId(search.getUserId() );
		searchDto.setFollowType(search.getFollowType() );
//		searchDto.setUserIdList(timelineDao.selectFollowUserId(search.getUserId()) );
//		System.out.println("searchDto.getUserIdList() : "+searchDto.getUserIdList());
		searchDto.setLimit(search.getLimit() );
		searchDto.setSharedType(search.getSharedType() );
		searchDto.setOffset(search.getOffset() );
		List<Record> recordList=new ArrayList<Record>();
		for(Map<String,Object> tempMap:timelineDao.selectMapRecordList(searchDto)) {
			Record record =TimelineUtil.recordToMap(tempMap);
			record.setDistance(GeoUtil.calculateCloseDistance(search.getLatitude(), search.getLongitude(), record.getLatitude(), record.getLongitude()));
			recordList.add(record);
		}
		GeoUtil.sortByDistance(recordList);
		return recordList;
	}
	

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
