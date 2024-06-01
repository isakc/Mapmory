package com.mapmory.services.product.service;

import java.util.List;
import java.util.Map;

import com.mapmory.common.domain.Search;
import com.mapmory.services.product.domain.Product;

public interface ProductService {

	public void addProduct(Product product, List<String> imageNames) throws Exception;
	
	public Product getDetailProduct(int productNo) throws Exception;
	
	public Map<String,Object> getProductList(Search search) throws Exception;
	
	public void updateProduct(Product product, List<String> imageNames) throws Exception;
	
	public void deleteProduct(int productNo) throws Exception;
	
	public Product getProductByName(String productTitle) throws Exception;
}
