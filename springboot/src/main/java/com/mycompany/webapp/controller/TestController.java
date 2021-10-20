package com.mycompany.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mycompany.webapp.exception.ForbiddenException;
import com.mycompany.webapp.exception.NotFoundException;
import com.mycompany.webapp.exception.UnauthorizedException;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class TestController {
	
	@RequestMapping("/401")
	public void error401() {
		log.info("Run");
		
		throw new UnauthorizedException();
	}
	
	@RequestMapping("/403")
	public void error403() {
		log.info("Run");
		
		throw new ForbiddenException();
	}
	
	@RequestMapping("/404")
	public void error404() {
		log.info("Run");
		
		throw new NotFoundException();
	}
	
	@RequestMapping("/500")
	public void error500() {
		log.info("Run");
		
		throw new NullPointerException();
	}
}
