package com.mapmory.services.community.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mapmory.common.domain.Search;
import com.mapmory.services.community.dao.CommunityDao;
import com.mapmory.services.community.domain.CommunityLogs;
import com.mapmory.services.community.domain.Reply;
import com.mapmory.services.community.domain.Report;
import com.mapmory.services.community.service.CommunityService;

@Service("communityServiceImpl")
public class CommunityServiceImpl implements CommunityService {

	@Autowired
	@Qualifier("communityDao")
	private CommunityDao communityDao;
	
	public void setCommunityDao(CommunityDao communityDao) {
		this.communityDao = communityDao;
	}
	
	public CommunityServiceImpl() {
		System.out.println(this.getClass());
	}
	
	@Override
	public Map<String, Object> getReplyList(Search search, int recordNo) throws Exception {
		List<Object> list = communityDao.getReplyList(search, recordNo);
		int totalCount = communityDao.getReplyTotalCount(search);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", list);
		map.put("totalCount", new Integer(totalCount));
		return map;
	}

	@Override
	public List<Object> getUserReplyList(Search search, String userId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void addReply(Reply reply) throws Exception {
		communityDao.addReply(reply);
	}

	@Override
	public Reply getReply(int replyNo) throws Exception {
		return communityDao.getReply(replyNo);
	}	
	
	@Override
	public void updateReply(Reply reply) throws Exception {
		communityDao.updateReply(reply);
	}

	@Override
	public void deleteReply(Reply reply) throws Exception {
		communityDao.deleteReply(reply);
		
	}

	@Override
	public void updateBookmarkSharedRecord(CommunityLogs communityLogs, String userId) throws Exception {

	}

	@Override
	public void updateReactionSharedRecord(CommunityLogs communityLogs, String userId) throws Exception {
		
	}

	@Override
	public void updateReactionReply(CommunityLogs communityLogs, String userId) throws Exception {

	}

	@Override
	public Map<String, Object> getBookMarkSharedRecordList(Search search, String userId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getReactionList(Search search, String userId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addReport(Report report) throws Exception {
		communityDao.addReport(report);
	}

	@Override
	public void doReport(Report report) throws Exception {
		communityDao.doReport(report);
	}

	@Override
	public Map<String, Object> getUSerReportList(Search search, String userId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getAdminReportList(Search search, String userId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void confirmReport(Search search, String userId) throws Exception {
		communityDao.confirmReport(search, userId);
	}

	@Override
	public void addBlockUser(String userId) throws Exception {
		communityDao.addBlockUser(userId);
	}

	@Override
	public Map<String, Object> getBlockedList(Search search, String userId) throws Exception {
		return null;
	}

	@Override
	public void deleteBlockUser(String userId) throws Exception {
		communityDao.deleteBlockUser(userId);
		
	}

}
