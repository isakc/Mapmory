package com.mapmory.services.timeline.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mapmory.services.timeline.dao.TimelineDao;
import com.mapmory.services.timeline.domain.ImageTag;
import com.mapmory.services.timeline.domain.Record;
import com.mapmory.services.timeline.domain.Record2;
import com.mapmory.services.timeline.domain.RecordDto;
import com.mapmory.services.timeline.service.TimelineService;


@Service("timelineService")
@Transactional
public class TimelineServiceImpl implements TimelineService {
	
	@Autowired
	@Qualifier("timelineDao")
	private TimelineDao timelineDao;
	
	@Override
	public List<Record2> getDetailTimeline2(int recordNo) throws Exception{
		return timelineDao.selectDetailTimeline2(recordNo);
	}

	@Override
	public RecordDto getDetailTimeline(int recordNo) throws Exception {
		// TODO Auto-generated method stub
		return recordDtoToRecord(timelineDao.selectDetailTimeline(recordNo)
				, timelineDao.selectImage(recordNo)
				, timelineDao.selectHashtag(recordNo));
	}
	
	@Override
	public RecordDto recordDtoToRecord(Record record,List<String> image,List<String> heshtag) throws Exception {
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
	public Record recordToRecordDto(RecordDto recordDto) throws Exception{
		Record record=Record.builder()
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
