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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.PageAttributes.MediaType;
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

    @GetMapping("/addProduct")
    public String addProduct() {
        return "product/admin/addProduct";
    }
    
    @PostMapping("/addProduct")
    public String addProduct(@ModelAttribute("product") Product product,
                             @RequestParam("uploadFile") List<MultipartFile> files) throws Exception {
        // 각 이미지 파일에 대해 UUID 생성하여 처리
        List<String> uuidFileNames = new ArrayList<>();
        List<String> originalFileNames = new ArrayList<>();
        for (MultipartFile file : files) {
            String uuid = ImageFileUtil.getProductImageUUIDFileName(file.getOriginalFilename());
            String originalFilename = file.getOriginalFilename();
            objectStorageUtil.uploadFileToS3(file, uuid); // 파일 업로드 시 UUID 값 전달
            uuidFileNames.add(uuid);
            originalFileNames.add(originalFilename);
        }
        productService.addProduct(product, uuidFileNames, originalFileNames);
        return "redirect:/product/getProductList";
    }
    
    @GetMapping("/getProductList")
    public String getProductList(@ModelAttribute("search") Search search, Model model) throws Exception {
    	
    	if(search.getCurrentPage() == 0) {
			search.setCurrentPage(1);
		}
		
		search.setPageSize(pageSize);
		
        Map<String, Object> map = productService.getProductList(search);
        List<Product> productList = (List<Product>) map.get("productList");
        
        int totalCount = (int) map.get("productTotalCount");
        
        System.out.println("productList =====" + productList);

        model.addAttribute("productList", productList);
        model.addAttribute("search", search);
        model.addAttribute("totalCount", totalCount);

        return "product/getProductList";
    }
    
    @GetMapping("/getDetailProduct/{productNo}")
    public String getDetailProduct(@PathVariable int productNo, Model model) throws Exception {
    	System.out.println("GetDetailProduct Start......");
    	
    	Product product = productService.getDetailProduct(productNo);
    	model.addAttribute("product",product);
    	return "product/admin/getDetailProduct";
    }
    
    
    @PostMapping("/updateProduct/{productNo}")
    public String updateProduct(@ModelAttribute("update") Product product,
                                @PathVariable int productNo,
                                @RequestParam(value = "uploadFile", required = false) List<MultipartFile> files,
                                @RequestParam(value = "deleteImageUuids", required = false) List<String> deleteImageUuids) throws Exception {

        List<String> uuidFileName = new ArrayList<>();

        // 새로운 이미지 파일 업로드 및 UUID 생성
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) { // 빈 파일이 아닌 경우에만 처리
                    String uuid = ImageFileUtil.getProductImageUUIDFileName(file.getOriginalFilename());
                    objectStorageUtil.uploadFileToS3(file, uuid); // 파일 업로드
                    uuidFileName.add(uuid);
                }
            }
        }

        // 삭제할 이미지 UUID 목록 처리
        if (deleteImageUuids != null && !deleteImageUuids.isEmpty()) {
            for (String uuid : deleteImageUuids) {
                productService.deleteImage(uuid);
            }
        }

        // 상품 정보 업데이트
        productService.updateProduct(product, uuidFileName);

        return "redirect:/product/getDetailProduct/" + productNo;
    }
    
    @GetMapping("/updateProduct/{productNo}")
    public String showUpdateProductForm(@PathVariable int productNo, Model model) throws Exception {
        Product product = productService.getDetailProduct(productNo);
        model.addAttribute("product", product);
        return "product/admin/updateProduct"; // 상품 수정 페이지로 이동
    }
    
    @GetMapping("/deleteProduct/{productNo}")
    public String deleteProduct(@PathVariable int productNo) throws Exception {
        productService.deleteProduct(productNo);
        return "redirect:/product/getProductList";
    }
    
    @PostMapping("/deleteImage/{uuid}")
    @ResponseBody
    public ResponseEntity<String> deleteImage(@PathVariable String uuid) {
        try {
            productService.deleteImage(uuid);
            return ResponseEntity.ok("이미지 삭제 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 삭제 중 오류 발생");
        }
    }
}
