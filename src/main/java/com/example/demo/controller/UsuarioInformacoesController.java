package com.example.demo.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.model.Atividade;
import com.example.demo.model.EstadoAtividade;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

@Controller
public class UsuarioInformacoesController {

	@Autowired
	private UserRepository userRepository;
	
	@GetMapping("/informacoesUsuario/{id}")
	public String informacoesUsuario(@PathVariable Long id, Model model, Principal principal) {
		User usuarioLogado = userRepository.findByEmail(principal.getName());
		User usuarioCadastro = userRepository.findById(id).get();
		
		List<Atividade> usuarioParticipa = new ArrayList<>();
		List<Atividade> usuarioParticipou = new ArrayList<>();
		
		if (usuarioCadastro.getAtividadesQueUsuariosParticipa().isEmpty() || usuarioCadastro.getAtividadesQueUsuariosParticipa() == null) {
			model.addAttribute("usuarioParticipa", "-");
			model.addAttribute("usuarioParticipou", "-");
		}else {
			for(Atividade atividade : usuarioCadastro.getAtividadesQueUsuariosParticipa()) {
				if (atividade.getDataHorarioAtividade().isAfter(LocalDateTime.now()) &&  atividade.getEstadoAtividade().equals(EstadoAtividade.CONFIRMADO))
					usuarioParticipa.add(atividade);
				else if (atividade.getDataHorarioAtividade().isBefore(LocalDateTime.now()) &&  atividade.getEstadoAtividade().equals(EstadoAtividade.CONFIRMADO))
					usuarioParticipou.add(atividade);
			}
			model.addAttribute("usuarioParticipa", usuarioParticipa);
			model.addAttribute("usuarioParticipou", usuarioParticipou);
		}
		
		model.addAttribute("usuarioLogado", usuarioLogado);
		model.addAttribute("user", usuarioLogado);
		model.addAttribute("novoUser", new User());
		model.addAttribute("usuarioCadastro", usuarioCadastro);
		return "admin_pages/usuario_informacoes";
	}
	
	@PostMapping("/inativaUsuario/{id}")
	public String inativaUsuario(@PathVariable Long id) {
		userRepository.ativaOuInativaUsuario(false, userRepository.findById(id).get().getEmail());
		return "redirect:/informacoesUsuario/{id}";
	}
	
	@PostMapping("/ativaUsuario/{id}")
	public String ativaUsuario(@PathVariable Long id) {
		userRepository.ativaOuInativaUsuario(true, userRepository.findById(id).get().getEmail());
		return "redirect:/informacoesUsuario/{id}";
	}
}
