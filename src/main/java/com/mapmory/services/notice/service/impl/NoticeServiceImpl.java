package com.mapmory.services.notice.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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
	
	@Value("${page.size}")
	private String pageSize;
	
	@Value("${page.Unit}")
	private String pageUnit;
	
	public void setNoticeDao(NoticeDao noticeDao) {
		this.noticeDao = noticeDao;
	}
	
	public NoticeServiceImpl() {
		System.out.println(this.getClass());
	}
	
	@Override
    public Map<String,Object> getNoticeList(Search search) throws Exception {
		int offset = (search.getCurrentPage() - 1) * search.getPageSize();
	    search.setOffset(offset);
	    search.setLimit(search.getPageSize());

	    List<Notice> noticeList = noticeDao.getNoticeList(search);
	    int totalCount = noticeDao.getNoticeTotalCount(search);

	    Map<String,Object> map = new HashMap<String, Object>();
	    map.put("noticeList", noticeList);
	    map.put("noticeTotalCount", new Integer(totalCount));

	    return map;
    }
	
	@Override
	public Map<String,Object> getFaqList(Search search) throws Exception {
	    int offset = (search.getCurrentPage() - 1) * search.getPageSize();
	    search.setOffset(offset);
	    search.setPageSize(search.getPageSize());

	    List<Notice> noticeList = noticeDao.getFaqList(search);
	    int totalCount = noticeDao.getFaQTotalCount(search);

	    Map<String,Object> map = new HashMap<String, Object>();
	    map.put("noticeList", noticeList);
	    map.put("noticeTotalCount", new Integer(totalCount));

	    return map;
	}
	
	public Notice getDetailNotice(int noticeNo) throws Exception {
		return noticeDao.getDetailNotice(noticeNo);
	}
	
	public void addNoticeOrFaq(Notice notice) throws Exception {
		noticeDao.addNoticeOrFaq(notice);
	}
	
	
	public void updateNoticeAndFaq(Notice notice) throws Exception {
		noticeDao.updateNoticeAndFaq(notice);
	}
	
	public void deleteNoticeAndFaq(int noticeNo) throws Exception {
		noticeDao.deleteNoticeAndFaq(noticeNo);
	}
	
}