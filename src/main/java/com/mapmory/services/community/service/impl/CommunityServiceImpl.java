package com.mapmory.services.community.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.mapmory.common.domain.Search;
import com.mapmory.common.util.ContentFilterUtil;
import com.mapmory.common.util.TimelineUtil;
import com.mapmory.services.community.dao.CommunityDao;
import com.mapmory.services.community.domain.CommunityLogs;
import com.mapmory.services.community.domain.Reply;
import com.mapmory.services.community.domain.Report;
import com.mapmory.services.community.service.CommunityService;
import com.mapmory.services.timeline.dao.TimelineDao;
import com.mapmory.services.timeline.domain.Record;
import com.mapmory.services.timeline.dto.SharedRecordDto;
import com.mapmory.services.timeline.service.TimelineService;
import com.mapmory.services.user.domain.FollowBlock;

@Service("communityServiceImpl")
public class CommunityServiceImpl implements CommunityService {

	@Autowired
	@Qualifier("communityDao")
	private CommunityDao communityDao;
	
	@Autowired
	@Qualifier("timelineService")
	private TimelineService timelineService;
	
	@Autowired
	@Qualifier("timelineDao")
	private TimelineDao timelineDao;
	
	public void setCommunityDao(CommunityDao communityDao) {
		this.communityDao = communityDao;
	}
	
	public void setTimelineDao(TimelineDao timelineDao) {
		this.timelineDao = timelineDao;
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
		Reply newReply = Reply.builder()
				.recordNo(reply.getRecordNo())
				.userId(reply.getUserId())
				.replyText(reply.getReplyText())
				.replyImageName(reply.getReplyImageName())
				.build();
		
		if(ContentFilterUtil.checkBadWord(newReply.getReplyText())) {
			System.out.println("비속어 등록 불가능");
		} else {
			communityDao.addReply(newReply);	
		}
	}	

	@Override
	public Reply getReply(int replyNo) throws Exception {
//		int likeCount = communityDao.getReactionLikeTotalCount(replyNo);
//		int dislikeCount = communityDao.getReactionDisLikeTotalCount(replyNo);
//		map.put("likeCount", Integer.valueOf(likeCount));
//		map.put("dislikeCount", Integer.valueOf(dislikeCount));
		return communityDao.getReply(replyNo);
	}	
	
	@Override
	public void updateReply(Reply reply) throws Exception {
		reply.getReplyNo();
		communityDao.updateReply(reply);
	}

	@Override
	public void deleteReply(String userId, int replyNo) throws Exception {
		communityDao.deleteReply(userId, replyNo);
	}
	
	@Override
	public void addCommunityLogs(CommunityLogs communityLogs) throws Exception {
		communityDao.addCommunityLogs(communityLogs);
		
	}	
	
	@Override
	public CommunityLogs getCommunityLogs(int commmunityLogsNo) throws Exception {
		return communityDao.getCommunityLogs(commmunityLogsNo);
	}	
	
	@Override
	public void updateCommunityLogs(CommunityLogs communityLogs) throws Exception {
		communityDao.updateCommunityLogs(communityLogs);
	}	
	
	@Override
	public void deleteCommunityLogs(int communityLogsNo, String userId) throws Exception {
		communityDao.deleteCommunityLogs(communityLogsNo, userId);
	}

	@Override
	public Map<String, Object> getCommunityLogsList(Search search, String userId, int logsType) throws Exception {
		List<Object> list = communityDao.getCommunityLogsList(search, userId, logsType);
		
		Map<String, Object> map = new HashMap<>();
		map.put("list", list);
		return map;
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
	public Map<String, Object> getUserReportList(Search search, String userId) throws Exception {
		List<Object> list = communityDao.getUSerReportList(search, userId);
		int totalCount = communityDao.getUserReportTotalCount(search, userId);
		
		Map<String, Object> map = new HashMap<>();
		map.put("list", list);
		map.put("totalCount", Integer.valueOf(totalCount));

		return map;
	}

	@Override
	public Map<String, Object> getAdminReportList(Search search, int role) throws Exception {
		List<Object> list = communityDao.getAdminReportList(search, role);
		int totalCount = communityDao.getAdminReportTotalCount(search);
		int unConfirmCount = communityDao.getUnConfirmReportTotalCount(search);
		
		Map<String, Object> map = new HashMap<>();
		map.put("list", list);
		map.put("totalCount", Integer.valueOf(totalCount));
		map.put("unConfirmCount", Integer.valueOf(unConfirmCount));
		return map;
	}

	@Override
	public Report getReport(int reportNo) throws Exception {
		return communityDao.getReport(reportNo);
	}
	
	@Override
	public void confirmReport(int reportNo) throws Exception {
		communityDao.confirmReport(reportNo);
	}

	@Override
	public void addBlockUser(FollowBlock followBlock) throws Exception {
		communityDao.addBlockUser(followBlock);
	}

	@Override
	public Map<String, Object> getBlockedList(Search search, String userId) throws Exception {
		List<Object> list = communityDao.getBlockedList(search, userId);
		int totalCount = communityDao.getBlockedTotalCount(search, userId);
		
		Map<String, Object> map = new HashMap<>();
		map.put("list", list);
		map.put("totalCount", Integer.valueOf(totalCount));
		return map;
	}

	@Override
	public FollowBlock getBlockedUser(String userId, String targetId) throws Exception {
		return communityDao.getBlockedUser(userId, targetId);
	}	
	
	@Override
	public void deleteBlockedUser(String userId, String targetId) throws Exception {
		communityDao.deleteBlockedUser(userId, targetId);
		
	}
}
