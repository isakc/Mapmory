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
public class SearchTransitRouter {
	private double startX;
    private double startY;
    private double endX;
    private double endY;
    private int lang; //국문 : 0(기본값), 영문 : 1
    private String format; //출력포맷 json
    private int count; // 응답 개수
}
