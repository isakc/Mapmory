package com.mapmory.services.timeline.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor

public class SharedRecordDto {
	private int recordNo;
	private String recordUserId;
	private String recordTitle;
	private String recordText;
	private LocalDateTime sharedDate;
	private int updateCount;
	private String nickname;
	private String profileImageName;
	private long subscriptionEndDate;
	private String imageTagType;
	private String imageTagText;
	private int categoryNo;
	private String categoryName;
	private String categoryImoji;
	private int replyCount;
	private int logsCount;
}
