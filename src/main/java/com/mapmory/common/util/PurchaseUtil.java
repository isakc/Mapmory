package com.mapmory.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PurchaseUtil {
	
	public static String paymentChange(int paymentMethod) throws Exception{
		switch (paymentMethod) {
			case 1:
				return "카드";
				
			case 2:
				return "카카오페이";
				
			case 3:
				return "페이코";
				
			case 4:
				return "토스페이";
			}
		
		return "잘못 입력함";
	}
	
	public static String purchaseDateChange(LocalDateTime date) throws Exception{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd");
		
		return date.format(formatter);
	}
}
