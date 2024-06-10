package com.mapmory.services.timeline.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ImageTagDto {
	private int imageTagOrder;
	private int imageTagType;
	private String imageTagText;
}
