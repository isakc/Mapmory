package com.mapmory.services.timeline.service;

import java.util.List;
import java.util.Map;

import com.mapmory.services.timeline.domain.ImageTag;
import com.mapmory.services.timeline.domain.ImageTagDto;
import com.mapmory.services.timeline.domain.Record2;
import com.mapmory.services.timeline.domain.Record;
import com.mapmory.services.timeline.domain.RecordDto;
import com.mapmory.services.timeline.domain.Search;

public interface TimelineService {
	
	public Record getDetailTimeline(int recordNo) throws Exception;
	
	public void addTimeline(Record record) throws Exception;
	
	public void updateTimeline(Record record) throws Exception;
	
	public void deleteTimeline(int recordNo) throws Exception;
	
	public List<ImageTagDto> getImageForDelete(int recordNo) throws Exception;
	
	public void deleteImage(int imageNo) throws Exception;
	
//	public void deleteHashtag(int recordNo) throws Exception;
	
	public List<Record> getTimelineList(Search search) throws Exception;
	
	
	//아래 미사용
	public RecordDto getDetailTimeline2(int recordNo) throws Exception;
	
	public RecordDto recordDtoToRecord(Record2 record,List<String> image,List<String> heshtag) throws Exception;

	public Record2 recordToRecordDto(RecordDto recordDto) throws Exception;
	
	public List<ImageTag> imageToRecordDto(RecordDto recordDto) throws Exception;
	
	public List<ImageTag> hashtagToRecordDto(RecordDto recordDto) throws Exception;

}
