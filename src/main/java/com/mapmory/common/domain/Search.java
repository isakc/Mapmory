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
	private int pageSize;
	private int endRowNum;
	private int startRowNum;	
	
	public int getEndRowNum() {
		return getCurrentPage() * getPageSize();
	}
	
	public int getStartRowNum() {
		return (getCurrentPage()-1) * getPageSize()+1;
	}
}
