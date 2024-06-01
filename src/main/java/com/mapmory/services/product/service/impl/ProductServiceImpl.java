package com.mapmory.services.product.service.impl;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mapmory.common.domain.Search;
import com.mapmory.common.util.ImageFileUtil;
import com.mapmory.services.product.dao.ProductDao;
import com.mapmory.services.product.dao.ProductImageDao;
import com.mapmory.services.product.domain.Product;
import com.mapmory.services.product.domain.ProductImage;
import com.mapmory.services.product.service.ProductService;

@Service("productServiceImpl")
public class ProductServiceImpl implements ProductService {

	@Autowired
	@Qualifier("productDao")
	ProductDao productDao;

	@Autowired
	@Qualifier("productImageDao")
	ProductImageDao productImageDao;
	
	public void addProduct(Product product, List<String> imageNames) throws Exception {
		productDao.addProduct(product);
		
		for(String originalImageFilename : imageNames) {
			ProductImage productImage = new ProductImage();
			String uuidFilename = ImageFileUtil.getProductImageUUIDFileName(originalImageFilename);
			productImage.setUuid(uuidFilename); // UUID로 변경된 파일 이름 설정
			productImage.setProductNo(product.getProductNo());
			productImage.setImageFile(originalImageFilename); // 원본 파일 이름 설정
			productImageDao.addProductImage(productImage);
		}
	}
	
	public Product getDetailProduct(int productNo) throws Exception {
		return productDao.getDetailProduct(productNo);
	}
	
	public Map<String, Object> getProductList(Search search) throws Exception {

        List<Product> productList = productDao.getProductList(search);

        for (Product product : productList) {
            List<ProductImage> productImageList = productImageDao.getProductImageList(product.getProductNo());
            List<String> uuidList = new ArrayList<>();

            for (ProductImage productImage : productImageList) {
                uuidList.add(productImage.getUuid());
            }

            product.setProductImage(uuidList);
        }

        int totalCount = productDao.getProductTotalCount(search);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("list", productList);
        resultMap.put("totalCount", totalCount);

        return resultMap;
    }

    public void updateProduct(Product product, List<String> imageNames) throws Exception {
        productDao.updateProduct(product);

        for (String originalImageFilename : imageNames) {
            ProductImage productImage = new ProductImage();
            String uuidFilename = ImageFileUtil.getProductImageUUIDFileName(originalImageFilename);
            productImage.setUuid(uuidFilename); // UUID로 변경된 파일 이름 설정
            productImage.setProductNo(product.getProductNo());
            productImage.setImageFile(originalImageFilename); // 원본 파일 이름 설정
            productImageDao.addProductImage(productImage);
        }
    }

    public void deleteProduct(int productNo) throws Exception {
        productImageDao.deleteProductImage(productNo);
        productDao.deleteProduct(productNo);
    }

    public Product getProductByName(String productTitle) throws Exception {
        Product product = productDao.getProductByName(productTitle);

        List<ProductImage> productImageList = productImageDao.getProductImageList(product.getProductNo());
        List<String> uuidList = new ArrayList<>();

        for (ProductImage productImage : productImageList) {
            uuidList.add(productImage.getUuid());
        }

        product.setProductImage(uuidList);

        return product;
    }
	
}
