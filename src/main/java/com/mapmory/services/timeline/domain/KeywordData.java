package com.mapmory.services.timeline.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class KeywordData {
	private int keywordNo;
	private String keywordUserId;
	private String keyword;
	private int keywordCount;
}
