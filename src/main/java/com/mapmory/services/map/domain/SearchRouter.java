package com.mapmory.services.map.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SearchRouter {
	private double startX;
    private double startY;
    private double endX;
    private double endY;
    private String reqCoordType;
    private String resCoordType;
    private String startName;
    private String endName;
}
