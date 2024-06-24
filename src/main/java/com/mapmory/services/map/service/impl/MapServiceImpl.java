package com.mapmory.services.map.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.mapmory.services.map.domain.ResultDetailTransitRouter;
import com.mapmory.services.map.domain.ResultRouter;
import com.mapmory.services.map.domain.ResultTransitRouter;
import com.mapmory.services.map.domain.SearchRouter;
import com.mapmory.services.map.domain.SearchTransitRouter;
import com.mapmory.services.map.service.MapService;

@Service("mapServiceImpl")
public class MapServiceImpl implements MapService {

	///// Field /////
	
	@Value("${tmap.apiKey}")
	private String tmapAPIKey;

	@Value("${tmap.pedestrian.URL}")
	private String tmapPedestrianURL;

	@Value("${tmap.car.URL}")
	private String tmapCarURL;

	@Value("${tmap.transit.URL}")
	private String tmapTransitURL;
	
	/// Constructor /////
	
	/// Method /////
	
	@Override
	public ResultRouter getPedestrianRoute(SearchRouter searchRouter) throws Exception {
		String resultJson = getRouteResultJson(searchRouter, tmapPedestrianURL);
		
		ObjectMapper objectMapper = new ObjectMapper();
	    JsonNode rootNode = objectMapper.readTree(resultJson);
	    System.out.println(resultJson);
	    JsonNode resultData = rootNode.path("features");
	    
	    List<Double> latituedeList = new ArrayList<Double>();
	    List<Double> longitudeList = new ArrayList<Double>();
	    List<String> description = new ArrayList<String>();
	    
	    if (resultData.isArray()) {
            for (JsonNode item : resultData) {
                JsonNode geometry = item.path("geometry");
                if (geometry.path("type").asText().equals("LineString") ) {
                	
                    JsonNode coordinates = geometry.path("coordinates");
                    if (coordinates.isArray()) {
                    	
                        for (JsonNode coord : coordinates) {
                            latituedeList.add(coord.get(1).asDouble());
                            longitudeList.add(coord.get(0).asDouble());
                        }//for end
                        
                    }//if
                } else {
                	description.add(item.path("properties").path("description").asText());
                }//if~else LineString이면 lat, lng
                
            }//for
        }//만약 데이터가 있는 경우

	    ResultRouter resultRouter = ResultRouter.builder()
	    							.totalDistance(resultData.get(0).path("properties").path("totalDistance").asDouble())
	    							.totalTime(resultData.get(0).path("properties").path("totalTime").asDouble())
	    							.lat(latituedeList)
	    							.lon(longitudeList)
	    							.description(description)
	    							.build();
	    
		return resultRouter;
	}// getPedestrianRoute: 도보 경로찾기
	
	@Override
	public ResultRouter getCarRoute(SearchRouter searchRouter) throws Exception {
		String resultJson = getRouteResultJson(searchRouter, tmapCarURL);
		
		ObjectMapper objectMapper = new ObjectMapper();
	    JsonNode rootNode = objectMapper.readTree(resultJson);
	    JsonNode resultData = rootNode.path("features");
	    
	    List<Double> latituedeList = new ArrayList<Double>();
	    List<Double> longitudeList = new ArrayList<Double>();
	    List<String> description = new ArrayList<String>();
	    
	    if (resultData.isArray()) {
            for (JsonNode item : resultData) {
                JsonNode geometry = item.path("geometry");
                if (geometry.path("type").asText().equals("LineString") ) {
                	
                    JsonNode coordinates = geometry.path("coordinates");
                    if (coordinates.isArray()) {
                    	
                        for (JsonNode coord : coordinates) {
                            latituedeList.add(coord.get(1).asDouble());
                            longitudeList.add(coord.get(0).asDouble());
                        }//for end
                        
                    }//if
                } else {
                	description.add(item.path("properties").path("description").asText());
                }//if~else LineString이면 lat, lng
                
            }//for
        }//만약 데이터가 있는 경우

	    ResultRouter resultRouter = ResultRouter.builder()
	    							.totalDistance(resultData.get(0).path("properties").path("totalDistance").asDouble())
	    							.totalTime(resultData.get(0).path("properties").path("totalTime").asDouble())
	    							.lat(latituedeList)
	    							.lon(longitudeList)
	    							.description(description)
	    							.build();
	    
		return resultRouter;
	}// getCarRoute: 자동차 경로찾기

