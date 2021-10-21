package com.example.demo.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.example.demo.api.CoordenadasApi;
import com.example.demo.service.RoleService;
import com.example.demo.validadores.FieldMatch;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;

@FieldMatch.List({
	@FieldMatch(
		field = "senha",
		fieldMatch = "confirmacaoSenha",
		message = "Senhas digitadas devem ser idênticas"
	)
})
@Entity
@Table(name = "users")
@DynamicUpdate(true)
@DynamicInsert(true)
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
	@Column(name = "primeiro_nome")
	private String primeiroNome;
	
	@NotBlank
	@Column(name = "ultimo_nome")
	private String ultimoNome;
	
	@NotBlank
	@Pattern(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@+[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
	@Column(unique = true)
	private String email;
	
	@Pattern(regexp = "^[A-Z]{2}$")
	private String uf;
	
	@NotBlank
	private String municipio;
	
	@NotBlank
	private String senha;
	
	@NotBlank
	@Column(name = "confirmacao_senha")
	private String confirmacaoSenha;
	
	private Boolean enabled;
	
	private String resetPasswordToken;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.EAGER)
	@JsonIgnore
	private List<Atividade> atividades;
	
	@ManyToMany
	@JoinTable(
			name = "user_atividades",
			joinColumns = {@JoinColumn(name = "user_id")},
			inverseJoinColumns = {@JoinColumn(name = "atividade_id")})
	private List<Atividade> atividadesQueUsuariosParticipa = new ArrayList<>();
	
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(
			name = "users_roles",
			joinColumns = @JoinColumn(name = "id"),
			inverseJoinColumns = @JoinColumn(name = "role_id")
			)
	private Set<Role> roles = new HashSet<>();
	
	private String userCoordenadas;
	private String cidadeProcurada;
	private Boolean permitiuLocalizacao;
	
	public User() {
	}
	
	public Long getId() {
		return id;
	}
	public String getPrimeiroNome() {
		return primeiroNome;
	}
	public void setPrimeiroNome(String primeiroNome) {
		this.primeiroNome = primeiroNome;
	}
	public String getUltimoNome() {
		return ultimoNome;
	}
	public void setUltimoNome(String ultimoNome) {
		this.ultimoNome = ultimoNome;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
	public String getConfirmacaoSenha() {
		return confirmacaoSenha;
	}
	public void setConfirmacaoSenha(String confirmacaoSenha) {
		this.confirmacaoSenha = confirmacaoSenha;
	}
	public Boolean getEnabled() {
		return enabled;
	}
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	public String getNomeCompleto() {
		return primeiroNome + " " + ultimoNome;
	}
	
	public void adicionarAtividade(Atividade atividade) {
		atividadesQueUsuariosParticipa.add(atividade);
	}
	
	public Integer getAtividadeUsuarios() {
		return atividadesQueUsuariosParticipa.size(); 
	}
	
	public User getUser() {
		return this;
	}
	public List<Atividade> getAtividadesQueUsuariosParticipa() {
		return atividadesQueUsuariosParticipa;
	}
	public List<Atividade> getAtividadesOrdenadasPorData() {
		atividadesQueUsuariosParticipa.sort(Comparator.comparing(Atividade::getDataAtividade));
		return atividadesQueUsuariosParticipa;
	}
	public void setAtividadesQueUsuariosParticipa(List<Atividade> atividadesQueUsuariosParticipa) {
		this.atividadesQueUsuariosParticipa = atividadesQueUsuariosParticipa;
	}
	
	public Boolean isUsuarioConfirmado(Atividade atividade) {
		return this.atividadesQueUsuariosParticipa.contains(atividade);
	}
	public String getUf() {
		return uf;
	}
	public void setUf(String uf) {
		this.uf = uf;
	}
	public String getMunicipio() {
		return municipio;
	}
	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}
	public List<Atividade> getAtividades() {
		return atividades;
	}
	public void setAtividades(List<Atividade> atividades) {
		this.atividades = atividades;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getCidadeEstado() {
		return getMunicipio() + ", " + getUf();
	}
	
	public String getCoordenadas() throws ApiException, InterruptedException, IOException {
		GeocodingResult[] results = GeocodingApi.geocode(CoordenadasApi.getContexto(), getCidadeEstado()).await();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		String json = gson.toJson(results[0].geometry.location);
		return json;
	}
	
	public Long getLatitudeApi() throws ApiException, InterruptedException, IOException {
		GeocodingResult[] results = GeocodingApi.geocode(CoordenadasApi.getContexto(), getCidadeEstado()).await();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		String json = gson.toJson(results[0].geometry.location.lat);
		System.out.println("Latitude: " + json);
		return Long.valueOf(json);
	}
	
	public Long getLongitudeApi() throws ApiException, InterruptedException, IOException {
		GeocodingResult[] results = GeocodingApi.geocode(CoordenadasApi.getContexto(), getCidadeEstado()).await();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		String json = gson.toJson(results[0].geometry.location.lng);
		System.out.println("Longitude: " + json);
		return Long.valueOf(json);
	}

	public String getResetPasswordToken() {
		return resetPasswordToken;
	}
	
	public void setResetPasswordToken(String resetPasswordToken) {
		this.resetPasswordToken = resetPasswordToken;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	
	public void setRoles(String id) {
		this.roles.add(RoleService.findRoleById(id));
	}

	public String getUserCoordenadas() {
		return userCoordenadas;
	}

	public void setUserCoordenadas(String userCoordenadas) {
		this.userCoordenadas = userCoordenadas;
	}
	
	public String getCidadeProcurada(){
		return cidadeProcurada;
	}
	
	public void setCidadeProcurada() throws ApiException, InterruptedException, IOException {
		if(this.getCidadeProcurada().isEmpty() || this.getCidadeProcurada() == null) {
			cidadeProcurada = this.getMunicipio();
		}else {
			cidadeProcurada = obterCidadesPorCoordenadas();
		}
	}
	
	public String obterCidadesPorCoordenadas() throws ApiException, InterruptedException, IOException {
		GeocodingResult[] results = GeocodingApi.geocode(CoordenadasApi.getContexto(), getCidadeEstado()).await();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		String json = gson.toJson(results[0].addressComponents[3].longName);
		System.out.println("Cidade obtida: " + json);
		return json;
	}

	public Boolean getPermitiuLocalizacao() {
		return permitiuLocalizacao;
	}

	public void setPermitiuLocalizacao(Boolean permitiuLocalizacao) {
		this.permitiuLocalizacao = permitiuLocalizacao;
	}
	
}
