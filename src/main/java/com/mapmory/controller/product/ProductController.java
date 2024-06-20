package com.mapmory.controller.product;

import com.mapmory.services.product.domain.Product;
import com.mapmory.services.product.domain.ProductImage;
import com.mapmory.services.product.service.ProductService;
import com.mapmory.common.domain.Page;
import com.mapmory.common.domain.Search;
import com.mapmory.common.domain.SessionData;
import com.mapmory.common.util.ImageFileUtil;
import com.mapmory.common.util.ObjectStorageUtil;
import com.mapmory.common.util.RedisUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/product/*")
public class ProductController {

    @Autowired
    @Qualifier("productServiceImpl")
    private ProductService productService;

    @Autowired
    private ObjectStorageUtil objectStorageUtil; // ObjectStorageUtil 추가
    
    @Autowired
    private RedisUtil<SessionData> redisUtil;
    
    @Value("${page.Unit}")
    int pageUnit;

    @Value("${page.Size}")
    int pageSize;
    
    @Value("${object.folderName}")
    private String folderName;
    
    public ProductController() {
		System.out.println(this.getClass());
	}

    @GetMapping("/addProduct")  //상품 추가 
    public String addProduct() {
        return "product/admin/addProduct";
    }
    
    @PostMapping("/addProduct") //상품 추가
    public String addProduct(@ModelAttribute("product") Product product,
                             @RequestParam("uploadFile") List<MultipartFile> files,
                             @RequestParam("imageTag") List<String> imageTag) throws Exception {
        // 각 이미지 파일에 대해 UUID 생성하여 처리
        List<String> uuidFileNames = new ArrayList<>();
        List<String> originalFileNames = new ArrayList<>();
        for (MultipartFile file : files) {
            String uuid = ImageFileUtil.getImageUUIDFileName(file.getOriginalFilename());
            String originalFilename = file.getOriginalFilename();
            objectStorageUtil.uploadFileToS3(file, uuid,folderName); // 파일 업로드 시 UUID 값 전달
            uuidFileNames.add(uuid);
            originalFileNames.add(originalFilename);
        }
        productService.addProduct(product, uuidFileNames, originalFileNames, imageTag);
        return "redirect:/product/getAdminProductList";
    }
    
    @GetMapping("/getProductList") //상품 목록 조회 
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
    
    @GetMapping("/getAdminProductList") //상품 목록 조회 
    public String getAdminProductList(@ModelAttribute("search") Search search, Model model) throws Exception {
    	
    	if(search.getCurrentPage() == 0) {
			search.setCurrentPage(1);
		}
		
		search.setPageSize(pageSize);
		
        Map<String, Object> map = productService.getProductList(search);
        
        Page resultPage = new Page(search.getCurrentPage(),((Integer)map.get("productTotalCount")).intValue(),pageUnit,pageSize);
        
        System.out.println("상품 페이지 확인용 ::::::::::::::::: " + resultPage);
        

        model.addAttribute("productList", map.get("productList"));
        model.addAttribute("search", search);
        model.addAttribute("resultPage", resultPage);

        return "product/admin/getAdminProductList";
    }
    
    @GetMapping("/getDetailProduct/{productNo}") //상품 상세보기
    public String getDetailProduct(@PathVariable int productNo, Model model) throws Exception {
    	System.out.println("GetDetailProduct Start......");
    	
    	Product product = productService.getDetailProduct(productNo);
    	model.addAttribute("product",product);
    	return "product/getDetailProduct";
    }
    
    @GetMapping("/getAdminDetailProduct/{productNo}") //상품 상세보기
    public String getAdminDetailProduct(@PathVariable int productNo, Model model) throws Exception {
    	System.out.println("GetDetailProduct Start......");
    	
    	Product product = productService.getDetailProduct(productNo);
    	model.addAttribute("product",product);
    	return "product/admin/getAdminDetailProduct";
    }
    
    
    @PostMapping("/updateProduct/{productNo}")
    public String updateProduct(@ModelAttribute("update") Product product,
                                Model model,
                                @PathVariable int productNo,
                                @RequestParam(value = "uploadFile", required = false) List<MultipartFile> files,
                                @RequestParam(value = "deleteImageUuids", required = false) List<String> deleteImageUuids,
                                @RequestParam(value = "imageTags", required = false) List<String> imageTags,
                                @RequestParam(value = "newImageTags", required = false) List<String> newImageTags,
                                HttpServletRequest request) throws Exception {
        product.setUserId("admin");
        System.out.println("이미지 태그입니다 ::::::::::::::::::::::: " + imageTags);
        product.setNewImageTags(newImageTags);
        product.setImageTags(imageTags); // 상품에 이미지 태그 설정

        // 새로운 이미지 파일 업로드 및 UUID 생성
        List<String> uuidFileNames = new ArrayList<>();
        List<String> originalFileNames = new ArrayList<>();
        if (files != null) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String uuid = ImageFileUtil.getImageUUIDFileName(file.getOriginalFilename());
                    String originalFilename = file.getOriginalFilename();
                    objectStorageUtil.uploadFileToS3(file, uuid, folderName); // 파일 업로드
                    uuidFileNames.add(uuid);
                    originalFileNames.add(originalFilename);
                }
            }
        }

        // 삭제할 이미지 UUID 목록 처리
        if (deleteImageUuids != null) {
            for (String uuid : deleteImageUuids) {
                productService.deleteImage(uuid, folderName);
            }
        }

        // 상품 정보 업데이트
        productService.updateProduct(product, uuidFileNames, originalFileNames,imageTags,newImageTags);

        return "redirect:/product/getProductList";
    }


    
    @GetMapping("/updateProduct/{productNo}") //상품 업데이트 
    public String showUpdateProductForm(@PathVariable int productNo, Model model) throws Exception {
        Product product = productService.getDetailProduct(productNo);
        model.addAttribute("product", product);
        return "product/admin/updateProduct"; // 상품 수정 페이지로 이동
    }
    
    @GetMapping("/deleteProduct/{productNo}") //상품 삭제
    public String deleteProduct(@PathVariable int productNo) throws Exception {
        productService.deleteProduct(productNo,folderName);
        return "redirect:/product/getProductList";
    }
    
    @PostMapping("/deleteImage/{uuid}")  //이미지 삭제
    @ResponseBody
    public ResponseEntity<String> deleteImage(@PathVariable String uuid) {
        try {
            productService.deleteImage(uuid,folderName);
            return ResponseEntity.ok("이미지 삭제 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 삭제 중 오류 발생");
        }
    }
    
 // 상품 이미지 주소 가리기 위한 컨트롤러
    @GetMapping("/image/{uuid}")
    @ResponseBody
    public byte[] getImage(@PathVariable String uuid) throws Exception {
        byte[] bytes = productService.getImageBytes(uuid, folderName);
        return bytes;
    }
   
    @GetMapping("/image")
    @ResponseBody
    public String getImageUrlByImageTag(@RequestParam("imageTag") String imageTag) {
        try {
            return productService.getImageUrlByImageTag(imageTag,folderName);
        } catch (Exception e) {
            return "서버 오류 발생";
        }
    }
}