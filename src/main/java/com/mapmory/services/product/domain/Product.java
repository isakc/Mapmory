package com.mapmory.services.product.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.mapmory.services.purchase.domain.Purchase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
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
    private LocalDateTime productRegDate;   // 상품 등록 일자
    private int period;                     // 구분자
    private String userId;                  // 상품 등록한 사용자 아이디

    public String getFormattedProductRegDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return formatter.format(productRegDate);
    }
}
