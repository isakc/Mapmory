package com.mapmory.services.map.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mapmory.services.map.dao.MapDao;
import com.mapmory.services.map.domain.ResultDetailTransitRouter;
import com.mapmory.services.map.domain.ResultRouter;
import com.mapmory.services.map.domain.ResultTransitRouter;
import com.mapmory.services.map.domain.SearchRouter;
import com.mapmory.services.map.domain.SearchTransitRouter;
import com.mapmory.services.map.service.MapService;

@Service("mapServiceImpl")
public class MapServiceImpl implements MapService {

	///// Field /////
	@Autowired
	@Qualifier("mapDaoImpl")
	private MapDao mapDao;

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
		String resultJson = mapDao.getRoute(searchRouter, tmapPedestrianURL);
		
	    ResultRouter resultRouter = new ResultRouter();
		
		ObjectMapper objectMapper = new ObjectMapper();
	    JsonNode rootNode = objectMapper.readTree(resultJson);
	    JsonNode resultData = rootNode.path("features");
	    
	    double totalDistance = resultData.get(0).path("properties").path("totalDistance").asDouble();
	    double totalTime = resultData.get(0).path("properties").path("totalTime").asDouble();
	    List<Double> lat = new ArrayList<Double>();
	    List<Double> lon = new ArrayList<Double>();
	    List<String> description = new ArrayList<String>();
	    
	    if (resultData.isArray()) {
            for (JsonNode item : resultData) {
                JsonNode geometry = item.path("geometry");
                if (geometry.path("type").asText().equals("LineString") ) {
                	
                    JsonNode coordinates = geometry.path("coordinates");
                    if (coordinates.isArray()) {
                    	
                        for (JsonNode coord : coordinates) {
                            lat.add(coord.get(1).asDouble());
                            lon.add(coord.get(0).asDouble());
                        }//for end
                        
                    }//if
                } else {
                	description.add(item.path("properties").path("description").asText());
                }//if~else LineString이면 lat, lng
                
            }//for
        }//만약 데이터가 있는 경우

	    resultRouter.setTotalDistance(totalDistance);
	    resultRouter.setTotalTime(totalTime);
	    resultRouter.setLat(lat);
	    resultRouter.setLon(lon);
	    resultRouter.setDescription(description);
	    
