package com.mapmory.unit.product;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.mapmory.common.util.ImageFileUtil;
import com.mapmory.services.product.domain.Product;
import com.mapmory.services.product.domain.ProductImage;
import com.mapmory.services.product.service.ProductService;

@SpringBootTest
public class ProductServiceTest {

    @Autowired
    ProductService productService;

    @Test
    public void testAddProduct() throws Exception {
        // Given
        Product product = new Product();
        product.setProductTitle("Test Product");
        product.setPrice(10000);
        product.setProductRegDate(LocalDateTime.now());
        product.setPeriod(1);
        product.setUserId("admin");

        List<String> imageFiles = new ArrayList<>();
        imageFiles.add("test_image.jpg");
        imageFiles.add("test_image11.jpg");

        // When
        productService.addProduct(product, imageFiles);

        // Then
        // 추가로 확인해야 할 로직이 있다면 여기에 추가합니다.
    }
}
