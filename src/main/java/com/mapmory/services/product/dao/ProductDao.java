package com.mapmory.services.product.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mapmory.common.domain.Search;
import com.mapmory.services.product.domain.Product;
import com.mapmory.services.product.domain.ProductImage;

@Mapper
public interface ProductDao {

    public void addProduct(Product product) throws Exception;
    
    public Product getDetailProduct(int productNo) throws Exception;
    
    public List<Product> getProductList(Search search) throws Exception;
    
    public void updateProduct(Product product) throws Exception;
    
    public void deleteProduct(int productNo) throws Exception;
    
    public Product getProductByName(String productTitle) throws Exception;
    
    public int getProductTotalCount(Search search) throws Exception;
    
    public Product getSubscription(int productNo) throws Exception;

    // 이미지 관련 메서드 추가
}
