package com.mapmory.services.timeline.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SummaryRecordDto {
	private int recordNo;
	private String recordUserId;
	private String checkpointAddress;
	private String checkpointDate;
	private String mediaName;
	private String imageName;
}
