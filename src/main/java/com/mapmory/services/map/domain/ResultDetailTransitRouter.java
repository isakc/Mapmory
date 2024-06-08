package com.mapmory.services.map.domain;

import java.util.List;

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
public class ResultDetailTransitRouter {
	private String mode; // walk, bus,...
	private String route; //버스 번호
	private String startName; //출발지 이름
	private double startLat; //출발지 위도
	private double startLon; //출발지 경도
	private String endName; //도착지 이름
	private double endLat; //도착지 위도
	private double endLon; //도착지 경도
	private List<Double> lineStringLat; // 경로 위도,경도 [0] 위도, [1] 경도
	private List<Double> lineStringLon; // 경로 위도,경도 [0] 위도, [1] 경도
}
