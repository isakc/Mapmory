package com.mapmory.services.product.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mapmory.services.product.domain.ProductImage;

@Mapper
public interface ProductImageDao {

	public void addProductImage(ProductImage productImage) throws Exception;
	
	public List<ProductImage> getProductImageList(int productNo) throws Exception;
	
	public void deleteProductImage(int productNo) throws Exception;
}
