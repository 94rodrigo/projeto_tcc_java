package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Atividade;

@Repository("atividadeRepository")
public interface AtividadeRepository extends JpaRepository<Atividade, Long>{

	@Query("select a from Atividade a join a.user u where u.email = :email")
	List<Atividade> findAllByUsuario(@Param("email")String email);
	
	@Query("select a from Atividade a join a.user u where u.email = :email and a.estadoAtividade = 'CONFIRMADO'")
	List<Atividade> findAllByUsuarioConfirmado(@Param("email")String email);
	
	@Query("select a from Atividade a join a.user u where u.email = :email and a.estadoAtividade = 'CANCELADO'")
	List<Atividade> findAllByUsuarioCancelado(@Param("email")String email);
	
	@Query("select a from Atividade a join a.user u where u.email = :email and a.estadoAtividade = 'JÁ_OCORRIDO'")
	List<Atividade> findAllByUsuarioJaOcorrido(@Param("email")String email);

}
