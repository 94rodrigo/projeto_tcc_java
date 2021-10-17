package com.example.demo.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.model.Atividade;
import com.example.demo.model.EstadoAtividade;
import com.example.demo.model.User;
import com.example.demo.repository.AtividadeRepository;
import com.example.demo.repository.UserRepository;

@Controller
@RequestMapping
public class AprovarAtividadesController {

	@Autowired
	private AtividadeRepository atividadeRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping("/aprovarAtividades")
	public String aprovarAtividadesPage(Principal principal, Model model) {
		User user = userRepository.findByEmail(principal.getName());
		List<Atividade> atividadesPendentes = atividadeRepository.findAllByEstado(EstadoAtividade.PENDENTE);
		model.addAttribute("user", user);
		model.addAttribute("novoUser", new User());
		model.addAttribute("atividades", atividadesPendentes);
		return "admin_pages/atividades_aprovacao";
	}
	
	@PostMapping("/atividade/{id}/aprovar")
	public String aprovarAtividade(@PathVariable Long id) {
		atividadeRepository.alterarEstadoAtividade(EstadoAtividade.CONFIRMADO, id);
		return "redirect:/aprovarAtividades";
	}
	
	@PostMapping("/atividade/{id}/rejeitar")
	public String rejeitarAtividade(@PathVariable Long id) {
		atividadeRepository.alterarEstadoAtividade(EstadoAtividade.REJEITADO, id);
		return "redirect:/aprovarAtividades";
	}
}
