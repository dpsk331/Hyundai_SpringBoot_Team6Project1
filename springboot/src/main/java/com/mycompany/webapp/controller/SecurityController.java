package com.mycompany.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/security")
public class SecurityController {
	
	@RequestMapping("/content")
	public String content() {
		log.info("Run");

		return "security/content";
	}

	@RequestMapping("/loginForm")
	public String loginForm() {
		log.info("Run");
		
		return "security/loginForm";
	}
	
	@RequestMapping("/loginError")
	public String loginError(Model model) {
		log.info("Run");
		
		model.addAttribute("loginError", true);
		
		return "security/loginForm";
	}
	
	@RequestMapping("/403")
	public String accessDenied() {
		log.info("Run");
		
		return "error/403";
	}
	
	@RequestMapping("/admin/action")
	public String adminAction() {
		log.info("Run");
		
		return "redirect:/security/content";
	}
	   
	@RequestMapping("/manager/action")
	public String managerAction() {
		log.info("Run");
		
		return "redirect:/security/content";
	}
   
	@RequestMapping("/user/action")
	public String userAction() {
		log.info("Run");
		
		return "redirect:/security/content";
	}
	
}
