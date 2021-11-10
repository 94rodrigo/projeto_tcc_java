package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Municipio;
import com.example.demo.repository.MunicipioRepository;

@Service
public class MunicipioService {

	@Autowired
	private static MunicipioRepository municipioRepository;
	
	public MunicipioService() {
	}
	
	public static List<Municipio> encontraCidadesPorUf(String uf) {
		return municipioRepository.findByMunUf(uf);
	}
}
