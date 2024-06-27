package com.mapmory.services.timeline.dao;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.mapmory.common.domain.Search;
import com.mapmory.services.timeline.domain.Category;
import com.mapmory.services.timeline.domain.ImageTag;
import com.mapmory.services.timeline.domain.KeywordData;
import com.mapmory.services.timeline.domain.Record;
import com.mapmory.services.timeline.dto.CountAddressDto;
import com.mapmory.services.timeline.dto.ImageTagDto;
import com.mapmory.services.timeline.dto.NotifyTimecapsuleDto;
import com.mapmory.services.timeline.dto.SearchDto;
import com.mapmory.services.timeline.dto.SharedRecordDto;
import com.mapmory.services.timeline.dto.SummaryRecordDto;

@Mapper
public interface TimelineDao {
	
	//Record CRUD
	public void insertTimeline(Record record) throws Exception;
	
	public void insertImageTag(Map<String,Object> map) throws Exception;
	//resultmap collection 사용 시 Map으로만 묶어짐
	public Map<String, Object> selectDetailTimeline(int recordNo) throws Exception;
	
	public List<Map<String, Object>> selectTimelineList(Search search) throws Exception;
	
	public int selectTimelineCount(Search search) throws Exception;
	
	public void updateTimeline(Record record) throws Exception;
	
	public void deleteTimeline(int recordNo) throws Exception;
	
	public void deleteImageTag(ImageTag imageTag) throws Exception;
	
	public int deleteImageToImageNo(int imageNo) throws Exception;
	
	public int updateMedia(int recordNo) throws Exception;
	
	//Category CRUD
	public int insertCategory(Category category) throws Exception;
	
	public Category selectCategory(int categoryNo) throws Exception;
	
	public List<Category> selectCategoryList() throws Exception;
	
	public int updateCategory(Category category) throws Exception;
	
	public int deleteCategory(int categoryNo) throws Exception;
	
	public CountAddressDto selectCountAddress(Record record) throws Exception;
	
	public List<SummaryRecordDto> selectSummaryRecordImage(SearchDto searchDto) throws Exception;
	
	public List<SummaryRecordDto> selectSummaryRecordMedia(SearchDto searchDto) throws Exception;
	
	public String selectSummaryDate(SearchDto searchDto) throws Exception;
	
	public List<String> selectSummaryDateList(String userId) throws Exception;
	
	public List<NotifyTimecapsuleDto> selectNotifyTimecapsule() throws Exception;
	
	public List<KeywordData> selectKeywordList(String userId) throws Exception;
	
	public KeywordData selectKeyword(KeywordData keywordData) throws Exception;
	
	public int insertKeyword(KeywordData keywordData) throws Exception;
	
	public int updateKeyword(KeywordData keywordData) throws Exception;
	
	public int deleteKeyword(int keywordNo) throws Exception;
	
	public int deleteKeywordToId(String userId) throws Exception;
	
	public Map<String, Object> selectDetailSharedRecord(int recordNo, String userId) throws Exception;
	
	public List<SharedRecordDto> selectSharedRecordList(Search search) throws Exception;
	
	public List<Map<String, Object>> selectMapRecordList(SearchDto searchDto) throws Exception;
	
	public Record selectDetailTimeline2(int recordNo) throws Exception;
	
	public List<Map<String, Object>> selectProfileTimelineList(Search search) throws Exception;
	
	public Integer selectProfileTimelineCount(Search search) throws Exception;
	
	//아래 미사용
//	//record insert시 Imagefile insert
//	public void insertImageName(Map<String,Object> map) throws Exception;
//	//record insert시 Hashtag insert
//	public void insertHashtag(Map<String,Object> map) throws Exception;
//	
//	//Image and Hashtag delete
//	//record select시 imageNo 못가져와서 가져오는 image select
//	public List<ImageTagDto> selectImageForDelete(int recordNo) throws Exception;
//	
//	public void deleteHashtag(int recordNo) throws Exception;
//	
//	public List<String> selectFollowUserId(String userID) throws Exception;
//	
//	public Record2 selectDetailTimeline2(int recordNo) throws Exception;
//
//	public List<String> selectImage2(int recordNo) throws Exception;
//
//	public List<String> selectHashtag2(int recordNo) throws Exception;

}
