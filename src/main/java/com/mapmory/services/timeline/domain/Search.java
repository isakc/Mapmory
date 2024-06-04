package com.mapmory.services.timeline.domain;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Search {
	private int currentPage;
	private int limit;
	private int offset;
	
	private Integer sharedType;
	private Integer tempType;
	private Integer timecapsuleType;
	private String userId;
	private LocalDateTime selectDay1;
	private LocalDateTime selectDay2;

	public int getOffset() {
		return (getCurrentPage() - 1) * getLimit();
	}
}