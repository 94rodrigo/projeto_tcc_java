package com.example.demo.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.model.LogDeAcoes;
import com.example.demo.model.User;
import com.example.demo.repository.LogRepository;
import com.example.demo.repository.UserRepository;

@Controller
@RequestMapping("/logAdmin")
public class LogController {

	@Autowired
	private LogRepository logRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping
	public String logAcoes(Model model, Principal principal) {
		List<LogDeAcoes> logDeAcoes = logRepository.findAll();
		User usuarioLogado = userRepository.findByEmail(principal.getName());
		model.addAttribute("usuarioLogado", usuarioLogado);
		model.addAttribute("user", usuarioLogado);
		model.addAttribute("logDeAcoes", logDeAcoes);
		model.addAttribute("novoUser", new User());
		return "admin_pages/log_acoes_admin";
	}
}
