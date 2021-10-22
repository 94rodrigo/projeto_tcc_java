package com.example.demo.controller;

import java.io.IOException;
import java.security.Principal;
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
import com.example.demo.model.User;
import com.example.demo.repository.AtividadeRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.google.maps.errors.ApiException;

@Controller
@RequestMapping
public class CadastroController {

	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AtividadeRepository atividadeRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	public CadastroController(BCryptPasswordEncoder bCryptPasswordEncoder, UserService userService) {
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.userService = userService;
	}

	@GetMapping("/cadastro")
	public String cadastro(Model model) {
		model.addAttribute("novoUser", new User());
		return "cadastro";
	}

	@PostMapping("/cadastro")
	public String novoUsuario(@Valid User user, BindingResult result, HttpServletRequest request, Model model) {
		User usuarioExiste = userService.findByEmail(user.getEmail());

		if (usuarioExiste != null) {
			usuarioExiste.setPrimeiroNome(user.getPrimeiroNome());
			usuarioExiste.setUltimoNome(user.getUltimoNome());
			usuarioExiste.setEmail(user.getEmail());
			usuarioExiste.setUf(user.getUf());
			usuarioExiste.setMunicipio(user.getMunicipio());
			usuarioExiste.setEnabled(true);
			userService.saveUser(usuarioExiste);
			return "dashboard";
		}

		if (result.hasErrors()) {
			System.out.println(result.hasErrors());
			List<ObjectError> allErrors = result.getAllErrors();
			for (ObjectError objectError : allErrors) {
				System.out.println(objectError.getDefaultMessage());
			}
			System.out.println(request.getHeader(null));
			return "cadastro";
		}

		if (user.getRoles().isEmpty()) {
			user.getRoles().add(roleRepository.findById(2).get());
		}

		model.addAttribute("message", "Valid form");
		System.out.println("Controller: " + user.getSenha().equals(user.getConfirmacaoSenha()));
		String encodedPassword = bCryptPasswordEncoder.encode(user.getSenha());
		user.setSenha(encodedPassword);
		user.setConfirmacaoSenha(encodedPassword);
		user.setEnabled(true);
		user.setPermitiuLocalizacao(false);
		try {
			user.setUserCoordenadas(user.getCoordenadas());
		} catch (ApiException | InterruptedException | IOException e) {
			e.printStackTrace();
		}
		userService.saveUser(user);

		if (user.getRoles().contains(roleRepository.findById(1).get())) {
			return "redirect:/dashboard";
		}

		return "login";
	}

	@PostMapping("/adminCadastro")
	public String adminNovoUsuario(@Valid User novoUser, BindingResult result, HttpServletRequest request,
			Model model) {

		if (result.hasErrors()) {
			System.out.println(result.hasErrors());
			List<ObjectError> allErrors = result.getAllErrors();
			for (ObjectError objectError : allErrors) {
				System.out.println(objectError.getDefaultMessage());
			}
			return "dashboard";
		}

		if (novoUser.getRoles().isEmpty()) {
			novoUser.getRoles().add(roleRepository.findById(2).get());
		}

		model.addAttribute("message", "Valid form");
		System.out.println("Controller: " + novoUser.getSenha().equals(novoUser.getConfirmacaoSenha()));
		String encodedPassword = bCryptPasswordEncoder.encode(novoUser.getSenha());
		novoUser.setSenha(encodedPassword);
		novoUser.setConfirmacaoSenha(encodedPassword);
		novoUser.setEnabled(true);
		userService.saveUser(novoUser);

		return "dashboard";
	}
	
	@PostMapping("/atualizaCoordenadas")
	public String atualizaCoordenadas(User user, Principal principal, BindingResult result, HttpServletRequest request, Model model) throws ApiException, InterruptedException, IOException {

		model.addAttribute("message", "Valid form");
		user.setEnabled(true);
		
		if (!user.getPermitiuLocalizacao()) {
			user.setUserCoordenadas(user.getCoordenadas());
			user.setUserLatitude(user.getLatitudeApi());
			user.setUserLongitude(user.getLongitudeApi());
			userRepository.atualizaLocalizacao(false, user.getCoordenadas(), principal.getName());
			userRepository.atualizaLatitudeLongitude(false, user.getLatitudeApi(), user.getLongitudeApi(), principal.getName());
			return "redirect:/todas";
		}
		userRepository.atualizaLocalizacao(true, user.getUserCoordenadas(), principal.getName());
		userRepository.atualizaLatitudeLongitude(true, user.getUserLatitude(), user.getUserLongitude(), principal.getName());

		return "redirect:/todas";
	}
}
