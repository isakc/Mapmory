package com.mapmory.services.community.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CommunityLogs {

	//Field
	private int communityLogsNo;
	private String userId;
	private int recordNo;
	private int replyNo;
	private int logsType;
	
	//Constructor
	
	//Method
}