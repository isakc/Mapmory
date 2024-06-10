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
public class ResultTransitRouter {
	private int totalFare; // 총 요금
	private int totalTime; // 총 시간
	private int totalDistance; // 총 거리
	private int totalWalkTime; // 도보 총 시간
	private int transferCount; // 환승횟수
	private int pathType; // 1 –지하철, 2 – 버스, 3 – 버스+지하철, 4 – 고속/시외버스, 5 – 기차, 6 – 항공, 7 – 해운
	private List<ResultDetailTransitRouter> routeList;
}
