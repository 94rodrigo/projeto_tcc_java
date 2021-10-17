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
import com.example.demo.model.EstadoAtividade;
import com.example.demo.model.User;
import com.example.demo.repository.AtividadeRepository;
import com.example.demo.repository.UserRepository;

@Controller
@RequestMapping("dashboard")
public class DashboardController {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	AtividadeRepository atividadeRepository;
	
	@GetMapping
	public String home(Model model, Principal principal) {
		User user = userRepository.findByEmail(principal.getName());
		List<Atividade> findAllByMunicipioEstado = atividadeRepository.findAllByMunicipioEstado(user.getMunicipio(), EstadoAtividade.CONFIRMADO);
		findAllByMunicipioEstado.sort(Comparator.comparing(Atividade::getDataHorarioAtividade));
		
		List<Atividade> atividadesConfirmadasUsuario = atividadeRepository.findAllByUsuarioEDataAtual(user.getEmail(), EstadoAtividade.CONFIRMADO);
		atividadesConfirmadasUsuario.sort(Comparator.comparing(Atividade::getDataHorarioAtividade));
		
		List<User> usuariosCadastradosNaAtividade = atividadesConfirmadasUsuario.get(0).getUsuariosCadastradosNaAtividade();
		
		List<Atividade> atividadesPendentesUsuario = atividadeRepository.findAllByUsuarioEEstado(user.getEmail(), EstadoAtividade.PENDENTE);
		
		Atividade proximaAtividadeCidade = findAllByMunicipioEstado.get(0);
		
		List<Atividade> atividadesQueUsuariosParticipa = user.getAtividadesQueUsuariosParticipa();
		atividadesQueUsuariosParticipa.sort(Comparator.comparing(Atividade::getDataHorarioAtividade));
		Atividade proximaAtividadeUsuario = atividadesQueUsuariosParticipa.get(0);
		
		model.addAttribute("user", user);
		model.addAttribute("novoUser", new User());
		model.addAttribute("atividadesLocal", findAllByMunicipioEstado);
		model.addAttribute("atividadesConfirmadasUsuario", atividadesConfirmadasUsuario);
		model.addAttribute("atividadesPendentesUsuario", atividadesPendentesUsuario);
		model.addAttribute("usuariosCadastradosNaAtividade", usuariosCadastradosNaAtividade);
		model.addAttribute("proximaAtividadeCidade", proximaAtividadeCidade);
		model.addAttribute("proximaAtividadeUsuario", proximaAtividadeUsuario);
		return "dashboard";
	}
	
	
}
