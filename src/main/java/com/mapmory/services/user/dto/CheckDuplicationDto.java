package com.mapmory.services.user.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CheckDuplicationDto {

	private int type;  // 0: id, 1: nickname
	private String value;
}
