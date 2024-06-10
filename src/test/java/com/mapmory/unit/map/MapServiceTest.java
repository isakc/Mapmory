package com.mapmory.unit.map;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import com.mapmory.services.map.domain.SearchRouter;
import com.mapmory.services.map.domain.SearchTransitRouter;
import com.mapmory.services.map.service.MapService;

@SpringBootTest
public class MapServiceTest {
	
	@Autowired
	@Qualifier("mapServiceImpl")
	private MapService mapService;
	
	@Test
	public void testGetPedestrianRoute() throws Exception {
		SearchRouter searchRouter = SearchRouter.builder()
				.startX(127.0319638)
				.startY(37.4972038)
				.endX(127.0549272)
				.endY(37.47451030)
				.reqCoordType("WGS84GEO")
				.resCoordType("WGS84GEO")
				.startName("출발지")
				.endName("도착지")
				.build();
		
		System.out.println(mapService.getPedestrianRoute(searchRouter));
	}// testGetPedestrianRoute Test
	
	//@Test
	public void testGetCarRoute() throws Exception {
		SearchRouter searchRouter = SearchRouter.builder()
				.startX(127.0319638)
				.startY(37.4972038)
				.endX(127.0549272)
				.endY(37.47451030)
				.reqCoordType("WGS84GEO")
				.resCoordType("WGS84GEO")
				.startName("출발지")
				.endName("도착지")
				.build();
		
		System.out.println(mapService.getCarRoute(searchRouter));
	}// testGetCarRoute Test
	
	//@Test
	public void testGetTransitRoute() throws Exception {
		SearchTransitRouter searchTransitRouter = SearchTransitRouter.builder()
				.startX(127.0319638)
				.startY(37.4972038)
				.endX(127.0549272)
				.endY(37.47451030)
				.lang(0)
				.format("json")
				.count(5)
				.build();
		
		Assert.assertEquals(5, mapService.getTransitRoute(searchTransitRouter).size());
	}// testGetTransitRoute Test
}
