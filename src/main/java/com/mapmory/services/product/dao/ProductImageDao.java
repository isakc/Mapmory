package com.mapmory.services.product.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mapmory.services.product.domain.ProductImage;

@Mapper
public interface ProductImageDao {

    // 이미지 추가 메서드 변경
    public void addProductImage(ProductImage productImage) throws Exception;
    
    public List<String> getProductImageList(int productNo) throws Exception;
    
    public void updateProductImage(ProductImage productImage) throws Exception;
    
    public void deleteProductImage(int productNo) throws Exception;
    
    public void deleteImage(String uuid) throws Exception;
}
