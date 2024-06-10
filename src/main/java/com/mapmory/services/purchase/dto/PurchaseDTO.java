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
    private String cardType;
    private String lastFourDigits;
    private LocalDateTime purchaseDate;
    private int price;
    private String productTitle; // Product 정보
    private int period; // Product 정보
}
