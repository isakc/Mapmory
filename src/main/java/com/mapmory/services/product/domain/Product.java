package com.mapmory.services.product.domain;

import java.sql.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Product {
    
    private int productNo;                  // 상품 번호
    private String productTitle;            // 상품 제목
    private int price;                      // 상품 가격
    private Date productRegDate;   // 상품 등록 일자
    private int period;                     // 구분자
    private String userId;                  // 상품 등록한 사용자 아이디
    private List<String> uuid;
    

}
