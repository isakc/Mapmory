package com.mapmory.services.timeline.dto;

import java.sql.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class SearchDto {	
	private String userId;
	private int limit;
	private int offset;
	
	private Integer sharedType;
	private Integer tempType;
	private Integer timecapsuleType;
	private int radius;
	private Double minLatitude;
	private Double maxLatitude;
	private Double minLongitude;
	private Double maxLongitude;
	private Integer followType;
	private Integer privateType;
	
	private Date selectDate;
	private String checkpointTime;//06:00:00 형식으로
//	private List<String> userIdList;
}
