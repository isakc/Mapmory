package com.mapmory.services.timeline.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class SharedRecordDto {
	private int recordNo;
	private String recordUserId;
	private String recordTitle;
	private LocalDateTime sharedDate;
	private int updateCount;
	private String nickname;
	private String profileImageName;
	private long subscriptionEndDate;
	private String imageTagType;
	private String imageTagText;
	private String categoryName;
	private String categoryImoji;
	private int replyCount;
	private int logsCount;
}
