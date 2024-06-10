package com.mapmory.services.product.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mapmory.common.domain.Search;
import com.mapmory.services.product.dao.ProductDao;
import com.mapmory.services.product.dao.ProductImageDao;
import com.mapmory.services.product.domain.Product;
import com.mapmory.services.product.domain.ProductImage;
import com.mapmory.services.product.service.ProductService;
import com.mapmory.common.util.ImageFileUtil;
import com.mapmory.common.util.ObjectStorageUtil;

@Service("productServiceImpl")
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private ProductImageDao productImageDao;
    
    @Autowired
    private ObjectStorageUtil objectStorageUtil;
    
    @Value("${cdn.url}")
    private String cdnUrl;
    
    @Override
    @Transactional
    public void addProduct(Product product, List<String> uuidFileNames, List<String> originalFileNames, List<String> imageTag) throws Exception {
        productDao.addProduct(product);

        for (int i = 0; i < uuidFileNames.size(); i++) {
            String uuid = uuidFileNames.get(i);
            String originalFilename = originalFileNames.get(i);
            String imageTags = imageTag.get(i);

            ProductImage productImage = new ProductImage();
            productImage.setProductNo(product.getProductNo());
            productImage.setImageFile(originalFilename); // 원본 파일명 설정
            productImage.setUuid(uuid);
            productImage.setImageTag(imageTags);
            productImageDao.addProductImage(productImage);
        }
    }

    @Override
    public Product getDetailProduct(int productNo) throws Exception {
    	
    	Product product = productDao.getDetailProduct(productNo); // 상품 정보 가져오기
    	
    	if (product != null) { // 상품 정보가 존재하는 경우에만 이미지 정보를 가져와서 설정
    		List<String> imageUuidList = productImageDao.getProductImageList(productNo); // 이미지 정보 가져오기
    		product.setUuid(imageUuidList); // 이미지 정보를 상품 객체에 설정
    	}
    	
    	System.out.println("서비스 임플에서의 프로덕트입니다. : : : : :" + product);
    	return product; // 이미지 정보를 포함한 상품 정보 반환
    }

    @Override
    public Map<String,Object> getProductList(Search search) throws Exception {
    	
    	search.setOffset((search.getCurrentPage() - 1) * search.getPageSize());
        search.setPageSize(search.getPageSize());
        
    	List<Product> productList = productDao.getProductList(search);
    	int totalCount = productDao.getProductTotalCount(search);
    	
    	System.out.println("프로덕트 리스트입니다. " + productList);
    	
    	for (Product product : productList) {
            List<String> imageUuidList = productImageDao.getProductImageList(product.getProductNo());
            product.setUuid(imageUuidList);
        }
    	
    	Map<String,Object> map = new HashMap<String, Object>();
    	map.put("productList",productList);
    	map.put("productTotalCount", new Integer(totalCount));
        
        return map;
    }

    @Transactional
    @Override
    public void updateProduct(Product product, List<String> uuidFileNames, List<String> originalFileNames) throws Exception {
        productDao.updateProduct(product);

        // 이미지 파일 업데이트
        for (int i = 0; i < uuidFileNames.size(); i++) {
            String uuid = uuidFileNames.get(i);
            String originalFilename = originalFileNames.get(i);

            ProductImage productImage = new ProductImage();
            productImage.setProductNo(product.getProductNo());
            productImage.setImageFile(originalFilename);
            productImage.setUuid(uuid);
            productImageDao.addProductImage(productImage);
        }
    }

    @Override
    public void deleteProduct(int productNo,String folderName) throws Exception {
    	
    	List<String> imageUuidList = productImageDao.getProductImageList(productNo);
        for (String uuid : imageUuidList) {
            objectStorageUtil.deleteFile(uuid,folderName);
        }
        
    	productImageDao.deleteProductImage(productNo);
    	productDao.deleteProduct(productNo);
        
    }
    
    @Override
    public void deleteImage(String uuid,String folderName) throws Exception {
    	
    	objectStorageUtil.deleteFile(uuid, folderName);
    	productImageDao.deleteImage(uuid);
    	
    }

    @Override
    public Product getProductByName(String productTitle) throws Exception {
        return productDao.getProductByName(productTitle);
    }


    @Override
    public Map<String,Object> getProductImages(int productNo) throws Exception {
        return (Map<String, Object>) productImageDao.getProductImageList(productNo);
    }
    
    public String getImageUrl(ByteArrayResource imageResource) {
        return cdnUrl + "productImage/" + imageResource.getDescription();
    }
    
    public void processImageWithTag(String imageTag) throws Exception {
        // IMAGE_TAG를 기반으로 이미지 조회
        ProductImage productImage = getImageByTag(imageTag);
        
        if (productImage != null) {
            // 이미지 처리 로직 구현
            String imageUUID = productImage.getUuid();
            // ...
            System.out.println("Image UUID: " + imageUUID);
        } else {
            System.out.println("이건 이미지에 없는거임 ㅋ");
        }
    }
    
    @Override
    public String getImageUrlByImageTag(String imageTag,String folderName) throws Exception {
        ProductImage productImage = productImageDao.getImageByTag(imageTag);
        if (productImage != null) {
            String uuid = productImage.getUuid();
            ByteArrayResource imageResource = objectStorageUtil.getImageResource(uuid,folderName);
            return cdnUrl + "productImage/" + uuid;
        } else {
            return "이미지 not found";
        }
    }
    
    @Override
    public ProductImage getImageByTag(String imageTag) throws Exception {
        return productImageDao.getImageByTag(imageTag);
    }
    
    public Product getSubscription() throws Exception {
    	
    	Product product = productDao.getSubscription(); // 상품 정보 가져오기
    	
    	if (product != null) { // 상품 정보가 존재하는 경우에만 이미지 정보를 가져와서 설정
    		List<String> imageUuidList = productImageDao.getProductImageList(product.getProductNo()); // 이미지 정보 가져오기
    		product.setUuid(imageUuidList); // 이미지 정보를 상품 객체에 설정
    	}
    	
    	System.out.println("서비스 임플에서의 프로덕트입니다. : : : : :" + product);
    	return product; // 이미지 정보를 포함한 상품 정보 반환
    }
}
