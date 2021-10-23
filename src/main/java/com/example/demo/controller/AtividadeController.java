package com.example.demo.controller;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.api.CoordenadasApi;
import com.example.demo.dto.RequisicaoNovaAtividade;
import com.example.demo.model.Atividade;
import com.example.demo.model.EstadoAtividade;
import com.example.demo.model.LogDeAcoes;
import com.example.demo.model.User;
import com.example.demo.repository.AtividadeRepository;
import com.example.demo.repository.LogRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AtividadeService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;

@Controller
@RequestMapping
public class AtividadeController {

	@Autowired
	private AtividadeRepository atividadeRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AtividadeService atividadeService;
	
	@Autowired
	private LogRepository logRepository;
	
	private Long idAtividade;
	
	private Boolean isAtividadeCadastradaEPendente = false;
	
	@GetMapping("/todas")
	public String todasAsAtividades(Model model, Principal principal, String tipo) throws IOException {
//		List<Atividade> atividades = atividadeRepository.findAllByDataAtual(EstadoAtividade.CONFIRMADO);
		
		User usuarioLogado = userRepository.findByEmail(principal.getName());
		Double x1, x2, y1, y2;
		
		x1 = Double.valueOf(usuarioLogado.getUserLatitude());
		x2 = Double.valueOf(usuarioLogado.getUserLongitude());
		
		model.addAttribute("novoUser", new User());
		model.addAttribute("usuarioLogado", usuarioLogado);
		model.addAttribute("user", usuarioLogado);
		
		if (usuarioLogado.getPermitiuLocalizacao()) {
			List<Atividade> findAllByEstado = atividadeRepository.findAllByEstado(EstadoAtividade.CONFIRMADO);
			for (Atividade atividade : findAllByEstado) {
				y1 = Double.valueOf(atividade.getLatitude());
				y2 = Double.valueOf(atividade.getLongitude());
				if (calculaDistanciaPontos(x1, x2, y1, y2) >= 0.1) {
					findAllByEstado.remove(atividade);
				}
			}
			findAllByEstado.sort(Comparator.comparing(Atividade::getDataHorarioAtividade));
			model.addAttribute("atividades", findAllByEstado);
			return "atividade/atividades_todas";
		}
		
		String municipio = usuarioLogado.getMunicipio();
		String uf = usuarioLogado.getUf();
		List<Atividade> atividades = atividadeRepository.findAllByDataAtualMunicipioUf(EstadoAtividade.CONFIRMADO, municipio, uf);
		atividades.sort(Comparator.comparing(Atividade::getDataHorarioAtividade));
		
		for (Atividade atividade : atividades) {
			System.out.println("Horário: " + atividade.getDataHorarioAtividade().getHour() + ":" + atividade.getDataHorarioAtividade().getMinute());
		}
		
		model.addAttribute("atividades", atividades);
		
//		model.addAttribute("tipoBusca", tipo);
//		System.out.println(tipo);
		
		
		return "atividade/atividades_todas";
	}
	
	@GetMapping("/atividadesForm")
	public String atividadesForm(Model model, Principal principal) {
		User user = userRepository.findByEmail(principal.getName());
		model.addAttribute("user", user);
		model.addAttribute("novoUser", new User());
		model.addAttribute("requisicaoNovaAtividade", new RequisicaoNovaAtividade());
		return "atividade/form";
	}
	
