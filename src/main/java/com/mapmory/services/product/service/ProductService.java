package com.mapmory.services.product.service;

import java.util.List;

import com.mapmory.common.domain.Search;
import com.mapmory.services.product.domain.Product;
import com.mapmory.services.product.domain.ProductImage;

public interface ProductService {

    void addProduct(Product product, List<String> imageFiles) throws Exception;

    Product getDetailProduct(int productNo) throws Exception;

    List<Product> getProductList(Search search) throws Exception;

    void updateProduct(Product product, List<String> imageFiles) throws Exception;

    void deleteProduct(int productNo) throws Exception;

    Product getProductByName(String productTitle) throws Exception;

    int getProductTotalCount(Search search) throws Exception;

    List<ProductImage> getProductImages(int productNo) throws Exception;
}
