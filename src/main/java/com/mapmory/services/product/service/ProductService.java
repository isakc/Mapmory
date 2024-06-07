package com.mapmory.services.product.service;

import java.util.List;
import java.util.Map;

import com.mapmory.common.domain.Search;
import com.mapmory.services.product.domain.Product;
import com.mapmory.services.product.domain.ProductImage;

public interface ProductService {

//    public void addProduct(Product product, List<String> imageFiles) throws Exception;
	public void addProduct(Product product, List<String> uuidFileNames, List<String> originalFileNames,List<String> imageTag) throws Exception;

    public Product getDetailProduct(int productNo) throws Exception;

    public Map<String,Object> getProductList(Search search) throws Exception;

    public void updateProduct(Product product, List<String> uuidFileNames, List<String> originalFileNames) throws Exception;

    public void deleteProduct(int productNo) throws Exception;

    public Product getProductByName(String productTitle) throws Exception;

    public Map<String,Object> getProductImages(int productNo) throws Exception;
    
    public void deleteImage(String uuid) throws Exception;
    
    public String getImageUrlByImageTag(String imageTag) throws Exception;
    
    public void processImageWithTag(String imageTag) throws Exception;
    
    ProductImage getImageByTag(String imageTag) throws Exception;
}
