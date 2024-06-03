package com.mapmory.services.timeline.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mapmory.services.timeline.domain.Record;
import com.mapmory.services.timeline.domain.Record2;


@Mapper
public interface TimelineDao {
	
	public Record selectDetailTimeline(int recordNo) throws Exception;
	
	public List<String> selectImage(int recordNo) throws Exception;
	
	public List<String> selectHashtag(int recordNo) throws Exception;
	
	public List<Record2> selectDetailTimeline2(int recordNo) throws Exception;
	
	

}
