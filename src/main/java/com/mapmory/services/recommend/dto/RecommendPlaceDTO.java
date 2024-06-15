package com.mapmory.services.recommend.dto;

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
public class RecommendPlaceDTO {
	private String placeName;
    private String distance;
    private String placeUrl;
    private String categoryName;
    private String addressName;
    private String phone;
    private double latitude;
    private double longitude;
}
