package com.mapmory.services.timeline.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class SummaryRecordDto {
	private int recordNo;
	private String checkpointAddress;
	private LocalDateTime checkpointDate;
	private String imageName;
}
