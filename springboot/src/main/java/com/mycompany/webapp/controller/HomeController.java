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

		return "main";
	}


	  // error페이지 test용도
	  
	  @RequestMapping("/error/401") public String error1() { return "error/401"; }
	  
	  @RequestMapping("/error/403") public String error2() { return "error/403"; }
	  
	  @RequestMapping("/error/404") public String error3() { return "error/404"; }
	  
	  @RequestMapping("/error/500") public String error4() { return "error/500"; }
	 

}
