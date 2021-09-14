package com.example.demo.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.example.demo.dto.RequisicaoNovaAtividade;

@Entity
@Table(name = "atividades")
public class Atividade {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "nome_atividade")
	private String nomeAtividade;
	
	@Column(name = "tipo_atividade")
	private String tipoAtividade;
	
	private String uf;
	
	private String cidade;
	
	private String enderecoLocal;
	
	private LocalDate dataAtividade;

	private String descricao;
	
	@Column(name = "estado_atividade")
	@Enumerated(EnumType.STRING)
	private EstadoAtividade estadoAtividade;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;
	
	@Column(name = "user_email")
	private String userEmail;

	
	public Long getId() {
		return id;
	}

	public String getNomeAtividade() {
		return nomeAtividade;
	}

	public void setNomeAtividade(String nomeAtividade) {
		this.nomeAtividade = nomeAtividade;
	}

	public String getTipoAtividade() {
		return tipoAtividade;
	}

	public void setTipoAtividade(String tipoAtividade) {
		this.tipoAtividade = tipoAtividade;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public LocalDate getDataAtividade() {
		return dataAtividade;
	}

	public void setDataAtividade(LocalDate dataAtividade) {
		this.dataAtividade = dataAtividade;
	}
	
	public void setDataAtividade(String dataAtividade) {
		this.dataAtividade = LocalDate.parse(dataAtividade, FORMATTER);
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public EstadoAtividade getEstadoAtividade() {
		return estadoAtividade;
	}

	public void setEstadoAtividade(EstadoAtividade estadoAtividade) {
		this.estadoAtividade = estadoAtividade;
	}

	public String getEnderecoLocal() {
		return enderecoLocal;
	}

	public void setEnderecoLocal(String enderecoLocal) {
		this.enderecoLocal = enderecoLocal;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
		
	public void setId(Long id) {
		this.id = id;
	}

	public String getNomeCompletoUsuario() {
		return user.getNomeCompleto();
	}
	
	public RequisicaoNovaAtividade toRequisicao() {
		RequisicaoNovaAtividade requisicao = new RequisicaoNovaAtividade();
		requisicao.setNomeAtividade(nomeAtividade);
		requisicao.setCidade(cidade);
		requisicao.setDataAtividade(dataAtividade.format(FORMATTER));
		requisicao.setDescricao(descricao);
		requisicao.setEnderecoLocal(enderecoLocal);
		requisicao.setEstadoAtividade(estadoAtividade);
		requisicao.setTipoAtividade(tipoAtividade);
		requisicao.setUf(uf);
		return requisicao;
	}
	
}