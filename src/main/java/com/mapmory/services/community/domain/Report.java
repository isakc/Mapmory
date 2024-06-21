package com.mapmory.services.community.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Report {

	//Field
	private int reportNo;
	private String userId;
	private String targetUserId;
	private Integer recordNo;
	private Integer replyNo;
	private Integer chatroomNo;
	private String reportTitle;
	private String reportText;
	private String reportDate;
	private int reportStatus;
	private int reportResult;
	private String nickname;
	
	//Constructor
	
	//Method
}