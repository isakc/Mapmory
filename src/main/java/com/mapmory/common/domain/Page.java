package com.mapmory.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Page {
	private int currentPage;
	private int totalCount;
	private int pageSize;
	private int pageUnit;
	private int maxPage;
	private int beginUnitPage;
	private int endUnitPage;
}
