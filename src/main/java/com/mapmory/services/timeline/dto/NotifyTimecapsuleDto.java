package com.mapmory.services.timeline.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class NotifyTimecapsuleDto {
	private String userId;
	private int timecapsulCount;
	private String userPhoneNumber;

}
