package com.mapmory.controller.product;

import com.mapmory.services.product.domain.Product;
import com.mapmory.services.product.service.ProductService;
import com.mapmory.common.domain.Search;
import com.mapmory.common.util.ImageFileUtil;
import com.mapmory.common.util.ObjectStorageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/product/*")
public class ProductController {

    @Autowired
    @Qualifier("productServiceImpl")
    private ProductService productService;

    @Autowired
    private ObjectStorageUtil objectStorageUtil; // ObjectStorageUtil 추가
    
    @Value("${page.Unit}")
    int pageUnit;

    @Value("${page.Size}")
    int pageSize;
    
    public ProductController() {
		System.out.println(this.getClass());
	}

    @GetMapping("addProduct")
    public String addProduct() {
        return "forward:/product/addProduct";
    }

    @PostMapping("/addProduct")
    public String addProduct(@ModelAttribute("product") Product product,
                             @RequestParam("uploadFile") List<MultipartFile> files) throws Exception{
            // 각 이미지 파일에 대해 UUID 생성하여 처리
            List<String> uuidFileName = new ArrayList<>();
            for (MultipartFile file : files) {
                String uuid = ImageFileUtil.getProductImageUUIDFileName(file.getOriginalFilename());
                objectStorageUtil.uploadFileToS3(file); // 파일 업로드
                uuidFileName.add(uuid);
            }
            productService.addProduct(product, uuidFileName); // UUID 목록과 함께 상품 정보 전달
            return "redirect:/product/getProductList";

    }
    
    @GetMapping("/getProductList")
    public String getProductList(@ModelAttribute("search") Search search, Model model) throws Exception {
        Map<String, Object> map = productService.getProductList(search);
        List<Product> productList = (List<Product>) map.get("productList");
        int totalCount = (int) map.get("productTotalCount");

        model.addAttribute("productList", productList);
        model.addAttribute("search", search);
        model.addAttribute("totalCount", totalCount);

        return "product/getProductList";
    }
    
    @GetMapping("/getDetailProduct")
    public String getDetailProduct(@PathVariable int productNo, Model model) throws Exception {
    	System.out.println("GetDetailProduct Start......");
    	
    	Product product = productService.getDetailProduct(productNo);
    	model.addAttribute("product",product);
    	return "product/getDetailProduct";
    }
    
    @PostMapping("/updateProduct")
    public String updateProduct(@ModelAttribute("update") Product product,
                                @RequestParam("uploadFile") List<MultipartFile> files,
                                @RequestParam(value = "deleteImageUuids", required = false) List<String> deleteImageUuids) throws Exception {

        // 새로운 이미지 파일 업로드 및 UUID 생성
        List<String> uuidFileName = new ArrayList<>();
        for (MultipartFile file : files) {
            String uuid = ImageFileUtil.getProductImageUUIDFileName(file.getOriginalFilename());
            objectStorageUtil.uploadFileToS3(file); // 파일 업로드
            uuidFileName.add(uuid);
        }

        // 삭제할 이미지 UUID 목록 처리
        if (deleteImageUuids != null && !deleteImageUuids.isEmpty()) {
            for (String uuid : deleteImageUuids) {
                productService.deleteImage(uuid);
            }
        }

        // 상품 정보 업데이트
        productService.updateProduct(product, uuidFileName);

        return "redirect:/product/getProductList";
    }
    
    @GetMapping("/deleteProduct/{productNo}")
    public String deleteProduct(@PathVariable int productNo) throws Exception {
        productService.deleteProduct(productNo);
        return "redirect:/product/getProductList";
    }
    
}
