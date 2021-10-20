package com.mycompany.webapp.exception;

import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

//객체로 생성해서 관리하도록 설정
@Component
//모든 컨트롤러에 영향을 미치는 설정
@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {
	
	@ExceptionHandler
	public String handleNotAuthenticatedUserException(NotAuthenticatedUserException e) {
		log.info("로그인 되지 않은 유저");
		e.printStackTrace();
		return "redirect:/member/loginForm";
	}
	
	@ExceptionHandler
	public String OutOfStockException(OutOfStockExceptionHandler e, Model model) {
		log.info("Run / OutOfStockException");
		e.printStackTrace();
		model.addAttribute("message", e.getMessage());
		return "error/OutOfStockException";
	}

	@ExceptionHandler
	public String handleUnauthorizedException(UnauthorizedException e) { 
		log.info("handleUnauthorizedException 실행");
		e.printStackTrace();
		return "error/401";
	}
	
	@ExceptionHandler
	public String handleForbiddenException(ForbiddenException e) { 
		log.info("handleForbiddenException 실행");
		e.printStackTrace();
		return "error/403";
	}
	
	@ExceptionHandler
	public String handleNotFoundException(NotFoundException e) { 
		log.info("handleNotFoundException 실행");
		e.printStackTrace();
		return "error/404";
	}
	
	@ExceptionHandler
	public String handleNullPointerException(NullPointerException e) {
		log.info("NullPointException");
		e.printStackTrace();
		return "error/500";
	}
	
	@ExceptionHandler
	public String handleOtherException(Exception e) {
		log.info("실행");
		e.printStackTrace();
		return "error/500";
	}

}
