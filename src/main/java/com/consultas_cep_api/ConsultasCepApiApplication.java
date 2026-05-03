package com.consultas_cep_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ConsultasCepApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConsultasCepApiApplication.class, args);
	}

}
