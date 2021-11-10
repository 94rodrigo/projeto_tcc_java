package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Municipio;

@Repository
public interface MunicipioRepository extends JpaRepository<Municipio, Integer>{

	List<Municipio> findByMunUf(String munUf);
}
