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
public class ResultRouter {
	private double totalDistance;
    private double totalTime;
    private List<Double> lat;
    private List<Double> lon;
    private List<String> description;
}
