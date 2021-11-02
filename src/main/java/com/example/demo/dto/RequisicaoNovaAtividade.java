package com.example.demo.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.example.demo.model.Atividade;
import com.example.demo.model.EstadoAtividade;

public class RequisicaoNovaAtividade {
	
	@Id
	private Long id;
	
	@NotBlank
	private String nomeAtividade;
	
	@Column(name = "tipo_atividade")
	@DecimalMin(value = "1")
	private String tipoAtividade;
	
	@NotBlank
	@Pattern(regexp = "^[A-Z]{2}$")
	private String uf;
	
	@NotBlank
	private String cidade;
	
//	@Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4}$")
	@NotNull
	@DateTimeFormat(pattern = "dd/MM/yyyy", iso = ISO.DATE)
	private LocalDate dataAtividade;
	
//	@Pattern(regexp = "^\\d{2}:\\d{2}$")
	@DateTimeFormat(pattern = "HH:mm", iso = ISO.TIME)
	@NotNull
	private LocalTime horarioAtividade;
	
	private String descricao;
	
	private EstadoAtividade estadoAtividade;
	
	@NotBlank
	private String enderecoLocal;
	
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
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
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
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public Atividade toAtividade() {
		Atividade atividade = new Atividade();
//		String[] hora = this.horarioAtividade.split(":");
//		System.out.println("Horário: ");
//		for (String string : Arrays.asList(hora)) {
//			System.out.println(string + ":");
//		}
		
		atividade.setNomeAtividade(this.nomeAtividade);
		atividade.setTipoAtividade(this.tipoAtividade);
		atividade.setCidade(this.cidade);
//		atividade.setDataAtividade(LocalDate.parse(this.dataAtividade, FORMATTER));
//		atividade.setDataHorarioAtividade(LocalDateTime.of(LocalDate.parse(this.dataAtividade, FORMATTER), LocalTime.of(Integer.parseInt(hora[0]), Integer.parseInt(hora[1]))));
		atividade.setDataHorarioAtividade(LocalDateTime.of(dataAtividade, horarioAtividade));
		atividade.setUf(this.uf);
		atividade.setDescricao(this.descricao);
		atividade.setEnderecoLocal(this.enderecoLocal);
		
		return atividade;
	}
	
	public Atividade toAtividade(Atividade atividade) {
//		String[] hora = this.horarioAtividade.split(":");
//		System.out.println("Horário: ");
//		for (String string : Arrays.asList(hora)) {
//			System.out.println(string + ":");
//		}
		
		atividade.setId(this.id);
		atividade.setNomeAtividade(this.nomeAtividade);
		atividade.setTipoAtividade(this.tipoAtividade);
		atividade.setUf(this.uf);
		atividade.setCidade(this.cidade);
		atividade.setEnderecoLocal(this.enderecoLocal);
//		atividade.setDataAtividade(this.dataAtividade);
//		atividade.setDataHorarioAtividade(LocalDateTime.of(LocalDate.parse(this.dataAtividade, FORMATTER), LocalTime.of(Integer.parseInt(hora[0]), Integer.parseInt(hora[1]))));
		atividade.setDataHorarioAtividade(LocalDateTime.of(dataAtividade, horarioAtividade));
		atividade.setDescricao(this.descricao);
		return atividade;
	}
	
	public LocalTime getHorarioAtividade() {
		return horarioAtividade;
	}
	public void setHorarioAtividade(LocalTime horarioAtividade) {
		this.horarioAtividade = horarioAtividade;
	}
}
