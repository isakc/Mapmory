package com.mapmory.services.timeline.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ImageTag {
	private int imageTagOrder;
	private int recordNo;
	private int imageTagType;
	private String imageTagText;
	
	private String imageTagByte;
}
