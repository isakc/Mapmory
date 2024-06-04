package com.mapmory.services.community.service;


import java.util.List;
import java.util.Map;

import com.mapmory.common.domain.Search;
import com.mapmory.services.community.domain.CommunityLogs;
import com.mapmory.services.community.domain.Reply;
import com.mapmory.services.community.domain.Report;

public interface CommunityService {
	
	//기록의 댓글 목록 조회
	public Map<String, Object> getReplyList(Search search, int recordNo) throws Exception;
	
	//내가 쓴 댓글 목록 조회
	public Map<String, Object> getUserReplyList(Search search, String userId) throws Exception;
	
	//댓글 작성
	public void addReply(Reply reply) throws Exception;

	//댓글 선택
	public Reply getReply(int replyNo) throws Exception;
	
	//댓글 수정
	public void updateReply(Reply reply) throws Exception;
	
	//댓글 삭제
	public void deleteReply(String userId, int replyNo) throws Exception;
	
	//기록 즐겨찾기 추가
	public void addBookmarkSharedRecord(CommunityLogs communityLogs) throws Exception;	
	
	//기록 즐겨찾기 해제
	public void deleteBookmarkSharedRecord(String userId, int recordNo) throws Exception;
	
	// 기록 및 댓글에 대한 감정 표현 추가
	public void addReaction(CommunityLogs communityLogs) throws Exception;    
	    
	// 기록 및 댓글에 대한 감정 표현 수정
	public void updateReaction(CommunityLogs communityLogs) throws Exception;
	
	// 기록 및 댓글에 대한 감정 표현 삭제
	public void deleteReaction(CommunityLogs communityLogs) throws Exception;	
	
//	// 댓글에 대한 감정 표현 추가
//	public void addReactionReply(CommunityLogs communityLogs, String userId, int recordNo, int replyNo, int logsType) throws Exception;    
//
//	// 댓글에 대한 감정 표현 수정
//	public void updateReactionReply(CommunityLogs communityLogs, String userId, int recordNo, int replyNo, int logsType) throws Exception;
//
//	// 댓글에 대한 감정 표현 삭제
//	public void deleteReactionReply(CommunityLogs communityLogs, String userId, int recordNo, int replyNo, int logsType) throws Exception;
	
	//즐겨찾기 목록 조회
	public Map<String, Object> getBookMarkSharedRecordList(Search search, String userId) throws Exception;
	
	//감정표현 목록 조회
	public Map<String, Object> getReactionList(Search search, String userId) throws Exception;
	
	//신고하기 이동
	public void addReport(Report report) throws Exception;
	
	//신고하기 제출
	public void doReport(Report report) throws Exception;
	
	//신고 목록 조회 
	public Map<String, Object> getUSerReportList(Search search, String userId) throws Exception;
	
	//전체 신고 목록 조회(관리자)
	public Map<String, Object> getAdminReportList(Search search, String userId) throws Exception;
	
	//신고 처리(관리자)
	public void confirmReport(Search search, String userId) throws Exception;
		
	//사용자 차단
	public void addBlockUser(String userId) throws Exception;
	
	//차단한 사용자 목록 조회
	public Map<String, Object> getBlockedList(Search search, String userId) throws Exception;
	
	//사용자 차단해제
	public void deleteBlockUser(String userId) throws Exception;
		
}