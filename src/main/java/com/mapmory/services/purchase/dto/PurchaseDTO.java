package com.mapmory.services.purchase.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PurchaseDTO {
	private int purchaseNo;
    private String userId;
    private int productNo;
    private int paymentMethod;
	private String paymentMethodString; // 1: 카드결제, 2: 카카오페이, 3: 페이코, 4: 토스페이
    private String cardType;
    private String lastFourDigits;
    private LocalDateTime purchaseDate;
    private String purchaseDateString;
    private int price;
    private String productTitle; // Product 정보
    private int period; // Product 정보
}
