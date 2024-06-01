package com.mapmory.services.notice.service;

import java.util.Map;

import com.mapmory.common.domain.Search;
import com.mapmory.services.notice.domain.Notice;

public interface NoticeService {
	
	public Map<String, Object> getNoticeList(Search search) throws Exception;
	
	public Notice getDetailNotice(int noticeNo) throws Exception;
	
	public void updateNoticeAndFaq(Notice notice) throws Exception;
	
	public void addNotice(Notice notice) throws Exception;
	
	public void deleteNoticeAndFaq(int noticeNo) throws Exception;
	
	public Map<String, Object> getFaqList(Search search) throws Exception;
	
	public Notice getDetailFaq(int noticeNo) throws Exception;
	
	public void addFaq(Notice notice) throws Exception;
	
}
