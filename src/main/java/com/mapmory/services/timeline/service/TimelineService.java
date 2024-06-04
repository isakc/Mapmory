package com.mapmory.services.timeline.service;

import java.util.List;
import java.util.Map;

import com.mapmory.services.timeline.domain.Category;
import com.mapmory.services.timeline.domain.ImageTag;
import com.mapmory.services.timeline.domain.ImageTagDto;
import com.mapmory.services.timeline.domain.Record2;
import com.mapmory.services.timeline.domain.Record;
import com.mapmory.services.timeline.domain.RecordDto;
import com.mapmory.services.timeline.domain.Search;

public interface TimelineService {
	//Record CRUD
	public void addTimeline(Record record) throws Exception;
	
	public Record getDetailTimeline(int recordNo) throws Exception;
	
	public List<Record> getTimelineList(Search search) throws Exception;
	
	public void updateTimeline(Record record) throws Exception;
	
	public void deleteTimeline(int recordNo) throws Exception;
	//map을 record로 묶어주는 기능
	public Record recordToMap(Map<String,Object> map) throws Exception;
	//record select시 imageNo 못가져와서 가져오는 image select
	public List<ImageTagDto> getImageForDelete(int recordNo) throws Exception;
	//image만 삭제
	public void deleteImage(int imageNo) throws Exception;
	//hashtag는 일괄 삭제 후 summit
//	public void deleteHashtag(int recordNo) throws Exception;
	
	//Category CRUD
	public void addCategory(Category category) throws Exception;
	
	public Category getCategory(int categoryNo) throws Exception;
	
	public List<Category> getCategoryList() throws Exception;
	
	public void updateCategory(Category category) throws Exception;
	
	public void deleteCategory(int categoryNo) throws Exception;
	
	
	//아래 미사용
//	public RecordDto getDetailTimeline2(int recordNo) throws Exception;
//	
//	public RecordDto recordDtoToRecord(Record2 record,List<String> image,List<String> heshtag) throws Exception;
//
//	public Record2 recordToRecordDto(RecordDto recordDto) throws Exception;
//	
//	public List<ImageTag> imageToRecordDto(RecordDto recordDto) throws Exception;
//	
//	public List<ImageTag> hashtagToRecordDto(RecordDto recordDto) throws Exception;

}
