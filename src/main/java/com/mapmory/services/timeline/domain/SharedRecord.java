package com.mapmory.services.timeline.domain;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class SharedRecord {
	private int recordNo;
	private String recordUserId;
	private String recordTitle;
	private Double latitude;
	private Double longitude;
	private String checkpointAddress;
	private LocalDateTime checkpointDate;
	private String mediaName;
	private List<Map<String,Object>> imageTagList;
	private List<ImageTag> imageName;
	private List<ImageTag> hashtag;
	private int categoryNo;
	private String recordText;
	private int tempType;
	private LocalDateTime recordAddDate;
	private LocalDateTime sharedDate;
	private int updateCount;
	private Date d_DayDate;
	private int timecapsuleType;
	
	private String nickname;
	private String profileImageName;
	private long subscriptionEndDate;
}
