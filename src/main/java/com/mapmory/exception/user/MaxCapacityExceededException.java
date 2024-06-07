package com.mapmory.exception.user;

public class MaxCapacityExceededException extends RuntimeException {

	public MaxCapacityExceededException(String message) {
		super(message);
	}
}
