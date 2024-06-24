package com.mapmory.services.timeline.dto;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SearchDto {
	private int searchCondition;
	private String userId;
	private String searchKeyword;
	private int categoryNo;
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
