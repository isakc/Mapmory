package com.mapmory.services.timeline.domain;

import java.util.List;

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
//	private List<String> userIdList;
}
