package com.consultas_cep_api.exceptions;

import static org.assertj.core.api.Assertions.assertThat;

import com.consultas_cep_api.dto.ErrorResponse;
import com.consultas_cep_api.mapper.ErrorResponseMapper;
import feign.FeignException;
import feign.Request;
import feign.Response;
import jakarta.validation.ConstraintViolationException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler(new ErrorResponseMapper());

    @Test
    void cenarioTriste_handleCepNotFoundDeveRetornar404() {
        MockHttpServletRequest request = request("/api/v1/cep/99999999");

        ResponseEntity<ErrorResponse> response = handler.handleCepNotFound(new CepNotFoundException("99999999"),
                request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().mensagem()).isEqualTo("CEP nao encontrado");
    }

    @Test
    void cenarioTriste_handleValidationDeveRetornar400() {
        MockHttpServletRequest request = request("/api/v1/cep/123");

        ResponseEntity<ErrorResponse> response = handler
                .handleValidation(new ConstraintViolationException(Collections.emptySet()), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().mensagem()).contains("CEP invalido");
    }

    @Test
    void cenarioTriste_handleFeign404DeveRetornarMensagemDeCepNaoEncontrado() {
        MockHttpServletRequest request = request("/api/v1/cep/08030310");

        ResponseEntity<ErrorResponse> response = handler.handleFeign(feignException(404), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().mensagem()).isEqualTo("CEP nao encontrado");
    }

    @Test
    void cenarioTriste_handleFeign500DeveRetornarMensagemDeErroExterno() {
        MockHttpServletRequest request = request("/api/v1/cep/08030310");

        ResponseEntity<ErrorResponse> response = handler.handleFeign(feignException(500), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().mensagem()).isEqualTo("Erro ao consultar servico externo de CEP");
    }

    @Test
    void regraDeNegocio_handleFeignComStatusForaDaEnumDeveUsarBadGateway() {
        MockHttpServletRequest request = request("/api/v1/cep/08030310");

        ResponseEntity<ErrorResponse> response = handler.handleFeign(feignException(799), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().mensagem()).isEqualTo("CEP nao encontrado");
    }

    @Test
    void cenarioTriste_handleGenericDeveRetornar500() {
        MockHttpServletRequest request = request("/api/v1/cep/consultas");

        ResponseEntity<ErrorResponse> response = handler.handleGeneric(new RuntimeException("boom"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().mensagem()).isEqualTo("Erro interno no servidor");
    }

    private MockHttpServletRequest request(String uri) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(uri);
        return request;
    }

    private FeignException feignException(int status) {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "https://viacep.com.br/ws/08030310/json",
                Collections.emptyMap(),
                null,
                StandardCharsets.UTF_8,
                null);

        Response response = Response.builder()
                .status(status)
                .reason("error")
                .request(request)
                .headers(Collections.emptyMap())
                .body(new byte[0])
                .build();

        return FeignException.errorStatus("CepApiClient#buscar", response);
    }
}
