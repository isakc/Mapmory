package com.mapmory.services.user.domain;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SuspensionLogList {

	private String userId;
	private List<SuspensionDetail> suspensionDetailList;
}
