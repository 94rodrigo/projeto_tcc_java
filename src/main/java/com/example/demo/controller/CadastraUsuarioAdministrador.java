package com.example.demo.controller;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import com.example.demo.model.Role;
import com.example.demo.model.RolesEnum;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.google.maps.errors.ApiException;

@Controller
@Component
public class CadastraUsuarioAdministrador {

	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
	
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
			user.setEnabled(true);
			user.setPermitiuLocalizacao(false);
			user.setCadastrado(LocalDateTime.now());
			user.setSenha(bCryptPasswordEncoder.encode("Q1w2e3r4t5y6"));
			user.setConfirmacaoSenha(bCryptPasswordEncoder.encode("Q1w2e3r4t5y6"));
			try {
				user.setUserCoordenadas(user.getCoordenadas());
				user.setUserLatitude(user.getLatitudeApi());
				user.setUserLongitude(user.getLongitudeApi());
			} catch (ApiException | InterruptedException | IOException e) {
				e.printStackTrace();
			}
			user.getRoles().add(roleRepository.findByNome(RolesEnum.ADMIN));
			userService.saveUser(user);
			System.out.println("\n\n****************************************************************************\n\n");
			System.out.println("Usuario administrador salvo\nUsuario: admin@sistema.com.br\nSenha: Q1w2e3r4t5y6");
			System.out.println("\n\n****************************************************************************\n\n");
		}else {
			System.out.println("Usuario administrador já cadastrado: admin@sistema.com.br\n\n");
		}
	}
	
	private void cadastraRoles() {
		System.out.println("Método cadastraRoles()");
		if(roleRepository.findAll().isEmpty()) {
			Role role1 = new Role(RolesEnum.ADMIN);
			Role role2 = new Role(RolesEnum.USER);
			System.out.println("\n\n****************************************************************************\n\n");
			System.out.println("Roles cadastradas:\n1 - ADMIN\n2 - USER");
			System.out.println("\n\n****************************************************************************\n\n");
			roleRepository.save(role1);
			roleRepository.save(role2);
		}else {
			System.out.println("Roles já cadastradas\n\n");
		}
	}
}
