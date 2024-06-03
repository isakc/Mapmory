package com.mapmory.services.timeline.domain;


import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ImageTagDto {
	private int imageTagOrder;
	private String imageTagText;
}