	@Override
	public List<ResultTransitRouter> getTransitRoute(SearchTransitRouter searchTransitRouter) throws Exception {
		String resultJson = getRouteResultJson(searchTransitRouter, tmapTransitURL);
	    System.out.println(resultJson);
		List<ResultTransitRouter> ResultRouterList = new ArrayList<ResultTransitRouter>(); // 대중교통 경로 리스트
		
		ObjectMapper objectMapper = new ObjectMapper();
	    JsonNode rootNode = objectMapper.readTree(resultJson);
	    JsonNode itineraries = rootNode.path("metaData").path("plan").path("itineraries");
	    
	    for(JsonNode item : itineraries) {
	    	List<ResultDetailTransitRouter> routeDetailList = new ArrayList<ResultDetailTransitRouter>(); // 방법 중 단계를 담는 List
	    	
	    	JsonNode legs = item.path("legs");
	    	for(JsonNode leg : legs) {
		    	ResultDetailTransitRouter resultDetailTransitRouter = ResultDetailTransitRouter.builder()
		    			.mode(leg.path("mode").asText())
		    			.route(leg.path("route").asText())
		    			.startName(leg.path("start").path("name").asText())
		    			.startLat(leg.path("start").path("lat").asDouble())
		    			.startLon(leg.path("start").path("lon").asDouble())
		    			.endName(leg.path("end").path("name").asText())
		    			.endLat(leg.path("end").path("lat").asDouble())
		    			.endLon(leg.path("end").path("lon").asDouble())
		    			.build(); // 단계들

    			List<Double> lineStringLat = new ArrayList<Double>();
    			List<Double> lineStringLon = new ArrayList<Double>();
		    	
    			if(resultDetailTransitRouter.getMode().equals("WALK")) {

    		    	JsonNode steps = leg.path("steps");
    		    	
    		    	if(steps.size() == 0) {
    		    		JsonNode lineStrings = leg.path("passShape").path("linestring");
    		    		
    		    		String[] lineStringArray = lineStrings.asText().split(" "); //lineString 파싱
    		    		
        				for (String lineString : lineStringArray) {
        					String[] coordinates = lineString.split(",");
        		    	            
        					lineStringLon.add(Double.parseDouble(coordinates[0])); 
        					lineStringLat.add(Double.parseDouble(coordinates[1])); 
        		    	      
        				}// 위도, 경도 나누기
    		    	}else {
    		    	
    		    	for(JsonNode step : steps) {
    		    		String[] lineStringArray = step.path("linestring").asText().split(" "); //lineString 파싱
    		    		
    		    		 for (String lineString : lineStringArray) {
    		    	            String[] coordinates = lineString.split(",");
    		    	            
    		    	            lineStringLon.add(Double.parseDouble(coordinates[0])); 
    		    	            lineStringLat.add(Double.parseDouble(coordinates[1])); 
    		    	            
    		    	      }// 위도, 경도 나누기
    		    		
    		    	}//step 중 " " 로 lineString 파싱
    		    	
    		    	}
    			//}else if(resultDetailTransitRouter.getMode().equals("BUS") || resultDetailTransitRouter.getMode().equals("SUBWAY")) {
    			}else {
    				JsonNode lineStrings = leg.path("passShape").path("linestring");
    		    	
    				String[] lineStringArray = lineStrings.asText().split(" "); //lineString 파싱
    		    		
    				for (String lineString : lineStringArray) {
    					String[] coordinates = lineString.split(",");
    		    	            
    					lineStringLon.add(Double.parseDouble(coordinates[0])); 
    					lineStringLat.add(Double.parseDouble(coordinates[1])); 
    		    	      
    				}// 위도, 경도 나누기
    			}
		    	
		    	resultDetailTransitRouter.setLineStringLat(lineStringLat);
		    	resultDetailTransitRouter.setLineStringLon(lineStringLon);
		    	
		    	routeDetailList.add(resultDetailTransitRouter);
	    	}//방법 1중 단계 legs
	    	
	    	ResultTransitRouter resultTransitRouter = ResultTransitRouter.builder()
	                .totalFare(item.path("fare").path("regular").path("totalFare").asInt()) // 총 요금
	                .totalTime(item.path("totalTime").asInt()) // 총 걸린 시간
	                .totalDistance(item.path("totalDistance").asInt()) // 총 거리
	                .totalWalkTime(item.path("totalWalkTime").asInt()) // 총 도보시간
	                .transferCount(item.path("transferCount").asInt()) // 환승횟수
	                .pathType(item.path("pathType").asInt()) // 어떤 종류인지
	                .routeList(routeDetailList)
	                .build();
	    	
	    	ResultRouterList.add(resultTransitRouter); // 방법1 추가
	    }//for
	    
		return ResultRouterList;
	}// getTransitRoute: 대중교통 경로찾기
	
	public String getRouteResultJson(Object searchRouter, String requestUrl) throws Exception {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("appKey", tmapAPIKey);
		
		String requestJson = new Gson().toJson(searchRouter);
		HttpEntity<String> requestEntity = new HttpEntity<>(requestJson, headers);
		String resultJson = restTemplate.postForObject(requestUrl, requestEntity, String.class);
		
		return resultJson;
		
	}// getRoute: 보행자, 자동차, 대중교통 경로 요청
}
