package com.mapmory.services.timeline.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Search {
	private int currentPage;
	private int limit;
	private int offset;
	
	private int sharedType;
	private int tempType;
	private int timecapsulType;

	public int getOffset() {
		return (getCurrentPage() - 1) * getLimit();
	}
}