		return resultRouter;
	}// getPedestrianRoute: 도보 경로찾기
	
	@Override
	public ResultRouter getCarRoute(SearchRouter searchRouter) throws Exception {
		String resultJson = mapDao.getRoute(searchRouter, tmapCarURL);
		
	    ResultRouter resultRouter = new ResultRouter();
		
		ObjectMapper objectMapper = new ObjectMapper();
	    JsonNode rootNode = objectMapper.readTree(resultJson);
	    JsonNode resultData = rootNode.path("features");
	    
	    double totalDistance = resultData.get(0).path("properties").path("totalDistance").asDouble();
	    double totalTime = resultData.get(0).path("properties").path("totalTime").asDouble();
	    List<Double> lat = new ArrayList<Double>();
	    List<Double> lon = new ArrayList<Double>();
	    List<String> description = new ArrayList<String>();
	    
	    if (resultData.isArray()) {
            for (JsonNode item : resultData) {
                JsonNode geometry = item.path("geometry");
                if (geometry.path("type").asText().equals("LineString") ) {
                	
                    JsonNode coordinates = geometry.path("coordinates");
                    if (coordinates.isArray()) {
                    	
                        for (JsonNode coord : coordinates) {
                            lat.add(coord.get(1).asDouble());
                            lon.add(coord.get(0).asDouble());
                        }//for end
                        
                    }//if
                } else {
                	description.add(item.path("properties").path("description").asText());
                }//if~else LineString이면 lat, lng
                
            }//for
        }//만약 데이터가 있는 경우

	    resultRouter.setTotalDistance(totalDistance);
	    resultRouter.setTotalTime(totalTime);
	    resultRouter.setLat(lat);
	    resultRouter.setLon(lon);
	    resultRouter.setDescription(description);
	    
		return resultRouter;
	}// getCarRoute: 자동차 경로찾기

	@Override
	public List<ResultTransitRouter> getTransitRoute(SearchTransitRouter searchTransitRouter) throws Exception {
		String resultJson = mapDao.getRoute(searchTransitRouter, tmapTransitURL);
	    
		List<ResultTransitRouter> ResultRouterList = new ArrayList<ResultTransitRouter>(); // 대중교통 경로 리스트
		
		ObjectMapper objectMapper = new ObjectMapper();
	    JsonNode rootNode = objectMapper.readTree(resultJson);
	    JsonNode itineraries = rootNode.path("metaData").path("plan").path("itineraries");
	    
	    for(JsonNode item : itineraries) {
	    	ResultTransitRouter resultTransitRouter = new ResultTransitRouter(); // 방법 1, 방법2, ...
	    	List<ResultDetailTransitRouter> routeDetailList = new ArrayList<ResultDetailTransitRouter>(); // 방법 중 단계를 담는 List
	    	
	    	resultTransitRouter.setTotalFare(item.path("fare").path("regular").path("totalFare").asInt()); // 총 요금
	    	resultTransitRouter.setTotalTime(item.path("totalTime").asInt()); // 총 걸린 시간
	    	resultTransitRouter.setTotalDistance(item.path("totalDistance").asInt()); // 총 거리
	    	resultTransitRouter.setTotalWalkTime(item.path("totalWalkTime").asInt()); // 총 도보시간
	    	resultTransitRouter.setTransferCount(item.path("transferCount").asInt()); // 환승횟수
	    	resultTransitRouter.setPathType(item.path("pathType").asInt()); // 어떤 종류인지
	    	
	    	JsonNode legs = item.path("legs");
	    	for(JsonNode leg : legs) {
		    	ResultDetailTransitRouter resultDetailTransitRouter = ResultDetailTransitRouter.builder()
		    			.mode(leg.path("mode").asText())
		    			.route(leg.path("route").asText())
		    			.startName(leg.path("start").path("name").asText())
		    			.startLat(leg.path("start").path("lon").asDouble())
		    			.startLon(leg.path("start").path("lon").asDouble())
		    			.endName(leg.path("end").path("name").asText())
		    			.endLat(leg.path("end").path("lat").asDouble())
		    			.endLon(leg.path("end").path("lon").asDouble())
		    			.build(); // 단계들
		    	
		    	JsonNode steps = leg.path("steps");

    			List<Double> lineStringLat = new ArrayList<Double>();
    			List<Double> lineStringLon = new ArrayList<Double>();
		    	
		    	for(JsonNode step : steps) {
		    		String[] lineStringArray = step.path("linestring").asText().split(" "); //lineString 파싱
		    		
		    		 for (String lineString : lineStringArray) {
		    	            String[] coordinates = lineString.split(",");
		    	            
		    	            lineStringLon.add(Double.parseDouble(coordinates[0])); 
		    	            lineStringLat.add(Double.parseDouble(coordinates[1])); 
		    	            
		    	      }// 위도, 경도 나누기
		    		
		    	}//step 중 lineString
		    	
		    	resultDetailTransitRouter.setLineStringLat(lineStringLat);
		    	resultDetailTransitRouter.setLineStringLon(lineStringLon);
		    	
		    	routeDetailList.add(resultDetailTransitRouter);
	    	}//방법 1중 단계 legs
	    	
	    	resultTransitRouter.setRouteList(routeDetailList); // 가는 스텝 설정
	    	
	    	ResultRouterList.add(resultTransitRouter); // 방법1 추가
	    }//for
	    
		return ResultRouterList;
	}// getTransitRoute: 대중교통 경로찾기

}
