package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Atividade;
import com.example.demo.model.EstadoAtividade;

@Repository("atividadeRepository")
public interface AtividadeRepository extends JpaRepository<Atividade, Long>{

	@Query("select a from Atividade a join a.user u where u.email = :email")
	List<Atividade> findAllByUsuario(@Param("email")String email);
	
	@Query("select a from Atividade a where a.estadoAtividade = :estado")
	List<Atividade> findAllByEstado(@Param("estado") EstadoAtividade estado);
	
	@Query("select a from Atividade a join a.user u where u.email = :email and a.estadoAtividade = :estado")
	List<Atividade> findAllByUsuarioEEstado(@Param("email")String email, @Param("estado") EstadoAtividade estado);
	
	@Query("select a from Atividade a join a.user u where u.email = :email and a.dataAtividade >= date(now())")
	List<Atividade> findAllByUsuarioEDataAtual(@Param("email")String email);
	
	@Query("select a from Atividade a join a.user u where u.email = :email and a.dataAtividade < date(now())")
	List<Atividade> findAllByUsuarioEDataAnterior(@Param("email")String email);
	
	@Query("select a from Atividade a where a.dataAtividade >= date(now()) and a.estadoAtividade = :estado")
	List<Atividade> findAllByDataAtual(@Param("estado") EstadoAtividade estado);
	
	@Transactional
	@Modifying
	@Query("update Atividade a set a.estadoAtividade = :estado where a.id = :id")
	void alterarEstadoAtividade(@Param("estado") EstadoAtividade estado, @Param("id") Long id);
	
	
}
