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

    public void updateProduct(Product product, List<String> uuidFileNames, List<String> originalFileNames, List<String> imageTags, List<String> newImageTags) throws Exception;

    public void deleteProduct(int productNo,String folderName) throws Exception;
    
    public void deleteImage(String uuid,String folderName) throws Exception;
    
    public String getImageUrlByImageTag(String imageTag,String folderName) throws Exception;

    public Product getSubscription() throws Exception;
    
    public byte[] getImageBytes(String uuid, String folderName) throws Exception;

	public byte[] getImageBytesByImageTag(String imageTag, String folderName)throws Exception;
	
	public void updateImageTags(int productNo, List<String> imageTags) throws Exception;
	
}