package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Atividade;
import com.example.demo.repository.AtividadeRepository;

@Service
public class AtividadeService {
	
	@Autowired
	private AtividadeRepository atividadeRepository;
	
	public List<Atividade> listarResultados(String keyword){
		if (keyword != null) {
			return atividadeRepository.buscaNomeAtividadeOuDescricao(keyword);
		}
		return atividadeRepository.findAll();
	}

}
