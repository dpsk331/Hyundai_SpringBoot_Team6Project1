package com.mycompany.webapp.exception;

public class UnauthorizedException extends RuntimeException{
	public UnauthorizedException() {
		super("허용되지 않은 접근입니다");
	}
	public UnauthorizedException(String message) {
		super(message);
	}
}
