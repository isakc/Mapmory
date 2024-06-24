package com.mapmory.services.user.domain;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * 월간 통계용 :: year, month
 * 일간 통계용 :: selectLoginDate
 * @author rlaeo
 *
 */
@Getter
@Setter
@ToString
@Builder
// @SuperBuilder
public class LoginSearch {

	// 월간 통계 용
//	private int year;
//	private int month;
	
	private String year;
	private String month;
	
	
	// 일간 통계 용
	private LocalDate selectLoginDate;
}