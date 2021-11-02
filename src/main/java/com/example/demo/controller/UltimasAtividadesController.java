package com.example.demo.controller;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.model.Atividade;
import com.example.demo.model.User;
import com.example.demo.service.UserService;

@Controller
@RequestMapping("ultimasAtividades")
public class UltimasAtividadesController {

	@Autowired
	private UserService userService;
	
	@GetMapping
	public String paginaUltimasAtividades(Principal principal, Model model) {
		User user = userService.findByEmail(principal.getName());
		List<Atividade> atividadesQueUsuariosParticipa = user.getAtividadesQueUsuariosParticipa();
		atividadesQueUsuariosParticipa.sort(Comparator.comparing(Atividade::getDataHorarioAtividade));
		model.addAttribute("user", user);
		model.addAttribute("atividadesQueUsuariosParticipa", atividadesQueUsuariosParticipa);
		model.addAttribute("novoUser", new User());
		return "atividade/ultimas_atividades";
	}
}
