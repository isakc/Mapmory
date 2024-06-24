package com.mapmory.services.timeline.domain;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SharedRecord {
	private int recordNo;
	private String recordUserId;
	private String recordTitle;
	private String recordText;
	private Double latitude;
	private Double longitude;
	private String checkpointAddress;
	private String checkpointDate;
	private String mediaName;
	private List<Map<String,Object>> imageTagList;
	private List<ImageTag> imageName;
	private List<ImageTag> hashtag;
	private int categoryNo;
	private int tempType;
	private String recordAddDate;
	private String sharedDate;
	private int updateCount;
	private Date d_DayDate;
	private int timecapsuleType;
	
	private String nickname;
	private String profileImageName;
	private long subscriptionEndDate;
	private String categoryName;
	private String categoryImoji;
	private long bookmark;
	private long likeCount;
	private long dislikeCount;
	private long replyCount;
	private long logsCount;
}
