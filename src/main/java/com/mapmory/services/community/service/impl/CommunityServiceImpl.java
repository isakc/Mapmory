package com.mapmory.services.community.service.impl;

import java.util.Map;

import com.mapmory.common.domain.Search;
import com.mapmory.services.community.domain.CommunityLogs;
import com.mapmory.services.community.domain.Reply;
import com.mapmory.services.community.domain.Report;
import com.mapmory.services.community.service.CommunityService;

public class CommunityServiceImpl implements CommunityService {

	@Override
	public void updateBookmarkSharedRecord(CommunityLogs communityLogs) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateReactionSharedRecord(CommunityLogs communityLogs) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateReactionReply(CommunityLogs communityLogs) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, Object> getReplyList(Search search, int recordNo) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addReply(Reply reply) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateReply(Reply reply) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteReply(Reply reply) throws Exception {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doReport(Report report) throws Exception {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addBlockUser(String userId) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, Object> getBlockedList(Search search, String userId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteBlockUser(String userId) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
