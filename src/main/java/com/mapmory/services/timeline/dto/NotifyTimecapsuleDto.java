package com.mapmory.services.timeline.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class NotifyTimecapsuleDto {
	private String userId;
	private int timecapsulCount;
	private String userPhoneNumber;

}
