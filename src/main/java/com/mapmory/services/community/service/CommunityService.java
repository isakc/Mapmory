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
	public List<Object> getUserReplyList(Search search, String userId) throws Exception;
	
	//댓글 작성
	public void addReply(Reply reply) throws Exception;

	//댓글 선택
	public Reply getReply(int replyNo) throws Exception;
	
	//댓글 수정
	public void updateReply(Reply reply) throws Exception;
	
	//댓글 삭제
	public void deleteReply(Reply reply) throws Exception;
	
	//기록 즐겨찾기 추가 및 해제
	public void updateBookmarkSharedRecord(CommunityLogs communityLogs, String userId) throws Exception;
	
	//기록 감정표현 추가, 수정, 삭제
	public void updateReactionSharedRecord(CommunityLogs communityLogs, String userId) throws Exception;
	
	//댓글 감정표현 추가, 수정, 삭제
	public void updateReactionReply(CommunityLogs communityLogs, String userId) throws Exception;
	
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