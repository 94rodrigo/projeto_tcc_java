package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Municipio;

@Repository("municipiosRepository")
public interface MunicipiosRepository extends JpaRepository<Municipio, Integer>{

	@Query("select m from Municipio m where m.munUf = :munUf")
	List<Municipio> findAllByMunUf(@Param("munUf")String munUf);
	
}
