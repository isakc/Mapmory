package com.mapmory.services.timeline.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.mapmory.services.timeline.domain.ImageTag;
import com.mapmory.services.timeline.domain.ImageTagDto;
import com.mapmory.services.timeline.domain.Record;
import com.mapmory.services.timeline.domain.Record2;
import com.mapmory.services.timeline.domain.Search;

@Mapper
public interface TimelineDao {

	public Map<String, Object> selectDetailTimeline(int recordNo) throws Exception;
	
	public void insertTimeline(Record record) throws Exception;
	
	public void insertImageName(Map<String,Object> map) throws Exception;
	
	public void insertHashtag(Map<String,Object> map) throws Exception;
	
	public void updateTimeline(Record record) throws Exception;
	
	public void deleteTimeline(int recordNo) throws Exception;
	
	public List<ImageTagDto> selectImageForDelete(int recordNo) throws Exception;
	
	public void deleteImageToRecordNo(int recordNo) throws Exception;
	
	public void deleteImageToImageNo(int imageNo) throws Exception;
	
	public void deleteHashtag(int recordNo) throws Exception;
	
	public List<Record> selectTimelineList(Search search) throws Exception;
	
	public int selectTimelineCount(Search search) throws Exception;

	//아래 미사용
	public Record2 selectDetailTimeline2(int recordNo) throws Exception;

	public List<String> selectImage2(int recordNo) throws Exception;

	public List<String> selectHashtag2(int recordNo) throws Exception;

}
