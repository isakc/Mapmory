package com.mapmory.exception.purchase;

public class SubscriptionException extends RuntimeException {
	
    public SubscriptionException() {
        super("구독 결제중 에러 발생");
    }
    
    public SubscriptionException(String message) {
        super(message);
    }

    public SubscriptionException(String message, Throwable cause) {
        super(message, cause);
    }
}