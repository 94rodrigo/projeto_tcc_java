package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Role;
import com.example.demo.repository.RoleRepository;

@Service
public class RoleService {
	
	@Autowired
	private static RoleRepository roleRepository;
	
	public static Role findRoleById(String id) {
		return roleRepository.findById(Integer.valueOf(id)).get();
	}
	
	public static Role findRoleByName(String nome) {
		return roleRepository.findByNome(nome).get();
	}
}
