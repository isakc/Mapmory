package com.mapmory.services.product.service.impl;

import java.util.ArrayList;
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
        	List<ProductImage> productImages = productImageDao.getProductImageList(product.getProductNo());
            
            List<String> uuids = new ArrayList<>();
            List<String> imageTags = new ArrayList<>();
            
            for (ProductImage image : productImages) {
                uuids.add(image.getUuid());
                imageTags.add(image.getImageTag());
            }
            
            product.setUuid(uuids); // 이미지 UUID 정보를 상품 객체에 설정
            product.setImageTags(imageTags);
        }
        
        System.out.println("서비스 임플에서의 프로덕트입니다. : : : : :" + product);
        return product; // 이미지 정보를 포함한 상품 정보 반환
    }

    @Override
    public Map<String,Object> getProductList(Search search) throws Exception {
        
        int offset = (search.getCurrentPage() - 1) * search.getPageSize();
        search.setOffset(offset);
        
        List<Product> productList = productDao.getProductList(search);
        int totalCount = productDao.getProductTotalCount(search);
        
        System.out.println("프로덕트 리스트입니다. " + productList);
        
        for (Product product : productList) {
            List<ProductImage> productImages = productImageDao.getProductImageList(product.getProductNo());
            
            List<String> uuids = new ArrayList<>();
            List<String> imageTags = new ArrayList<>();
            
            for (ProductImage image : productImages) {
                uuids.add(image.getUuid());
                imageTags.add(image.getImageTag());
            }
            
            product.setUuid(uuids); // 이미지 UUID 정보를 상품 객체에 설정
            product.setImageTags(imageTags);
        }
        
        Map<String,Object> map = new HashMap<>();
        map.put("productList", productList);
        map.put("productTotalCount", totalCount);
        
        return map;
    }


    @Override
    public void updateProduct(Product product, List<String> uuidFileNames, List<String> originalFileNames, List<String> imageTags, List<String> newImageTags) throws Exception {
        // 상품 정보 업데이트
        productDao.updateProduct(product);

        // 기존 이미지 태그 업데이트
        if (imageTags != null) {
            updateImageTags(product.getProductNo(), imageTags);
        }

        // 새로운 이미지 태그 업데이트
        if (newImageTags != null) {
            updateNewImageTags(product.getProductNo(), newImageTags);
        }

        // 새로운 이미지 파일 업로드 및 이미지 정보 추가
        for (int i = 0; i < uuidFileNames.size(); i++) {
            String uuid = uuidFileNames.get(i);
            String originalFilename = originalFileNames.get(i);
            String imageTag = null;
            if (i < newImageTags.size()) {
                imageTag = newImageTags.get(i);
            }

            ProductImage productImage = new ProductImage();
            productImage.setProductNo(product.getProductNo());
            productImage.setImageFile(originalFilename);
            productImage.setUuid(uuid);
            productImage.setImageTag(imageTag);
            productImageDao.addProductImage(productImage);
        }
    }
    
    private void updateNewImageTags(int productNo, List<String> newImageTags) throws Exception {
    	List<ProductImage> existingImages = productImageDao.getNewProductImageList(productNo);

        for (int i = 0; i < existingImages.size(); i++) {
            ProductImage image = existingImages.get(i);
            if (i < newImageTags.size()) {
                image.setImageTag(newImageTags.get(i));
            } else {
                image.setImageTag("");
            }
            productImageDao.updateProductImage(image);
        }
    }



    @Override
    public void deleteProduct(int productNo, String folderName) throws Exception {
        
        List<ProductImage> imageList = productImageDao.getProductImageList(productNo);
        
        for (ProductImage image : imageList) {
            objectStorageUtil.deleteFile(image.getUuid(), folderName);
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
    public Product getSubscription() throws Exception {
        
        Product product = productDao.getSubscription(); // 상품 정보 가져오기
        
        if (product != null) {
            List<ProductImage> productImages = productImageDao.getProductImageList(product.getProductNo()); // 이미지 정보 가져오기
            
            List<String> uuids = new ArrayList<>();
            List<String> imageTags = new ArrayList<>();
            
            for (ProductImage image : productImages) {
                uuids.add(image.getUuid());
                imageTags.add(image.getImageTag());
            }
            
            product.setUuid(uuids); // 이미지 UUID 정보를 상품 객체에 설정
            product.setImageTags(imageTags);
        }
        
        System.out.println("서비스 임플에서의 프로덕트입니다. : : : : :" + product);
        return product; // 이미지 정보를 포함한 상품 정보 반환
    }

    
    public byte[] getImageBytesByImageTag(String imageTag, String folderName) throws Exception {
        ProductImage productImage = productImageDao.getImageByTag(imageTag);
        if (productImage != null) {
            String uuid = productImage.getUuid();
            return objectStorageUtil.getImageBytes(uuid, folderName);
        } else {
            return null;
        }
    }
    
    public byte[] getImageBytes(String uuid, String folderName) throws Exception {
        return objectStorageUtil.getImageBytes(uuid, folderName);
    }
    
    @Override
    public void updateImageTags(int productNo, List<String> imageTags) throws Exception {
        List<ProductImage> existingImages = productImageDao.getProductImageList(productNo);
        
        // imageTags가 null인 경우 빈 리스트로 초기화
        if (imageTags == null) {
            imageTags = new ArrayList<>();
        }
        
        for (int i = 0; i < existingImages.size(); i++) {
            ProductImage image = existingImages.get(i);
            if (i < imageTags.size()) {
                image.setImageTag(imageTags.get(i));
            } else {
                image.setImageTag("");
            }
            productImageDao.updateProductImage(image);
        }
    }

}