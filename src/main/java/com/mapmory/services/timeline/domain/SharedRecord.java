package com.mapmory.services.timeline.domain;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class SharedRecord {
	private int recordNo;
	private String recordTitle;
	private LocalDateTime sharedDate;
	private int updateCount;
	private String nickname;
	private String profileImageName;
	private boolean subscriptionEndDate;
	private String imageTagType;
	private String imageTagText;
	private String categoryName;
	private String categoryImoji;
	private int replyCount;
	private int logsCount;
}