	@PostMapping("/atividadesFormGravar")
	public String novaAtividade(@Valid RequisicaoNovaAtividade requisicaoNovaAtividade, BindingResult result, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByEmail(username);
		
		if (idAtividade != null) {
			Atividade atividade2 = atividadeRepository.findById(idAtividade).get();
			requisicaoNovaAtividade.setId(idAtividade);
			atividade2 = requisicaoNovaAtividade.toAtividade(atividade2);
			try {
				atividade2.setCoordenadas(atividade2.getCoordenadasAtividade());
				atividade2.setLatitude(atividade2.getLatitudeAtividade());
				atividade2.setLongitude(atividade2.getLongitudeAtividade());
			} catch (ApiException | InterruptedException | IOException e) {
				e.printStackTrace();
			}
			System.out.println(result.hasErrors());
			if (result.hasErrors()) {
				return verificarErrosAtividade(result);
			}
			atividadeRepository.save(atividade2);
			logRepository.save(new LogDeAcoes(
					user,
					atividade2,
					"Atualizou atividade ".concat(atividade2.getNomeAtividade())));
			idAtividade = null;
			return "redirect:/userAtividades";
		}
		
		System.out.println(result.hasErrors());
		if (result.hasErrors()) {
			return verificarErrosAtividade(result);
		}
		
		Atividade atividade = requisicaoNovaAtividade.toAtividade();
		
		atividade.setUser(user);
		atividade.setUserEmail(user.getEmail());
		atividade.setEstadoAtividade(EstadoAtividade.PENDENTE);
		user.adicionarAtividade(atividade);
		atividade.incluirUsuario(user);
		try {
			atividade.setCoordenadas(atividade.getCoordenadasAtividade());
			atividade.setLatitude(atividade.getLatitudeAtividade());
			atividade.setLongitude(atividade.getLongitudeAtividade());
		} catch (ApiException | InterruptedException | IOException e) {
			e.printStackTrace();
		}
		
		model.addAttribute("user", user);
		
		atividadeRepository.save(atividade);
		userRepository.save(user);
		logRepository.save(new LogDeAcoes(user, atividade, "Cadastrou atividade: ".concat(atividade.getNomeAtividade())));
		
		isAtividadeCadastradaEPendente = true;
		
		
		redirectAttributes.addFlashAttribute("message", "Atividade \"" + atividade.getNomeAtividade() + "\" cadastrada. Aguarde aprovação pelos moderadores.");
		
		return "redirect:/userAtividades";
	}

	private String verificarErrosAtividade(BindingResult result) {
		System.out.println(result.hasErrors());
		List<ObjectError> allErrors = result.getAllErrors();
		for (ObjectError objectError : allErrors) {
			System.out.println(objectError.getDefaultMessage());
		}
		return "atividade/form";
	}
	
	@GetMapping("/userAtividades")
	public String minhasAtividades(Model model, Principal principal) {
		List<Atividade> atividades = atividadeRepository.findAllByUsuario(principal.getName());
		User user = userRepository.findByEmail(principal.getName());
		model.addAttribute("atividadeCadatradaEPendente", isAtividadeCadastradaEPendente);
		model.addAttribute("user", user);
		model.addAttribute("atividades", atividades);
		model.addAttribute("novoUser", new User());
		for (Atividade atividade : atividades) {
			if (atividade.getDataAtividade().isBefore(LocalDateTime.now()) && !atividade.getEstadoAtividade().equals(EstadoAtividade.CANCELADO)) {
				atividade.setEstadoAtividade(EstadoAtividade.JÁ_OCORRIDO);
				atividadeRepository.save(atividade);
			} else if (atividade.getDataAtividade().isAfter(LocalDateTime.now()) && atividade.getEstadoAtividade().equals(EstadoAtividade.JÁ_OCORRIDO)) {
				atividade.setEstadoAtividade(EstadoAtividade.CONFIRMADO);
				atividadeRepository.save(atividade);
			}
		}
		isAtividadeCadastradaEPendente = false;
		
		return "atividade/minhas_atividades";
	}
	
	@GetMapping("/atividadesOcorridas")
	public String atividadesOcorridas(Model model, Principal principal) {
		User usuarioLogado = userRepository.findByEmail(principal.getName());
		String municipio = usuarioLogado.getMunicipio();
		String uf = usuarioLogado.getUf();
		List<Atividade> atividades = atividadeRepository.findAllByDataAtualMunicipioUfDataAnterior(EstadoAtividade.JÁ_OCORRIDO, municipio, uf);
		model.addAttribute("atividades", atividades);
		model.addAttribute("usuarioLogado", usuarioLogado);
		model.addAttribute("user", usuarioLogado);
		model.addAttribute("novoUser", new User());
		
		return "atividade/atividades_todas";
	}
	
