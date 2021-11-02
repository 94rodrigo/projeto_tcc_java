package com.example.demo.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import com.example.demo.model.Role;
import com.example.demo.model.RolesEnum;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;

@Controller
@Component
public class CadastraUsuarioAdministrador {

	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Bean
	@Scope("singleton")
	public void cargaInicial() {
		cadastraRoles();
		cadastraAdmin();
	}

	private void cadastraAdmin() {
		System.out.println("Método cadastraAdmin()");
		userService.findByEmail("admin@sistema.com.br");
		if(!userRepository.existsByEmail("admin@sistema.com.br")) {
			User user = new User();
			user.setPrimeiroNome("Administrador");
			user.setUltimoNome("Sistema");
			user.setEmail("admin@sistema.com.br");
			user.setMunicipio("Santos");
			user.setUf("SP");
			user.setSenha("Q1w2e3r4t5y6");
			user.setConfirmacaoSenha("Q1w2e3r4t5y6");
			user.setEnabled(true);
			user.setCadastrado(LocalDateTime.now());
			user.getRoles().add(roleRepository.findById(1).get());
			System.out.println("\nUsuario administrador salvo\nUsuario: admin@sistema.com.br\nSenha: Q1w2e3r4t5y6");
			userService.saveUser(user);
		}else {
			System.out.println("Usuario administrador já cadastrado: admin@sistema.com.br");
		}
	}
	
	private void cadastraRoles() {
		System.out.println("Método cadastraRoles()");
		if(roleRepository.findAll().isEmpty()) {
			Role role1 = new Role(RolesEnum.ADMIN);
			Role role2 = new Role(RolesEnum.USER);
			System.out.println("\nRoles cadastradas:\n1 - ADMIN\n2 - USER");
			roleRepository.save(role1);
			roleRepository.save(role2);
		}else {
			System.out.println("Roles já cadastradas");
		}
	}
}
