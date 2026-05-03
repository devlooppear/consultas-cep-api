package com.consultas_cep_api.exceptions;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CepNotFoundExceptionTest {

    @Test
    void cenarioTriste_deveMontarMensagemComCepInformado() {
        CepNotFoundException exception = new CepNotFoundException("08030310");

        assertThat(exception).hasMessage("CEP nao encontrado: 08030310");
    }
}