	@GetMapping("/atividadesGeral")
	public String atividadesTodosOsEstados(Model model, Principal principal) {
		User usuarioLogado = userRepository.findByEmail(principal.getName());
		String municipio = usuarioLogado.getMunicipio();
		String uf = usuarioLogado.getUf();
		List<Atividade> atividades = atividadeRepository.findAllByMunicipioUfTodas(municipio, uf);
		model.addAttribute("atividades", atividades);
		model.addAttribute("usuarioLogado", usuarioLogado);
		model.addAttribute("user", usuarioLogado);
		model.addAttribute("novoUser", new User());
		
		return "atividade/atividades_todas";
	}
	
	@GetMapping("/atividadesCanceladas")
	public String atividadesCanceladas(Model model, Principal principal) {
		User usuarioLogado = userRepository.findByEmail(principal.getName());
		String municipio = usuarioLogado.getMunicipio();
		String uf = usuarioLogado.getUf();
		List<Atividade> atividades = atividadeRepository.findAllByDataAtualMunicipioUfCancelado(EstadoAtividade.CANCELADO, municipio, uf);
		model.addAttribute("atividades", atividades);
		model.addAttribute("usuarioLogado", usuarioLogado);
		model.addAttribute("user", usuarioLogado);
		model.addAttribute("novoUser", new User());
		
		return "atividade/atividades_todas";
	}
	
	@GetMapping("/userAtividadesConfirmadas")
	public String minhasAtividadesConfirmadas(Model model, Principal principal) {
		User user = userRepository.findByEmail(principal.getName());
		model.addAttribute("user", user);
		List<Atividade> atividades = atividadeRepository.findAllByUsuarioEDataAtual(principal.getName(), EstadoAtividade.CONFIRMADO);
		model.addAttribute("atividades", atividades);
		model.addAttribute("novoUser", new User());
		
		return "atividade/minhas_atividades";
	}
	
	@GetMapping("/userAtividadesCanceladas")
	public String minhasAtividadesCanceladas(Model model, Principal principal) {
		User user = userRepository.findByEmail(principal.getName());
		model.addAttribute("user", user);
		List<Atividade> atividades = atividadeRepository.findAllByUsuarioEEstado(principal.getName(), EstadoAtividade.CANCELADO);
		model.addAttribute("atividades", atividades);
		model.addAttribute("novoUser", new User());
		
		return "atividade/minhas_atividades";
	}
	
	@GetMapping("/userAtividadesPendentes")
	public String minhasAtividadesPendentes(Model model, Principal principal) {
		User user = userRepository.findByEmail(principal.getName());
		model.addAttribute("user", user);
		List<Atividade> atividades = atividadeRepository.findAllByUsuarioEEstado(principal.getName(), EstadoAtividade.PENDENTE);
		model.addAttribute("atividades", atividades);
		model.addAttribute("novoUser", new User());
		
		return "atividade/minhas_atividades";
	}
	
	@GetMapping("/userAtividadesRejeitadas")
	public String minhasAtividadesRejeitadas(Model model, Principal principal) {
		User user = userRepository.findByEmail(principal.getName());
		model.addAttribute("user", user);
		List<Atividade> atividades = atividadeRepository.findAllByUsuarioEEstado(principal.getName(), EstadoAtividade.REJEITADO);
		model.addAttribute("atividades", atividades);
		model.addAttribute("novoUser", new User());
		
		return "atividade/minhas_atividades";
	}
	
	@GetMapping("/userAtividadesOcorridas")
	public String minhasAtividadesJaOcorridas(Model model, Principal principal) {
		User user = userRepository.findByEmail(principal.getName());
		model.addAttribute("user", user);
		List<Atividade> atividades = atividadeRepository.findAllByUsuarioEDataAnterior(principal.getName());
		model.addAttribute("atividades", atividades);
		model.addAttribute("novoUser", new User());
		
		return "atividade/minhas_atividades";
	}
	
