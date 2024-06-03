package com.mapmory.services.product.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mapmory.common.domain.Search;
import com.mapmory.services.product.dao.ProductDao;
import com.mapmory.services.product.dao.ProductImageDao;
import com.mapmory.services.product.domain.Product;
import com.mapmory.services.product.domain.ProductImage;
import com.mapmory.services.product.service.ProductService;
import com.mapmory.common.util.ImageFileUtil;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private ProductImageDao productImageDao;

    @Override
    @Transactional
    public void addProduct(Product product, List<String> imageFiles) throws Exception {
        productDao.addProduct(product); // 상품 정보를 먼저 추가

        // 이미지 파일의 UUID 값을 생성하고 ProductImage 객체에 설정하여 데이터베이스에 삽입
        for (String imageFile : imageFiles) {
            String uuid = ImageFileUtil.getProductImageUUIDFileName(imageFile);
            ProductImage productImage = new ProductImage();
            productImage.setProductNo(product.getProductNo());
            productImage.setImageFile(uuid);
            productImage.setUuid(uuid); // UUID 설정
            productImageDao.addProductImage(productImage);
        }
    }

    @Override
    public Product getDetailProduct(int productNo) throws Exception {
        return productDao.getDetailProduct(productNo);
    }

    @Override
    public List<Product> getProductList(Search search) throws Exception {
        
        return null;
    }

    @Transactional
    @Override
    public void updateProduct(Product product, List<String> imageFiles) throws Exception {
        
    }

    @Override
    public void deleteProduct(int productNo) throws Exception {
        
    }

    @Override
    public Product getProductByName(String productTitle) throws Exception {
        return productDao.getProductByName(productTitle);
    }

    @Override
    public int getProductTotalCount(Search search) throws Exception {
        // getProductTotalCount 메서드 구현
        return 0;
    }

    @Override
    public List<ProductImage> getProductImages(int productNo) throws Exception {
        return productImageDao.getProductImageList(productNo);
    }
}
