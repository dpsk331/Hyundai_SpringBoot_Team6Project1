package com.mycompany.webapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends RuntimeException {

	public ForbiddenException() {
        super("잘못된 접근입니다.");
    }
	
    public ForbiddenException(String message) {
        super(message);
    }
    
}
