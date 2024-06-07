package com.mapmory.services.timeline.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class NotifyTimecapsule {
	private String userId;
	private int timecapsulCount;
	private String userPhoneNumber;

}
