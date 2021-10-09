package com.example.demo.model;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.example.demo.api.CoordenadasApi;
import com.example.demo.dto.RequisicaoNovaAtividade;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;

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
	
//	private LocalDate dataAtividade;
	
	private LocalDateTime dataHorarioAtividade;

	private String descricao;
	
	@Column(name = "estado_atividade")
	@Enumerated(EnumType.STRING)
	private EstadoAtividade estadoAtividade;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private User user;
	
	@Column(name = "user_email")
	private String userEmail;
	
	@ManyToMany(cascade = CascadeType.ALL, mappedBy = "atividadesQueUsuariosParticipa")
	private List<User> usuariosCadastradosNaAtividade = new ArrayList<>();

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

	public String getNomeTipoAtividade() {
		String[] tipos = {"Corrida", "Caminhada", "Ciclismo", "Esportes coletivos", "Natação", "Musculação", "Treino funcional", "Outros"};
		int posicao = (Integer.parseInt(tipoAtividade) - 1); 
		if (posicao < 0 || posicao >= tipos.length)
			return null;
		
		return tipos[posicao];
	}
	
	public String[] getArrayTipos() {
		String[] tipos = {"Corrida", "Caminhada", "Ciclismo", "Esportes coletivos", "Natação", "Musculação", "Treino funcional", "Outros"};
		return tipos;
	}
	
	public int getNumeroAtividade(String tipo) {
		return Arrays.asList(getArrayTipos()).indexOf(tipo);
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

	public LocalDateTime getDataAtividade() {
		return dataHorarioAtividade;
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
		requisicao.setId(id);
		requisicao.setNomeAtividade(nomeAtividade);
		requisicao.setCidade(cidade);
//		requisicao.setDataAtividade(dataAtividade.format(FORMATTER));
		requisicao.setDataAtividade(dataHorarioAtividade.format(FORMATTER));
		requisicao.setHorarioAtividade(dataHorarioAtividade.getHour() + ":" + dataHorarioAtividade.getMinute());
		requisicao.setDescricao(descricao);
		requisicao.setEnderecoLocal(enderecoLocal);
		requisicao.setEstadoAtividade(estadoAtividade);
		requisicao.setTipoAtividade(tipoAtividade);
		requisicao.setUf(uf);
		return requisicao;
	}
	
	public String getEnderecoMapas() {
		String enderecoNormal = enderecoLocal + ", " + cidade + ", " + uf;
		String enderecoConvertido = enderecoNormal.replace("%", "%25").replace(" ", "%20").replace("\"", "%22").replace("<", "%3C")
			.replace(">", "%3E").replace("#", "%23").replace("|", "%7C");
		return enderecoConvertido;
	}
	
	public String getEnderecoCompleto() {
		return enderecoLocal + ", " + cidade + ", " + uf + ", Brasil";
	}
	
	public String getCoordenadas() throws ApiException, InterruptedException, IOException {
		GeocodingResult[] results = GeocodingApi.geocode(CoordenadasApi.getContexto(), getEnderecoCompleto()).await();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		String json = gson.toJson(results[0].geometry.location);
		return json;
	}
	
	public List<User> getUsuariosCadastradosNaAtividade() {
		return usuariosCadastradosNaAtividade;
	}

	public void setUsuariosCadastradosNaAtividade(List<User> usuariosCadastradosNaAtividade) {
		this.usuariosCadastradosNaAtividade = usuariosCadastradosNaAtividade;
	}

	public void incluirUsuario(User user) {
		usuariosCadastradosNaAtividade.add(user);
	}
	
	public Integer getUsuariosAtividade() {
		return usuariosCadastradosNaAtividade.size(); 
	}
	
	public Boolean getUsuarioParticipando(User user) {
		return usuariosCadastradosNaAtividade.contains(user);
	}
	
	public Boolean getOcorridoOuCancelado() {
		return getOcorrido() ||	getCancelado();
	}

	public Boolean getCancelado() {
		return estadoAtividade.equals(EstadoAtividade.CANCELADO);
	}

	public Boolean getOcorrido() {
		return dataHorarioAtividade.isBefore(LocalDateTime.now())
				|| estadoAtividade.equals(EstadoAtividade.JÁ_OCORRIDO);
	}

	public LocalDateTime getDataHorarioAtividade() {
		return dataHorarioAtividade;
	}

	public void setDataHorarioAtividade(LocalDateTime dataHorarioAtividade) {
		this.dataHorarioAtividade = dataHorarioAtividade;
	}
	
	
	
}