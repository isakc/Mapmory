package com.mapmory.services.timeline.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Search {
	private int currentPage;
	private int limit;
	private int offset;
	
	private String sharedType;
	private String tempType;
	private String timecapsuleType;

	public int getOffset() {
		return (getCurrentPage() - 1) * getLimit();
	}
}