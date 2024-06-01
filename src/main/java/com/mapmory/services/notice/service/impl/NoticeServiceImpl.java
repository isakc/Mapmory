package com.mapmory.services.notice.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mapmory.common.domain.Search;
import com.mapmory.services.notice.dao.NoticeDao;
import com.mapmory.services.notice.domain.Notice;
import com.mapmory.services.notice.service.NoticeService;

@Service("noticeServiceImpl")
public class NoticeServiceImpl implements NoticeService {
	
	@Autowired
	@Qualifier("noticeDao")
	private NoticeDao noticeDao;
	
	public void setNoticeDao(NoticeDao noticeDao) {
		this.noticeDao = noticeDao;
	}
	
	public NoticeServiceImpl() {
		System.out.println(this.getClass());
	}
	
	public Map<String, Object> getNoticeList(Search search) throws Exception{
		List<Notice> list = noticeDao.getNoticeList(search);
		int totalCount = noticeDao.getNoticeTotalCount(search);
		
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("noticeList", list);
		map.put("noticeTotalCount", new Integer(totalCount));
		return map;
	}
	
	public void addNotice(Notice notice) throws Exception{
		noticeDao.addNotice(notice);
	}
	
	public Notice getDetailNotice(int noticeNo) throws Exception{
		return noticeDao.getDetailNotice(noticeNo);
	}
	
	public void updateNoticeAndFaq(Notice notice) throws Exception{
		noticeDao.updateNoticeAndFaq(notice);
	}
	
	public void deleteNoticeAndFaq(int noticeNo) throws Exception {
		noticeDao.deleteNoticeAndFaq(noticeNo);
	}
	
	public Map<String, Object> getFaqList(Search search) throws Exception{
		List<Notice> list = noticeDao.getNoticeList(search);
		int totalCount = noticeDao.getNoticeTotalCount(search);
		
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("noticeList", list);
		map.put("noticeTotalCount", new Integer(totalCount));
		return map;
	}
	
	public void addFaq(Notice notice) throws Exception{
		noticeDao.addFaq(notice);
	}
	
	public Notice getDetailFaq(int noticeNo) throws Exception{
		return noticeDao.getDetailFaq(noticeNo);
	}
	
}
