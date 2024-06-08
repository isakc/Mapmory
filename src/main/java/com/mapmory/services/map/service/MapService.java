package com.mapmory.services.map.service;

import java.util.List;

import com.mapmory.services.map.domain.ResultRouter;
import com.mapmory.services.map.domain.ResultTransitRouter;
import com.mapmory.services.map.domain.SearchRouter;
import com.mapmory.services.map.domain.SearchTransitRouter;

public interface MapService {
	
	public ResultRouter getPedestrianRoute(SearchRouter searchRouter) throws Exception;
	
	public ResultRouter getCarRoute(SearchRouter searchRouter) throws Exception;
	
	public List<ResultTransitRouter> getTransitRoute(SearchTransitRouter searchTransitRouter) throws Exception;
}
