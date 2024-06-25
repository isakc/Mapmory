package com.mapmory.services.timeline.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CountAddressDto {
	private int checkpointCount;
	private String checkpointDate;
}
