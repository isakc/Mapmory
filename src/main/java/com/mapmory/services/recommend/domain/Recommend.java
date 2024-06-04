package com.mapmory.services.recommend.domain;

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
public class Recommend {

	private String userId;
	private int recordNo;
	private String recordTitle;
	private String category;
	private String hashTag;
	private int positive;
	private long timeStamp;

}
