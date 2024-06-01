package com.mapmory.services.product.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import lombok.Data;

@Data
public class Product {
	
	private int productNo;					//상품 번호		
	private int productTitle;				//상품 제목
	private String productImage;			//상품 UUID 이름
	private int price;						//상품 가격
	private LocalDateTime productRegDate;	//상품 등록 일자
	private int period;						//구분자
	private String userId;					//상품 등록한 사람
	
	public String getproductRegDate() {
    	DateTimeFormatter product = DateTimeFormatter.ofPattern("yyyy-mm-dd");
    	return product.format(productRegDate);
	}
	
	public void setProductImage(List<String> productImages) {
        this.productImage = productImage;
    }
}
