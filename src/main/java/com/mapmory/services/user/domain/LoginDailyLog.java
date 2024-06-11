package com.mapmory.services.user.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoginDailyLog {

	private int hour;
	private int count;
}