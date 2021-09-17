package com.example.demo.api;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.maps.GeoApiContext;

@Component
public class CoordenadasApi {

	
	private static GeoApiContext contexto = null;
	
	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public GeoApiContext getGeoApiContext(String API_KEY) {
		System.out.println("passei por aqui");
		if (contexto == null) {
			contexto = new GeoApiContext.Builder().apiKey(API_KEY).build();
			System.out.println("Passei por aqui também");
			System.out.println(contexto.toString() + " + " + contexto.getClass().getMethods());
		}
		return contexto;
	}
	
	public static GeoApiContext getContexto() {
		return contexto;
	}
}
