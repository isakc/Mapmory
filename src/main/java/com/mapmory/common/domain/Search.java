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
public class Search {
	private int searchCondition;
	private String searchKeyword;
	private String userId;
	private int categoryNo;
	private int currentPage;
}
