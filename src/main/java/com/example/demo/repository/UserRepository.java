package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.User;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Long>{

	@Query("SELECT u FROM User u WHERE u.email = ?1")
	public User findByEmail(String email);
	
//	User findByNomeCompleto(String primeiroNome, String ultimoNomeString);
	
	@Query(value = "select count(*) from user_atividades where user_id = :usuario_id and atividade_id = :ativ_id", nativeQuery = true)
	Integer findByIdUserAtividades(@Param("usuario_id")Long usuario_id, @Param("ativ_id")Long ativ_id);
	
	@Transactional
	@Modifying
	@Query(value = "delete from user_atividades where user_id = ?1 and atividade_id = ?2", nativeQuery = true)
	void deleteByIdUserAtividades(Long id_user, Long id_atividade);
	
	public User findByResetPasswordToken(String token);
	
	@Transactional
	@Modifying
	@Query("update User u set u.permitiuLocalizacao = :permitiuLocalizacao, u.userCoordenadas = :userCoordenadas where u.email = :email")
	public void atualizaLocalizacao(@Param("permitiuLocalizacao")Boolean permitiuLocalizacao,
			@Param("userCoordenadas")String userCoordenadas, @Param("email")String email);
	
	@Transactional
	@Modifying
	@Query("update User u set u.permitiuLocalizacao = :permitiuLocalizacao, u.userLatitude = :userLatitude, u.userLongitude = :userLongitude where u.email = :email")
	public void atualizaLatitudeLongitude(@Param("permitiuLocalizacao")Boolean permitiuLocalizacao,
			@Param("userLatitude")String userLatitude, @Param("userLongitude")String userLongitude, @Param("email")String email);
	
	@Transactional
	@Modifying
	@Query("update User u set u.enabled = :enabled where u.email = :email")
	public void ativaOuInativaUsuario(@Param("enabled")Boolean enabled, @Param("email")String email);

	public boolean existsByEmail(String email);
	
//	List<User> findUserByAtividadeId();
}
