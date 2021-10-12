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
@RequestMapping("/aprovarAtividades")
public class AprovarAtividadesController {

	@Autowired
	private AtividadeRepository atividadeRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping
	public String aprovarAtividadesPage(Principal principal, Model model) {
		User user = userRepository.findByEmail(principal.getName());
		List<Atividade> atividadesPendentes = atividadeRepository.findAllByEstado(EstadoAtividade.PENDENTE);
		model.addAttribute("user", user);
		model.addAttribute("novoUser", new User());
		model.addAttribute("atividades", atividadesPendentes);
		return "admin_pages/atividades_aprovacao";
	}
	
	@PostMapping("/aprovar/{id}")
	public String aprovarAtividade(@PathVariable Long id) {
		Atividade atividadeEncontrada = atividadeRepository.findById(id).get();
		atividadeEncontrada.setEstadoAtividade(EstadoAtividade.CONFIRMADO);
		atividadeRepository.save(atividadeEncontrada);
		return "redirect:/aprovarAtividades";
	}
	
	@PostMapping("/rejeitar/{id}")
	public String rejeitarAtividade(@PathVariable Long id) {
		Atividade atividadeEncontrada = atividadeRepository.findById(id).get();
		atividadeEncontrada.setEstadoAtividade(EstadoAtividade.REJEITADO);
		atividadeRepository.save(atividadeEncontrada);
		return "redirect:/aprovarAtividades";
	}
}
