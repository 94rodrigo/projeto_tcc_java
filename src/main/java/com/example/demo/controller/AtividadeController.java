package com.example.demo.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.dto.RequisicaoNovaAtividade;
import com.example.demo.model.Atividade;
import com.example.demo.model.EstadoAtividade;
import com.example.demo.model.User;
import com.example.demo.repository.AtividadeRepository;
import com.example.demo.repository.UserRepository;

@Controller
@RequestMapping
public class AtividadeController {

	@Autowired
	private AtividadeRepository atividadeRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping("/todas")
	public String todasAsAtividades(Model model) {
		List<Atividade> atividades = atividadeRepository.findAll();
		model.addAttribute("atividades", atividades);
		return "atividade/atividades_todas";
	}
	
	@GetMapping("/atividadesForm")
	public String atividadesForm(Model model) {
		model.addAttribute("requisicaoNovaAtividade", new RequisicaoNovaAtividade());
		return "atividade/form";
	}
	
	@PostMapping("/atividadesFormGravar")
	public String novaAtividade(@Valid RequisicaoNovaAtividade requisicao, BindingResult result, HttpServletRequest request) {
		
		System.out.println(result.hasErrors());
		if (result.hasErrors()) {
			System.out.println(result.hasErrors());
			List<ObjectError> allErrors = result.getAllErrors();
			for (ObjectError objectError : allErrors) {
				System.out.println(objectError.getDefaultMessage());
			}
			return "atividade/form";
		}
		
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByEmail(username);
		
		Atividade atividade = requisicao.toAtividade();
		
		atividade.setUser(user);
		atividade.setUserEmail(user.getEmail());
		atividade.setEstadoAtividade(EstadoAtividade.CONFIRMADO);
		
		
		if (atividade.getDataAtividade().isBefore(LocalDate.now()))
			atividade.setEstadoAtividade(EstadoAtividade.JÁ_OCORRIDO);

		atividadeRepository.save(atividade);
		
		return "redirect:/userAtividades";
	}
	
	@GetMapping("/userAtividades")
	public String minhasAtividades(Model model, Principal principal) {
		List<Atividade> atividades = atividadeRepository.findAllByUsuario(principal.getName());
		
		model.addAttribute("atividades", atividades);
		
		return "atividade/minhas_atividades";
	}
	
	@GetMapping("/userAtividadesConfirmadas")
	public String minhasAtividadesConfirmadas(Model model, Principal principal) {				
		List<Atividade> atividades = atividadeRepository.findAllByUsuarioConfirmado(principal.getName());
		
		model.addAttribute("atividades", atividades);
		
		return "atividade/minhas_atividades";
	}
	
	@GetMapping("/userAtividadesCanceladas")
	public String minhasAtividadesCanceladas(Model model, Principal principal) {
		List<Atividade> atividades = atividadeRepository.findAllByUsuarioCancelado(principal.getName());
		
		model.addAttribute("atividades", atividades);
		
		return "atividade/minhas_atividades";
	}
	
	@GetMapping("/userAtividadesOcorridas")
	public String minhasAtividadesJaOcorridas(Model model, Principal principal) {
		List<Atividade> atividades = atividadeRepository.findAllByUsuarioJaOcorrido(principal.getName());
		
		model.addAttribute("atividades", atividades);
		
		return "atividade/minhas_atividades";
	}
	
	@RequestMapping(path = "delete/{id}", method = RequestMethod.POST)
	public String deletar(@PathVariable Long id) {
		atividadeRepository.deleteById(id);
		return "redirect:/userAtividades";
	}
	
	@GetMapping(path = "/atividadesFormAlteracao/{id}")
	public ModelAndView atividadesFormAlteracao(@PathVariable Long id) {
		ModelAndView mav = new ModelAndView("atividade/form");
		Atividade atividade = atividadeRepository.findById(id).get();
		
		mav.addObject("requisicaoNovaAtividade", atividade);
		
		return mav;
	}
}
