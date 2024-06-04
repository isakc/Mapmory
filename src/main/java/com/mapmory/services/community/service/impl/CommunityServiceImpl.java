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
		int totalCount = communityDao.getReplyTotalCount(search, recordNo);
		
		Map<String, Object> map = new HashMap<>();
		map.put("list", list);
		map.put("totalCount", Integer.valueOf(totalCount));
		return map;
	}

	@Override
	public Map<String, Object> getUserReplyList(Search search, String userId) throws Exception {
		List<Object> list = communityDao.getUserReplyList(search, userId);
		int totalCount = communityDao.getReplyUserTotalCount(search, userId);
		
		Map<String, Object> map = new HashMap<>();
		map.put("list", list);
		map.put("totalCount", Integer.valueOf(totalCount));
		return map;
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
	public void deleteReply(String userId, int replyNo) throws Exception {
		communityDao.deleteReply(userId, replyNo);
	}

	@Override
	public void addBookmarkSharedRecord(CommunityLogs communityLogs) throws Exception {
		communityDao.addBookmarkSharedRecord(communityLogs);
	}	

	@Override
	public void deleteBookmarkSharedRecord(String userId, int recordNo) throws Exception {
		communityDao.deleteBookmarkSharedRecord(userId, recordNo);
	}

	@Override
	public Map<String, Object> getBookmarkSharedRecordList(Search search, String userId) throws Exception {
		return null;
	}	
	
	@Override
	public void addReaction(CommunityLogs communityLogs) throws Exception {
		communityDao.addReaction(communityLogs);
	}

	@Override
	public void updateReaction(CommunityLogs communityLogs) throws Exception {
		communityDao.updateReaction(communityLogs);
	}

	@Override
	public void deleteReaction(CommunityLogs communityLogs) throws Exception {
		communityDao.deleteReaction(communityLogs);
	}

//	@Override
//	public void addReactionReply(CommunityLogs communityLogs, String userId, int recordNo, int replyNo, int logsType) throws Exception {
//		communityDao.addReactionReply(communityLogs, userId, recordNo, replyNo, logsType);
//	}
//
//	@Override
//	public void updateReactionReply(CommunityLogs communityLogs, String userId, int recordNo, int replyNo, int logsType) throws Exception {
//		communityDao.updateReactionReply(communityLogs, userId, recordNo, replyNo, logsType);
//		
//	}
//
//	@Override
//	public void deleteReactionReply(CommunityLogs communityLogs, String userId, int recordNo, int replyNo, int logsType) throws Exception {
//		communityDao.deleteReactionSharedRecord(communityLogs, userId, recordNo, logsType);
//		
//	}	

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
