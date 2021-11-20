package com.example.demo.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class LogDeAcoes {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User user;
	
	private String acao;
	
	private LocalDateTime horarioAcao;

	public LogDeAcoes(User user, String acao) {
		this.user = user;
		this.horarioAcao = LocalDateTime.now().minusHours(3);
		this.acao = acao;
	}
	
	public LogDeAcoes() {}

	public LocalDateTime getHorarioAcao() {
		return horarioAcao;
	}

	public void setHorarioAcao(LocalDateTime horarioAcao) {
		this.horarioAcao = horarioAcao;
	}

	public Long getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public String getAcao() {
		return acao;
	}
	
	public String getNomeUsuario() {
		return user.getNomeCompleto();
	}

	
}
