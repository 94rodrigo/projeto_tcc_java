package com.example.demo.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class LogDeAcoes {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User user;
	
	@OneToOne
	@JoinColumn(name = "atividade_id", referencedColumnName = "id")
	private Atividade atividade;
	
	private String acao;
	
	private LocalDateTime horarioAcao;

	public LogDeAcoes(User user, Atividade atividade, String acao) {
		this.user = user;
		this.atividade = atividade;
		this.horarioAcao = LocalDateTime.now();
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

	public Atividade getAtividade() {
		return atividade;
	}

	public String getAcao() {
		return acao;
	}
	
	public String getNomeUsuario() {
		return user.getNomeCompleto();
	}

	
}
