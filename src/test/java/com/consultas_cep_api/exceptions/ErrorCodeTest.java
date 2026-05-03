package com.consultas_cep_api.exceptions;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class ErrorCodeTest {

    @Test
    void regraDeNegocio_deveManterStatusECatalogoDeMensagens() {
        assertThat(ErrorCode.CEP_NAO_ENCONTRADO.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(ErrorCode.CEP_INVALIDO.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(ErrorCode.ERRO_CONSULTA_EXTERNA.getStatus()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(ErrorCode.ERRO_INTERNO.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        assertThat(ErrorCode.CEP_NAO_ENCONTRADO.getMensagem()).isEqualTo("CEP nao encontrado");
        assertThat(ErrorCode.CEP_INVALIDO.getMensagem()).contains("CEP invalido");
    }
}
