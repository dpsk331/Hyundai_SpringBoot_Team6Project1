package com.mycompany.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.java.Log;

@Controller
@Log
public class HomeController {
//	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@RequestMapping("/")
	public String home() {
//		logger.error("error"); logger.warn("warn"); logger.info("info");
//		logger.debug("debug");
		
		log.info("Run");
		
		return "home";
	}
	
}
