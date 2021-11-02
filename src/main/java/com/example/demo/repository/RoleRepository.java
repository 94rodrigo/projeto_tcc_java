package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.model.Role;
import com.example.demo.model.RolesEnum;

public interface RoleRepository extends JpaRepository<Role, Integer>{

	@Query("select r from Role r where r.nome = :nome")
	Optional<Role> findByNome(String nome);
	
	@Query("select r from Role r where r.nome = :nome")
	Role findByNome(RolesEnum nome);

}
