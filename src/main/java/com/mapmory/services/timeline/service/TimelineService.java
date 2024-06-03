package com.mapmory.services.timeline.service;

import java.util.List;

import com.mapmory.services.timeline.domain.ImageTag;
import com.mapmory.services.timeline.domain.Record;
import com.mapmory.services.timeline.domain.Record2;
import com.mapmory.services.timeline.domain.RecordDto;

public interface TimelineService {
	
	public RecordDto getDetailTimeline(int recordNo) throws Exception;
	
	public RecordDto recordDtoToRecord(Record record,List<String> image,List<String> heshtag) throws Exception;

	public Record recordToRecordDto(RecordDto recordDto) throws Exception;
	
	public List<ImageTag> imageToRecordDto(RecordDto recordDto) throws Exception;
	
	public List<ImageTag> hashtagToRecordDto(RecordDto recordDto) throws Exception;
	
	public List<Record2> getDetailTimeline2(int recordNo) throws Exception;

}
