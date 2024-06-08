package com.mapmory.unit.map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import com.mapmory.services.map.dao.MapDao;
import com.mapmory.services.map.domain.SearchRouter;
import com.mapmory.services.map.domain.SearchTransitRouter;

@SpringBootTest
public class MapDaoTest {
	
	///// Field /////
	
	@Value("${tmap.pedestrian.URL}")
	private String tmapPedestrianURL;

	@Value("${tmap.car.URL}")
	private String tmapCarURL;

	@Value("${tmap.transit.URL}")
	private String tmapTransitURL;
	
	@Autowired
	@Qualifier("mapDaoImpl")
	private MapDao mapDao;
	
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
		
		System.out.println(searchRouter);
		System.out.println(mapDao.getRoute(searchRouter, tmapPedestrianURL));
	}// testGetPedestrianRoute Test
	
	//@Test
	public void getCarRoute() throws Exception {
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
		
		System.out.println(searchRouter);
		System.out.println(mapDao.getRoute(searchRouter, tmapCarURL));
	}// getCarRoute Test
	
	//@Test
	public void getTransitRoute() throws Exception {
		SearchTransitRouter searchTransitRouter = SearchTransitRouter.builder()
				.startX(127.0319638)
				.startY(37.4972038)
				.endX(127.0549272)
				.endY(37.47451030)
				.lang(0)
				.format("json")
				.count(5)
				.build();
		
		System.out.println(mapDao.getRoute(searchTransitRouter, tmapTransitURL));
	}// getTransitRoute Test
}
