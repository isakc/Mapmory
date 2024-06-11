package com.mapmory.services.user.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoginMonthlyLog {
	
	private int day;
	private int count;
}
