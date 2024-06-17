package com.mapmory.services.timeline.service;

import java.util.List;
import java.util.Map;

import com.mapmory.common.domain.Search;
import com.mapmory.services.timeline.domain.Category;
import com.mapmory.services.timeline.domain.MapRecord;
import com.mapmory.services.timeline.domain.Record;
import com.mapmory.services.timeline.domain.SharedRecord;
import com.mapmory.services.timeline.dto.CountAddressDto;
import com.mapmory.services.timeline.dto.ImageTagDto;
import com.mapmory.services.timeline.dto.SharedRecordDto;
import com.mapmory.services.timeline.dto.SummaryRecordDto;

public interface TimelineService {
	//Record CRUD
	public int addTimeline(Record record) throws Exception;
	
	public Record getDetailTimeline(int recordNo) throws Exception;
	
	public List<Record> getTimelineList(Search search) throws Exception;
	
	public void updateTimeline(Record record) throws Exception;
	
	public void deleteTimeline(int recordNo) throws Exception;
	//image만 삭제
	public int deleteImage(int imageNo) throws Exception;
	
	//Category CRUD
	public void addCategory(Category category) throws Exception;
	
	public Category getCategory(int categoryNo) throws Exception;
	
	public List<Category> getCategoryList() throws Exception;
	
	public void updateCategory(Category category) throws Exception;
	
	public void deleteCategory(int categoryNo) throws Exception;
	
	public CountAddressDto getCountAddress(Record record) throws Exception;
	
	public SummaryRecordDto getSummaryRecord(Search search) throws Exception;
	
	public SharedRecord getDetailSharedRecord(int recordNo, String userId) throws Exception;
	
	public List<SharedRecordDto> getSharedRecordList(Search search) throws Exception;
	
	public List<MapRecord> getMapRecordList(Search search) throws Exception;
	
	public List<Map<String, Object>> getProfileTimelineList(Search search) throws Exception;
	
	

	//record select시 imageNo 못가져와서 가져오는 image select
//	public List<ImageTagDto> getImageForDelete(int recordNo) throws Exception;
	//hashtag는 일괄 삭제 후 summit
//	public void deleteHashtag(int recordNo) throws Exception;
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
