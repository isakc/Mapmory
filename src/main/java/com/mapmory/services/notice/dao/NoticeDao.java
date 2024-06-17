package com.mapmory.services.notice.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import com.mapmory.common.domain.Search;
import com.mapmory.services.notice.domain.Notice;

@Mapper
public interface NoticeDao {
 
	public List<Notice> getNoticeList(Search search) throws Exception;
	
	public List<Notice> getFaqList(Search search) throws Exception;
	
	public Notice getDetailNotice(int noticeNo) throws Exception;
	
	public void updateNoticeAndFaq(Notice notice) throws Exception;
	
	public void addNoticeOrFaq(Notice notice) throws Exception;
	
	public void deleteNoticeAndFaq(int noticeNo) throws Exception;
	
	public int getFaQTotalCount(Search search) throws Exception;
	
	public int getNoticeTotalCount(Search search) throws Exception;
	
}