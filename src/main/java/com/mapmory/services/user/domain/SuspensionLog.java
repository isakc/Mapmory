package com.mapmory.services.user.domain;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class SuspensionLog {

	private String userId;
	private List<LocalDateTime> startSuspensionDate;
	private List<String> reason;
}
