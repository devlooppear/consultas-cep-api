package com.consultas_cep_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI consultasCepOpenAPI(
            @Value("${spring.application.name}") String applicationName,
            @Value("${server.servlet.context-path:/}") String contextPath) {
        return new OpenAPI()
                .info(new Info()
                        .title("Consultas CEP API")
                        .description("Documentacao OpenAPI da API de consultas de CEP.")
                        .version("v1")
                        .contact(new Contact().name(applicationName)))
                .servers(List.of(new Server()
                        .url(contextPath)));
    }
}