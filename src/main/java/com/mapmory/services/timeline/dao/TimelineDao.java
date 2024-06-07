package com.mapmory.services.timeline.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.mapmory.common.domain.Search;
import com.mapmory.services.timeline.domain.Category;
import com.mapmory.services.timeline.domain.ImageTag;
import com.mapmory.services.timeline.domain.ImageTagDto;
import com.mapmory.services.timeline.domain.Record;
import com.mapmory.services.timeline.domain.Record2;
import com.mapmory.services.timeline.domain.SearchDto;
import com.mapmory.services.timeline.domain.SharedRecord;

@Mapper
public interface TimelineDao {
	
	//Record CRUD
	public void insertTimeline(Record record) throws Exception;
	
	public void insertImageTag(Map<String,Object> map) throws Exception;
//	//record insert시 Imagefile insert
//	public void insertImageName(Map<String,Object> map) throws Exception;
//	//record insert시 Hashtag insert
//	public void insertHashtag(Map<String,Object> map) throws Exception;
	//resultmap collection 사용 시 Map으로만 묶어짐
	public Map<String, Object> selectDetailTimeline(int recordNo) throws Exception;
	
	public List<Map<String, Object>> selectTimelineList(Search search) throws Exception;
	
	public int selectTimelineCount(Search search) throws Exception;
	
	public void updateTimeline(Record record) throws Exception;
	
	public void deleteTimeline(int recordNo) throws Exception;
	
	//Image and Hashtag delete
	//record select시 imageNo 못가져와서 가져오는 image select
	public List<ImageTagDto> selectImageForDelete(int recordNo) throws Exception;
	
	public void deleteImageTag(int recordNo) throws Exception;
	
	public void deleteImageToRecordNo(int recordNo) throws Exception;
	
	public void deleteImageToImageNo(int imageNo) throws Exception;
	
	public void deleteHashtag(int recordNo) throws Exception;
	
	//Category CRUD
	public void insertCategory(Category category) throws Exception;
	
	public Category selectCategory(int categoryNo) throws Exception;
	
	public List<Category> selectCategoryList() throws Exception;
	
	public void updateCategory(Category category) throws Exception;
	
	public void deleteCategory(int categoryNo) throws Exception;
	
	public List<SharedRecord> selectSharedRecordList(Search search) throws Exception;
	
	public List<Map<String, Object>> selectMapRecordList(SearchDto searchDto) throws Exception;
	
//	public List<String> selectFollowUserId(String userID) throws Exception;
	
	//아래 미사용
//	public Record2 selectDetailTimeline2(int recordNo) throws Exception;
//
//	public List<String> selectImage2(int recordNo) throws Exception;
//
//	public List<String> selectHashtag2(int recordNo) throws Exception;

}
