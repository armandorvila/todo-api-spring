package com.armandorv.poc.tasks.exception;

public class UserAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 5312177818790289069L;

	public UserAlreadyExistsException() {
		super();
	}

	public UserAlreadyExistsException(String message) {
		super(message);
	} 
	
}
