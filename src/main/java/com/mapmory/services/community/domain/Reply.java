package com.mapmory.services.community.domain;

import java.time.LocalDateTime;

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

public class Reply {
	
	//Field
	private int replyNo;
	private int recordNo;
	private String userId;
	private String nickname;
	private String profileImageName;
	private String subscriptionEndDate;
	private String replyText;
	private String replyImageName;
	private String replyDate;
	private String replyUpdateDate;
	private Integer likeCount;
	private Integer dislikeCount;
	private String recordTitle;
	
	//Constructor
	
	//Method
	
}