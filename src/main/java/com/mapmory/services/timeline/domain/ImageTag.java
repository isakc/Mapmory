package com.mapmory.services.timeline.domain;


import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ImageTag {
	private int imageTagOrder;
	private int recordNo;
	private int imageTagType;
	private String imageTagText;
}
