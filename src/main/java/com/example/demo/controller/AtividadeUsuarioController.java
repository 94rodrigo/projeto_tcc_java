package com.example.demo.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.model.Atividade;
import com.example.demo.model.LogDeAcoes;
import com.example.demo.model.User;
import com.example.demo.repository.AtividadeRepository;
import com.example.demo.repository.LogRepository;
import com.example.demo.repository.UserRepository;

@Controller
public class AtividadeUsuarioController {

	@Autowired
	private AtividadeRepository atividadeRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private LogRepository logRepository;
	
	@PostMapping("/atividade/participar/{id}")
	public String participar(@PathVariable Long id, Principal principal) {
		User user = userRepository.findByEmail(principal.getName());
		Atividade atividade = atividadeRepository.findById(id).get();
		user.adicionarAtividade(atividade);
		atividade.incluirUsuario(user);
		atividadeRepository.save(atividade);
		userRepository.save(user);
		logRepository.save(new LogDeAcoes(user, atividade, "Confirmou presença na atividade ".concat(atividade.getNomeAtividade())));
		System.out.println(user.getAtividadesQueUsuariosParticipa());
		return "redirect:/todas";
	}
	
	@PostMapping("/atividade/cancelar_participacao/{id}")
	public String cancelarParticipacao(@PathVariable Long id, Principal principal) {
		User user = userRepository.findByEmail(principal.getName());
		Atividade atividade = atividadeRepository.findById(id).get();
		userRepository.deleteByIdUserAtividades(user.getId(), atividade.getId());
		logRepository.save(new LogDeAcoes(user, atividade, "Cancelou presença na atividade ".concat(atividade.getNomeAtividade())));
		return "redirect:/todas";
	}
	
	@PostMapping("/atividade/participar_info/{id}")
	public String participarInfo(@PathVariable Long id, Principal principal) {
		User user = userRepository.findByEmail(principal.getName());
		Atividade atividade = atividadeRepository.findById(id).get();
		user.adicionarAtividade(atividade);
		atividade.incluirUsuario(user);
		atividadeRepository.save(atividade);
		userRepository.save(user);
		System.out.println(user.getAtividadesQueUsuariosParticipa());
		logRepository.save(new LogDeAcoes(user, atividade, "Confirmou presença na atividade ".concat(atividade.getNomeAtividade())));
		return "redirect:/atividade/{id}";
	}
	
	@PostMapping("/atividade/cancelar_participacao_info/{id}")
	public String cancelarParticipacaoInfo(@PathVariable Long id, Principal principal) {
		User user = userRepository.findByEmail(principal.getName());
		Atividade atividade = atividadeRepository.findById(id).get();
		userRepository.deleteByIdUserAtividades(user.getId(), atividade.getId());
		logRepository.save(new LogDeAcoes(user, atividade, "Cancelou presença na atividade ".concat(atividade.getNomeAtividade())));
		return "redirect:/atividade/{id}";
	}
	
}
