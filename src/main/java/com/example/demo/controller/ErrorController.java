package com.example.demo.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.model.User;
import com.example.demo.repository.AtividadeRepository;
import com.example.demo.repository.UserRepository;

@Controller
public class ErrorController {

	@Autowired
	private AtividadeRepository atividadeRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping("/403")
	public String error403(Principal principal, Model model) {
		User user = userRepository.findByEmail(principal.getName());
		model.addAttribute("novoUser", new User());
		model.addAttribute("user", user);
		return "error/403";
	}
	
	@GetMapping("/404")
	public String error404(Principal principal, Model model) {
		User user = userRepository.findByEmail(principal.getName());
		model.addAttribute("novoUser", new User());
		model.addAttribute("user", user);
		return "error/403";
	}
}
