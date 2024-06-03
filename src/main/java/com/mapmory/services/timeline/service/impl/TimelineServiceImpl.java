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
	
	@Override
	public Record getDetailTimeline(int recordNo) throws Exception{
		Map<String,Object> recordMap=timelineDao.selectDetailTimeline(recordNo);
		Record record=Record.builder()
				.recordNo((int)recordMap.get("recordNo"))
				.recordUserId((String)recordMap.get("recordUserId"))
				.recordTitle((String)recordMap.get("recordTitle"))
				.latitude((Double)recordMap.get("latitude"))
				.longitude((Double)recordMap.get("longitude"))
				.checkpointAddress((String)recordMap.get("checkpointAddress"))
				.checkpointDate((LocalDateTime)recordMap.get("checkpointDate"))
				.mediaName((String)recordMap.get("mediaName"))
				.imageName((List<String>)recordMap.get("imageName"))
				.hashtag((List<String>)recordMap.get("hashtag"))
				.categoryNo((Integer)recordMap.get("categoryNo"))
				.recordText((String)recordMap.get("recordText"))
				.tempType((Integer)recordMap.get("tempType"))
				.recordAddDate((LocalDateTime)recordMap.get("recordAddDate"))
				.sharedDate((LocalDateTime)recordMap.get("sharedDate"))
				.updateCount((Integer)recordMap.get("updateCount"))
				.d_DayDate((Date)recordMap.get("d_DayDate"))
				.timecapsuleType((Integer)recordMap.get("timecapsuleType"))
				.build();
		return record;
	}
	
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

	@Override
	public List<ImageTagDto> getImageForDelete(int recordNo) throws Exception {
		return timelineDao.selectImageForDelete(recordNo);
	}

	@Override
	public void deleteImage(int imageNo) throws Exception {
		timelineDao.deleteImageToImageNo(imageNo);
	}

//	@Override
//	public void deleteHashtag(int recordNo) throws Exception {
//	}
	
	@Override
	public List<Record> getTimelineList(Search search) throws Exception{
		return timelineDao.selectTimelineList(search);
	}

	//아래 미사용
	@Override
	public RecordDto getDetailTimeline2(int recordNo) throws Exception {
		// TODO Auto-generated method stub
		return recordDtoToRecord(timelineDao.selectDetailTimeline2(recordNo)
				, timelineDao.selectImage2(recordNo)
				, timelineDao.selectHashtag2(recordNo));
	}
	
	@Override
	public RecordDto recordDtoToRecord(Record2 record,List<String> image,List<String> heshtag) throws Exception {
		RecordDto recordDto=RecordDto.builder()
				.recordNo(record.getRecordNo())
				.recordUserId(record.getRecordUserId())
				.recordTitle(record.getRecordTitle())
				.latitude(record.getLatitude())
				.longitude(record.getLongitude())
				.checkpointAddress(record.getCheckpointAddress())
				.checkpointDate(record.getCheckpointDate())
				.mediaName(record.getMediaName())
				.imageName(image)
				.Hashtag(heshtag)
				.sharedType(record.getSharedType())
				.categoryNo(record.getCategoryNo())
				.recordText(record.getRecordText())
				.tempType(record.getTempType())
				.recordAddDate(record.getRecordAddDate())
				.sharedDate(record.getSharedDate())
				.updateCount(record.getUpdateCount())
				.d_DayDate(record.getD_DayDate())
				.timecapsuleType(record.getTimecapsuleType())
				.build();
		
		return recordDto;
	}
	
	@Override
	public Record2 recordToRecordDto(RecordDto recordDto) throws Exception{
		Record2 record=Record2.builder()
				.recordNo(recordDto.getRecordNo())
				.recordUserId(recordDto.getRecordUserId())
				.recordTitle(recordDto.getRecordTitle())
				.latitude(recordDto.getLatitude())
				.longitude(recordDto.getLongitude())
				.checkpointAddress(recordDto.getCheckpointAddress())
				.checkpointDate(recordDto.getCheckpointDate())
				.mediaName(recordDto.getMediaName())
				.sharedType(recordDto.getSharedType())
				.categoryNo(recordDto.getCategoryNo())
				.recordText(recordDto.getRecordText())
				.tempType(recordDto.getTempType())
				.recordAddDate(recordDto.getRecordAddDate())
				.sharedDate(recordDto.getSharedDate())
				.updateCount(recordDto.getUpdateCount())
				.d_DayDate(recordDto.getD_DayDate())
				.timecapsuleType(recordDto.getTimecapsuleType())
				.build();
		
		return record;
	}

	@Override
	public List<ImageTag> imageToRecordDto(RecordDto recordDto) throws Exception{
		List<ImageTag> imageList=new ArrayList<ImageTag>();
		for(String text:recordDto.getImageName()) {
			imageList.add(ImageTag.builder()
					.recordNo(recordDto.getRecordNo())
					.imageTagType(1)
					.imageTagText(text)
					.build());
		}
		return imageList;
	}
	
	@Override
	public List<ImageTag> hashtagToRecordDto(RecordDto recordDto) throws Exception{
		List<ImageTag> hashtagList=new ArrayList<ImageTag>();
		for(String text:recordDto.getImageName()) {
			hashtagList.add(ImageTag.builder()
					.recordNo(recordDto.getRecordNo())
					.imageTagType(0)
					.imageTagText(text)
					.build());
		}
		return hashtagList;
	}
	
	

}
