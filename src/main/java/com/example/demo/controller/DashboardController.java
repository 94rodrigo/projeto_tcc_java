package com.example.demo.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.model.Atividade;
import com.example.demo.model.EstadoAtividade;
import com.example.demo.model.LogDeAcoes;
import com.example.demo.model.User;
import com.example.demo.repository.AtividadeRepository;
import com.example.demo.repository.LogRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;

@Controller
@RequestMapping
public class DashboardController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AtividadeRepository atividadeRepository;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private LogRepository logRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
	private Boolean alterouUsuario = false;
	private Boolean erroAoAlterarUsuario = false;
	private String mensagemErroCampoVazio = "";
	private Boolean cadastroAdmin = false;
	private Boolean administradorAtualizou = false;
	
	@GetMapping("/dashboard")
	public String home(Model model, Principal principal) {
		User user = userRepository.findByEmail(principal.getName());
		List<Atividade> findAllByMunicipioEstado = atividadeRepository.findAllByMunicipioEstado(user.getMunicipio(), EstadoAtividade.CONFIRMADO);
		findAllByMunicipioEstado.sort(Comparator.comparing(Atividade::getDataHorarioAtividade));
		
		List<Atividade> atividadesConfirmadasUsuario = atividadeRepository.findAllByUsuarioEDataAtual(user.getEmail(), EstadoAtividade.CONFIRMADO);
		if (!atividadesConfirmadasUsuario.isEmpty()) {
			atividadesConfirmadasUsuario.sort(Comparator.comparing(Atividade::getDataHorarioAtividade));
			List<User> usuariosCadastradosNaAtividade = atividadesConfirmadasUsuario.get(0).getUsuariosCadastradosNaAtividade();
			model.addAttribute("usuariosCadastradosNaAtividade", usuariosCadastradosNaAtividade);
		} else {
			model.addAttribute("usuariosCadastradosNaAtividade", "-");
		}
		
		
		List<Atividade> atividadesPendentesUsuario = atividadeRepository.findAllByUsuarioEEstado(user.getEmail(), EstadoAtividade.PENDENTE);
		
		
		if (!findAllByMunicipioEstado.isEmpty()) {
			Atividade proximaAtividadeCidade = findAllByMunicipioEstado.get(0);
			model.addAttribute("proximaAtividadeCidade", proximaAtividadeCidade);
		} else {
			model.addAttribute("proximaAtividadeCidade", "-");
		}
		
		List<Atividade> atividadesQueUsuariosParticipa = user.getAtividadesQueUsuariosParticipa();
		for (Iterator<Atividade> iterator = atividadesQueUsuariosParticipa.iterator(); iterator.hasNext();) {
			Atividade atividade = iterator.next();
			if (!atividade.getEstadoAtividade().equals(EstadoAtividade.CONFIRMADO))
				iterator.remove();
		}
//		for (Atividade atividade : atividadesQueUsuariosParticipa) {
//			if (!atividade.getEstadoAtividade().equals(EstadoAtividade.CONFIRMADO))
//				atividadesQueUsuariosParticipa.remove(atividade);
//		}
		
		if (!atividadesQueUsuariosParticipa.isEmpty()) {
			atividadesQueUsuariosParticipa.sort(Comparator.comparing(Atividade::getDataHorarioAtividade));
			Atividade proximaAtividadeUsuario = atividadesQueUsuariosParticipa.get(0);
			model.addAttribute("proximaAtividadeUsuario", proximaAtividadeUsuario);
		} else {
			model.addAttribute("proximaAtividadeUsuario", "-");
		}
		
		model.addAttribute("user", user);
		model.addAttribute("novoUser", new User());
		model.addAttribute("atividadesLocal", findAllByMunicipioEstado);
		model.addAttribute("atividadesConfirmadasUsuario", atividadesConfirmadasUsuario);
		model.addAttribute("atividadesPendentesUsuario", atividadesPendentesUsuario);
		model.addAttribute("alterou", alterouUsuario);
		model.addAttribute("cadastroAdmin", cadastroAdmin);
		
		model.addAttribute("erroAoAlterarUsuario", erroAoAlterarUsuario);
		if(erroAoAlterarUsuario) model.addAttribute("mensagemErroCampoVazio", mensagemErroCampoVazio);
		if(cadastroAdmin) 		 model.addAttribute("administradorAtualizou", administradorAtualizou);
		
		alterouUsuario = false;
		erroAoAlterarUsuario = false;
		cadastroAdmin = false;
		mensagemErroCampoVazio = "";
		
		return "dashboard";
	}
	
	@PostMapping("/atualizaUsuario")
	public String atualizaUsuario(@Valid User user, BindingResult result, HttpServletRequest request, Model model, Principal principal) {
		List<String> errosCamposAlteracao = new ArrayList<>();
		User usuarioExiste = userService.findByEmail(principal.getName());
		if (!user.getPrimeiroNome().isEmpty())	usuarioExiste.setPrimeiroNome(user.getPrimeiroNome());	else errosCamposAlteracao.add("Campo \"Nome\" n??o pode estar em branco");
		if (!user.getUltimoNome().isEmpty())	usuarioExiste.setUltimoNome(user.getUltimoNome());		else errosCamposAlteracao.add("Campo \"Sobrenome\" n??o pode estar em branco");
		if (!user.getMunicipio().isEmpty())		usuarioExiste.setMunicipio(user.getMunicipio());		else errosCamposAlteracao.add("Campo \"Cidade\" n??o pode estar em branco");
		if (!user.getUf().equals("0"))			usuarioExiste.setUf(user.getUf());						else errosCamposAlteracao.add("Campo \"Estado\" n??o pode estar em branco");
		
		if (errosCamposAlteracao.isEmpty()) {
			userRepository.atualizaUsuarioPorEmail(
					usuarioExiste.getPrimeiroNome(),
					usuarioExiste.getUltimoNome(),
					usuarioExiste.getUf(),
					usuarioExiste.getMunicipio(),
					usuarioExiste.getEmail());
			logRepository.save(new LogDeAcoes(user, "Atualizou informa????es de cadastro"));
			this.alterouUsuario = true;			
		}else {
			for (String string : errosCamposAlteracao) {
				this.mensagemErroCampoVazio = this.mensagemErroCampoVazio.concat("- ").concat(string).concat("\n");
			}
			System.out.println(this.mensagemErroCampoVazio);
			this.erroAoAlterarUsuario = true;
		}

		 return "redirect:/dashboard";
	}
	
	
	@PostMapping("/adminCadastro")
	public String adminNovoUsuario(@Valid User novoUser, BindingResult result, HttpServletRequest request, Model model) {

		this.cadastroAdmin = true;
		if (result.hasErrors()) {
			System.out.println(result.hasErrors());
			List<ObjectError> allErrors = result.getAllErrors();
			for (ObjectError objectError : allErrors) {
				System.out.println(objectError.getDefaultMessage());
			}
			administradorAtualizou = false;
			return "redirect:/dashboard";
		}

		if (novoUser.getRoles().isEmpty()) {
			novoUser.getRoles().add(roleRepository.findById(2).get());
		}

		model.addAttribute("message", "Valid form");
		System.out.println("Controller: " + novoUser.getSenha().equals(novoUser.getConfirmacaoSenha()));
		String encodedPassword = bCryptPasswordEncoder.encode(novoUser.getSenha());
		novoUser.setSenha(encodedPassword);
		novoUser.setConfirmacaoSenha(encodedPassword);
		administradorAtualizou = true;
		userService.saveUser(novoUser);
		
		return "redirect:/dashboard";
	}
}
