package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Atividade;
import com.example.demo.model.EstadoAtividade;
import com.example.demo.repository.AtividadeRepository;

@Service
public class AtividadeService {
	
	@Autowired
	private AtividadeRepository atividadeRepository;
	
	public List<Atividade> listarResultadosPorNomeOuDescricao(String keyword){
		if (keyword != null) {
			return atividadeRepository.buscaNomeAtividadeOuDescricao(keyword);
		}
		return atividadeRepository.findAll();
	}
	
	public List<Atividade> listarResultadosPorTipoDeAtividade(String keyword){
		if (keyword != null) {
			if (keyword.equalsIgnoreCase("corrida")) {
				keyword = "1";
			}
			else if (keyword.equalsIgnoreCase("caminhada")) {
				keyword = "2";
			}
			else if (keyword.equalsIgnoreCase("ciclismo")) {
				keyword = "3";
			}
			else if (keyword.equalsIgnoreCase("Esportes coletivos") ||
					keyword.toLowerCase().contains("esportes") ||
					keyword.toLowerCase().contains("coletivos")) {
				keyword = "4";
			}
			else if (keyword.equalsIgnoreCase("natação") ||
					keyword.equalsIgnoreCase("natacao")) {
				keyword = "5";
			}
			else if (keyword.equalsIgnoreCase("musculação") ||
					keyword.equalsIgnoreCase("musculacao")) {
				keyword = "6";
			}
			else if (keyword.equalsIgnoreCase("treino funcional") ||
					keyword.toLowerCase().contains("treino") ||
					keyword.toLowerCase().contains("funcional")) {
				keyword = "7";
			}
			else if (keyword.equalsIgnoreCase("outros")) {
				keyword = "8";
			}
			else {
				System.out.println("Número da atividade: " + keyword);
				return atividadeRepository.findAll();
			}
			System.out.println("Número da atividade: " + keyword);
			return atividadeRepository.buscaPorTipoAtividade(keyword);
		}
		System.out.println("Número da atividade: " + keyword);
		return atividadeRepository.findAll();
	}
	
	public List<Atividade> listarResultadosPorLocal(String keyword){
		if (keyword != null) {
			return atividadeRepository.buscaLocal(keyword, EstadoAtividade.CONFIRMADO);
		}
		return atividadeRepository.findAll();
	}
	
	public void deletarAtividade(Long id) {
		atividadeRepository.deleteUserAtividadeByAtividadeId(id);
		atividadeRepository.deleteById(id);
	}

}
