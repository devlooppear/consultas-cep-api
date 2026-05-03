package com.consultas_cep_api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Consultas CEP API", description = "Documentacao OpenAPI da API de consultas de CEP.", version = "v1", contact = @Contact(name = "consultas-cep-api")))
public class OpenApiConfig {

    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    YAMLMapper yamlMapper() {
        return new YAMLMapper();
    }
}