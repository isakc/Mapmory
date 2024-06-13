package com.mapmory.controller.product;

import com.mapmory.services.product.domain.Product;
import com.mapmory.services.product.service.ProductService;
import com.mapmory.common.domain.Page;
import com.mapmory.common.domain.Search;
import com.mapmory.common.util.ImageFileUtil;
import com.mapmory.common.util.ObjectStorageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest/products/*")
public class ProductRestController {

    @Autowired
    @Qualifier("productServiceImpl")
    private ProductService productService;

    @Autowired
    private ObjectStorageUtil objectStorageUtil;

    @Value("${page.Unit}")
    private int pageUnit;

    @Value("${page.Size}")
    private int pageSize;

    @Value("${object.folderName}")
    private String folderName;


    // 상품 목록 조회
    @GetMapping("/getProductList")
    public ResponseEntity<List<Product>> getProductList(@ModelAttribute("Search") Search search) throws Exception {
        if (search.getCurrentPage() == 0) {
            search.setCurrentPage(1);
        }

        search.setPageSize(pageSize);

        Map<String, Object> map = productService.getProductList(search);
        
        List<Product> productList = (List<Product>) map.get("productList");
        return ResponseEntity.ok(productList);
    }

    // 상품 상세 조회
    @GetMapping("/getDetailProduct/{productNo}")
    public ResponseEntity<Product> getDetailProduct(@PathVariable int productNo) throws Exception {
        Product product = productService.getDetailProduct(productNo);
        return ResponseEntity.ok(product);
    }
}