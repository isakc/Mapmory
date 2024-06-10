package com.mapmory.services.timeline.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class CountAddressDto {
	private int checkpointCount;
	private String checkpointDate;
}
