package com.mapmory.services.user.domain;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SuspensionDetail {
	
	private int logNo;
	private LocalDateTime startSuspensionDate;
	private String reason;
	
	public SuspensionDetail() {
		
	}
	
	public SuspensionDetail(LocalDateTime startSuspensionDate, String reason) {
		this.startSuspensionDate = startSuspensionDate;
		this.reason = reason;
	}
}
