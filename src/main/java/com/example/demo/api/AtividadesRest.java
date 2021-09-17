package com.example.demo.api;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Atividade;
import com.example.demo.model.EstadoAtividade;
import com.example.demo.repository.AtividadeRepository;

@RestController
@RequestMapping("/api/atividades")
public class AtividadesRest {

	@Autowired
	private AtividadeRepository atividadeRepository;
	
	@GetMapping("confirmadas/user")
	public List<Atividade> getAtividadesUsuarioConfirmadas(Principal principal){
		return atividadeRepository.findAllByUsuarioEEstado(principal.getName(), EstadoAtividade.CONFIRMADO);
	}
	
	@GetMapping("confirmadas/todas")
	public List<Atividade> getAtividadesConfirmadas(){
		return atividadeRepository.findAllByEstado(EstadoAtividade.CONFIRMADO);
	}
}
