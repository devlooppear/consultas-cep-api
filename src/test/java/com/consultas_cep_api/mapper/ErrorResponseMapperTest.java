package com.consultas_cep_api.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.consultas_cep_api.dto.ErrorResponse;
import com.consultas_cep_api.exceptions.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class ErrorResponseMapperTest {

    private final ErrorResponseMapper mapper = new ErrorResponseMapper();

    @Test
    void cenarioFeliz_toResponseComErrorCodeDeveMapearCorretamente() {
        ResponseEntity<ErrorResponse> response = mapper.toResponse(ErrorCode.CEP_NAO_ENCONTRADO, "/api/v1/cep/99999999");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().mensagem()).isEqualTo("CEP nao encontrado");
        assertThat(response.getBody().endpoint()).isEqualTo("/api/v1/cep/99999999");
    }

    @Test
    void regraDeNegocio_toResponseComHttpStatusCustomDeveRespeitarArgumentos() {
        ResponseEntity<ErrorResponse> response = mapper.toResponse(
                HttpStatus.BAD_GATEWAY,
                "Erro ao consultar servico externo de CEP",
                "/api/v1/cep/08030310");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(502);
        assertThat(response.getBody().mensagem()).isEqualTo("Erro ao consultar servico externo de CEP");
        assertThat(response.getBody().endpoint()).isEqualTo("/api/v1/cep/08030310");
    }
}
