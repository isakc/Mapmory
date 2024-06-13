package com.mapmory.common.domain;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
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
	private int limit;
	private int offset;
	
	private Integer sharedType;
	private Integer tempType;
	private Integer timecapsuleType;
	private Date selectDate;
	private String selectDay1;
	private String selectDay2;

	private int radius;
	private Double latitude;
	private Double longitude;
	private Integer followType;
	private Integer privateType;

	public void setPageSize(int paseSize) {
		this.pageSize = paseSize;
	}
	
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	
	public int getOffset() {
		return (getCurrentPage() - 1) * getPageSize();
	}
	public void setOffset(int offset) {
        this.offset = offset;
    }
	public int getEndRowNum() {
		return getCurrentPage() * getPageSize();
	}
	
	public int getStartRowNum() {
		return (getCurrentPage()-1) * getPageSize()+1;
	}
}
