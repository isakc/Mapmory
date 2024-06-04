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

import com.mapmory.services.timeline.dao.TimelineDao;
import com.mapmory.services.timeline.domain.Category;
import com.mapmory.services.timeline.domain.ImageTag;
import com.mapmory.services.timeline.domain.ImageTagDto;
import com.mapmory.services.timeline.domain.Record2;
import com.mapmory.services.timeline.domain.Record;
import com.mapmory.services.timeline.domain.RecordDto;
import com.mapmory.services.timeline.domain.Search;
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
		map.put("imageName",record.getImageName());
		map.put("hashtag",record.getHashtag());
		
		timelineDao.insertImageName(map);
		timelineDao.insertHashtag(map);
	}
	
	@Override
	public Record getDetailTimeline(int recordNo) throws Exception{
		return recordToMap(timelineDao.selectDetailTimeline(recordNo));
	}
	
	@Override
	public List<Record> getTimelineList(Search search) throws Exception{
		List<Record> recordList=new ArrayList<Record>();
		for(Map<String,Object> map:timelineDao.selectTimelineList(search)) {
			recordList.add(recordToMap(map));
		}
		return recordList;
	}

	@Override
	public void updateTimeline(Record record) throws Exception {
		Map<String, Object> map=new HashMap<String, Object>();
		timelineDao.updateTimeline(record);
		map.put("recordNo",record.getRecordNo());
		map.put("imageName",record.getImageName());
		map.put("hashtag",record.getHashtag());
		
		timelineDao.insertImageName(map);
		timelineDao.insertHashtag(map);
		
	}

	@Override
	public void deleteTimeline(int recordNo) throws Exception {
		timelineDao.deleteHashtag(recordNo);
		timelineDao.deleteImageToRecordNo(recordNo);
		timelineDao.deleteTimeline(recordNo);
	}
	
	//map을 record로 묶어주는 기능
	@Override
	public Record recordToMap(Map<String, Object> map) throws Exception {
		Record record=Record.builder()
				.recordNo((int)map.get("recordNo"))
				.recordUserId((String)map.get("recordUserId"))
				.recordTitle((String)map.get("recordTitle"))
				.latitude((Double)map.get("latitude"))
				.longitude((Double)map.get("longitude"))
				.checkpointAddress((String)map.get("checkpointAddress"))
				.checkpointDate((LocalDateTime)map.get("checkpointDate"))
				.mediaName(map.get("mediaName") ==null ? "" : (String)map.get("mediaName"))
				.imageName((List<String>)map.get("imageName"))
				.hashtag((List<String>)map.get("hashtag"))
				.categoryNo((Integer)map.get("categoryNo"))
				.recordText(map.get("recordText") ==null ? "" : (String)map.get("recordText"))
				.tempType((Integer)map.get("tempType"))
				.recordAddDate((LocalDateTime)map.get("recordAddDate"))
				.sharedDate((LocalDateTime)map.get("sharedDate"))
				.updateCount((Integer)map.get("updateCount"))
				.d_DayDate((Date)map.get("d_DayDate"))
				.timecapsuleType((Integer)map.get("timecapsuleType"))
				.build();
		return record;
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
