package com.example.demo.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.model.User;
import com.example.demo.service.UserService;

@Controller
@RequestMapping
public class CadastroController {

	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	public CadastroController(BCryptPasswordEncoder bCryptPasswordEncoder, UserService userService) {
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.userService = userService;
	}
	
	@GetMapping("/cadastro")
	public String cadastro(Model model) {
		model.addAttribute("user", new User());
		return "cadastro";
	}
	
	@PostMapping("/cadastro")
	public String novoUsuario(@Valid User user, BindingResult result, Model model) {
		User usuarioExiste = userService.findByEmail(user.getEmail());
		System.out.println(usuarioExiste);
		
		if (usuarioExiste != null) {
			return "cadastro";
		}
		
		if (result.hasErrors()) {
			System.out.println(result.hasErrors());
			List<ObjectError> allErrors = result.getAllErrors();
			for (ObjectError objectError : allErrors) {
				System.out.println(objectError.getDefaultMessage());
			}
			return "cadastro";
		}
		
		model.addAttribute("message", "Valid form");
		String encodedPassword = bCryptPasswordEncoder.encode(user.getSenha());
		String encodedPasswordConfirm = bCryptPasswordEncoder.encode(user.getConfirmacaoSenha());
		user.setSenha(encodedPassword);
		user.setConfirmacaoSenha(encodedPasswordConfirm);
		user.setEnabled(true);
		userService.saveUser(user);
		
		return "login";
	}
}
