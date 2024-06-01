package com.mapmory.services.product.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ProductImage {
	
	private String uuid;					//상품 UUID 값
	private int productNo;					//이미지가 들어간 상품 번호
	private String imageFile;				//상품 원본 이름
	private LocalDateTime imageRegDate;		//이미지 등록 일시

}