	@RequestMapping(path = "delete/{id}", method = RequestMethod.POST)
	public String deletar(@PathVariable Long id) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByEmail(username);
		Atividade atividade = atividadeRepository.findById(id).get();
		atividadeService.deletarAtividade(id);
		logRepository.save(new LogDeAcoes(user, atividade, "Apagou atividade ".concat(atividade.getNomeAtividade())));
		return "redirect:/userAtividades";
	}
	
	@GetMapping(path = "/atividadesFormAlteracao/{id}")
	public ModelAndView atividadesFormAlteracao(@PathVariable Long id, Principal principal) {
		ModelAndView mav = new ModelAndView("atividade/form");
		User user = userRepository.findByEmail(principal.getName());
		RequisicaoNovaAtividade requisicaoNovaAtividade = atividadeRepository.findById(id).get().toRequisicao();
		idAtividade = requisicaoNovaAtividade.getId();
		
		mav.addObject("user", user);
		mav.addObject("requisicaoNovaAtividade", requisicaoNovaAtividade);
		mav.addObject("novoUser", new User());
		
		return mav;
	}
	
	@RequestMapping(path = "/atividade/{id}/cancelar", method = RequestMethod.POST)
	public String cancelarAtividade(@PathVariable Long id) {
		atividadeRepository.alterarEstadoAtividade(EstadoAtividade.CANCELADO, id);
		Atividade atividade = atividadeRepository.findById(id).get();
		logRepository.save(new LogDeAcoes(null, atividade, "Alterou o status da atividade ".concat(atividade.getNomeAtividade()).concat(" para ".concat(EstadoAtividade.CANCELADO.name()))));
		return "redirect:/userAtividades";
	}
	
	public void obterCoordenadas(String endereco) throws ApiException, InterruptedException, IOException {
		GeocodingResult[] results = GeocodingApi.geocode(CoordenadasApi.getContexto(), endereco).await();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		System.out.println(gson.toJson(results[0].geometry.location));
	}
	
//	@RequestMapping("/resultados")
//	private String resultadosBusca(Model model, @Param("keyword") String keyword, Principal principal) {
//		List<Atividade> atividades = atividadeService.listarResultados(keyword);
//		model.addAttribute("atividades", atividades);
//		model.addAttribute("keyword", keyword);
//		model.addAttribute("usuarioLogado", userRepository.findByEmail(principal.getName()));
//		return "atividade/atividades_todas";
//	}
	
	@RequestMapping("/resultados")
	private String resultadosBuscaPesquisa(Model model, @Param("keyword") String keyword, @Param("tipoBusca") String tipoBusca, Principal principal) {
		List<Atividade> atividades;
		switch (tipoBusca) {
		case "0":
			atividades = atividadeService.listarResultadosPorNomeOuDescricao(keyword);
			break;
		case "1":
			atividades = atividadeService.listarResultadosPorTipoDeAtividade(keyword);
			break;
		case "2":
			atividades = atividadeService.listarResultadosPorLocal(keyword);
			break;
		default:
			return null;
		}
		model.addAttribute("atividades", atividades);
		model.addAttribute("keyword", keyword);
		model.addAttribute("tipoBusca", tipoBusca);
		model.addAttribute("usuarioLogado", userRepository.findByEmail(principal.getName()));
		model.addAttribute("user", userRepository.findByEmail(principal.getName()));
		model.addAttribute("novoUser", new User());
		return "atividade/atividades_todas";
	}
	
	@RequestMapping(path = "/atividade/{id}", method = RequestMethod.GET)
	public String infoAtividade(@PathVariable Long id, Model model, Principal principal) {
		Atividade atividade = atividadeRepository.findById(id).get();
		User usuarioLogado = userRepository.findByEmail(principal.getName());
		List<User> usuarios = atividade.getUsuariosCadastradosNaAtividade();
		
		model.addAttribute("atividade", atividade);
		model.addAttribute("usuarios", usuarios);
		model.addAttribute("usuarioLogado", usuarioLogado);
		model.addAttribute("user", usuarioLogado);
		model.addAttribute("novoUser", new User());
		return "atividade/atividade_informacoes";
	}
	
	private Double calculaDistanciaPontos(Double a1, Double a2, Double b1, Double b2) {
		Double a = Math.pow((a2 - b2), 2);
		Double b = Math.pow((a1 - b1), 2);
		return Math.sqrt(a + b);
	}
}
