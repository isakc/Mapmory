package com.mapmory.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.mapmory.exception.purchase.PaymentValidationException;
import com.mapmory.exception.purchase.SubscriptionException;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(SubscriptionException.class)
	public String handleSubscriptionException(SubscriptionException e, Model model) {
		model.addAttribute("errorMessage", e.getMessage());
		
		return "common/error"; // 공통 에러 페이지
		
	}// handleSubscriptionException: 구독 에러 처리

	@ExceptionHandler(PaymentValidationException.class)
	public String handlePaymentValidationException(PaymentValidationException e, Model model) {
		model.addAttribute("errorMessage", e.getMessage());
		
		return "common/error"; // 공통 에러 페이지
		
	}// handlePaymentValidationException: 구매 중 에러

	// @ExceptionHandler(Exception.class)
	public String handleGeneralException(Exception e, Model model) {
		// model.addAttribute("errorMessage", "예기치 않은 오류가 발생했습니다. 관리자에게 문의하세요.");
		model.addAttribute("errorMessage", e.getMessage());
		
		System.out.println(e.getMessage());
		return "common/error"; // 공통 에러 페이지
	}// handleGeneralException: 공통 예외 처리
}
