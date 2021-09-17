package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.demo.api.CoordenadasApi;

@SpringBootApplication
public class DemoApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
		
	}
	
	public static void desligar() {
		System.out.println("Finalizando ......");
		CoordenadasApi.getContexto().shutdown();		
	}

}
