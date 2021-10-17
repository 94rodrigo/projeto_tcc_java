package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {

	@RequestMapping("/login")
	public String login() {
		return "login";
	}
	
	@RequestMapping("/login-error")
	public String loginError(Model model) {
		String erro = "Login ou senha inválidos!";
		model.addAttribute("loginError", true);
		model.addAttribute("msgError", erro);
		return "login";
	}
}
