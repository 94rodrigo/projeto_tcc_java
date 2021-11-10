package com.example.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "mpr_municipios")
public class Municipio {

	@Id
	private Integer mun_id;
	
	@Column(name = "mun_ibge")
	private Integer munIbge;
	
	@Column(name = "mun_nome")
	private String munNome;
	
	@Column(name = "mun_uf")
	private String munUf;
	
	public Municipio() {
	}

	public Integer getMun_id() {
		return mun_id;
	}

	public void setMun_id(Integer mun_id) {
		this.mun_id = mun_id;
	}

	public Integer getMunIbge() {
		return munIbge;
	}

	public void setMunIbge(Integer munIbge) {
		this.munIbge = munIbge;
	}

	public String getMunNome() {
		return munNome;
	}

	public void setMunNome(String munNome) {
		this.munNome = munNome;
	}

	public String getMunUf() {
		return munUf;
	}

	public void setMunUf(String munUf) {
		this.munUf = munUf;
	}
}