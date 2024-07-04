package com.mapmory.services.community.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mapmory.common.domain.Search;
import com.mapmory.common.util.ContentFilterUtil;
import com.mapmory.common.util.RedisUtil;
import com.mapmory.services.community.dao.CommunityDao;
import com.mapmory.services.community.domain.CommunityLogs;
import com.mapmory.services.community.domain.Reply;
import com.mapmory.services.community.domain.Report;
import com.mapmory.services.community.service.CommunityService;
import com.mapmory.services.user.domain.FollowBlock;

@Service("communityServiceImpl")
public class CommunityServiceImpl implements CommunityService {

	@Autowired
	@Qualifier("communityDao")
	private CommunityDao communityDao;
	
	@Autowired
	private ContentFilterUtil contentFIlterUtil;
	
	@Value("{page.unit}")
	private String pageUnit;
	
	@Value("${page.size}")
	private String pageSize;
	
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
		Reply newReply = Reply.builder()
				.recordNo(reply.getRecordNo())
				.userId(reply.getUserId())
				.replyText(reply.getReplyText())
				.replyImageName(reply.getReplyImageName())
				.build();

		if(contentFIlterUtil.checkBadWord(newReply.getReplyText())) {
			throw new Exception("비속어가 포함된 댓글은 등록할 수 없음");
		} else {
			communityDao.addReply(newReply);	
		}
	}	

	@Override
	public Reply getReply(int replyNo) throws Exception {
		return communityDao.getReply(replyNo);
	}	
	
	@Override
	public void updateReply(Reply reply) throws Exception {
		Reply newReply = Reply.builder()
				.recordNo(reply.getRecordNo())
				.replyNo(reply.getReplyNo())
				.userId(reply.getUserId())
				.replyText(reply.getReplyText())
				.replyImageName(reply.getReplyImageName())
				.build();
	
		if(contentFIlterUtil.checkBadWord(newReply.getReplyText())) {
			throw new Exception("비속어가 포함된 댓글은 등록할 수 없음");
		} else {
			communityDao.updateReply(newReply);	
		}
	}

	@Override
	public void deleteReply(String userId, int replyNo) throws Exception {
		communityDao.deleteReply(userId, replyNo);
	}
	
	@Override
	public void addCommunityLogs(CommunityLogs communityLogs) throws Exception {
		
		int count = communityDao.checkDuplicateLogs(communityLogs.getUserId(), communityLogs.getRecordNo(), communityLogs.getReplyNo(), communityLogs.getLogsType());
		if(count ==0) {
			CommunityLogs newCommunityLogs = CommunityLogs.builder()
					.userId(communityLogs.getUserId())
					.recordNo(communityLogs.getRecordNo())
					.replyNo(communityLogs.getReplyNo())
					.logsType(communityLogs.getLogsType())
					.build();
			communityDao.addCommunityLogs(newCommunityLogs);		
		}
	}	
	
	@Override
	public void checkLog(CommunityLogs communityLogs) throws Exception {
		
		int logCount = communityDao.checkDuplicateLogs(communityLogs.getUserId(), communityLogs.getRecordNo(), communityLogs.getReplyNo(), communityLogs.getLogsType());
		
		if(logCount >0) {
			if(communityLogs.getLogsType() !=0) {
				communityDao.deleteCommunityLogs(communityLogs);
			}
		} else {
			communityDao.addCommunityLogs(communityLogs);
		}
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
	public void deleteCommunityLogs(CommunityLogs communityLogs) throws Exception {
		communityDao.deleteCommunityLogs(communityLogs);
	}

	@Override
	public Map<String, Object> getCommunityLogsList(Search search, CommunityLogs communityLogs) throws Exception {
		List<Object> list = communityDao.getCommunityLogsList(search, communityLogs);
		
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

		search.setOffset((search.getCurrentPage()-1) * search.getPageSize());
		search.setPageSize(search.getPageSize());
		
		List<Object> list = communityDao.getUserReportList(search, userId);
		int totalCount = communityDao.getUserReportTotalCount(search, userId);
				
		Map<String, Object> map = new HashMap<>();
		map.put("list", list);
		map.put("totalCount", Integer.valueOf(totalCount));

		return map;
	}

	@Override
	public Map<String, Object> getAdminReportList(Search search) throws Exception {
		
		int offset = (search.getCurrentPage() - 1) * search.getPageSize();
	    search.setOffset(offset);
	    search.setLimit(search.getPageSize());	
		
		List<Object> list = communityDao.getAdminReportList(search);
		int totalCount = communityDao.getAdminReportTotalCount(search);
		int unConfirmCount = communityDao.getUnConfirmReportTotalCount(search);
		
		Map<String, Object> map = new HashMap<>();
		map.put("list", list);
		map.put("totalCount", Integer.valueOf(totalCount));
		map.put("unConfirmCount", Integer.valueOf(unConfirmCount));
		return map;
	}

	@Override
	public Map<String, Object> getUnConfirmReportList(Search search) throws Exception {

		int offset = (search.getCurrentPage() - 1) * search.getPageSize();
	    search.setOffset(offset);
	    search.setLimit(search.getPageSize());			
		
	    List<Object> list = communityDao.getUnConfirmReportList(search);
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
	public void confirmReport(Report report) throws Exception {
		communityDao.confirmReport(report);
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
	public void updateBlockUser(FollowBlock followBlock) throws Exception {
		communityDao.updateBlockUser(followBlock);
	}	

	@Override
	public FollowBlock getBlockedUser(String userId, String targetId) throws Exception {
		return communityDao.getBlockedUser(userId, targetId);
	}	
	
	@Override
	public void deleteBlockedUser(String userId, String targetId) throws Exception {
		communityDao.deleteBlockedUser(userId, targetId);
		
	}

	@Override
	public Map<String, Object> getReplyLikeList(Search search, String userId) throws Exception {
		List<Object> list = communityDao.getReplyLikeList(search, userId);
		Map<String, Object> map = new HashMap<>();
		map.put("list", list);
		return map;
	}

	@Override
	public List<CommunityLogs> getUsersLogs(String userId, int recordNo) throws Exception {
		return communityDao.getUsersLogs(userId, recordNo);
	}

	@Override
	public void toggleCommunityLogs(CommunityLogs communityLogs) throws Exception {
		communityDao.deleteCommunityLogs(communityLogs);
	}

}
