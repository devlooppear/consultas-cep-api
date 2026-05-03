package com.consultas_cep_api.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CepUtilsTest {

    @Test
    void cenarioFeliz_normalizeDeveRemoverCaracteresNaoNumericos() {
        assertThat(CepUtils.normalize("01001-000")).isEqualTo("01001000");
    }

    @Test
    void cenarioTriste_normalizeComNullDeveRetornarVazio() {
        assertThat(CepUtils.normalize(null)).isEmpty();
    }

    @Test
    void regraDeNegocio_regexDeveAceitarComESemHifen() {
        assertThat("01001000").matches(CepUtils.CEP_REGEX);
        assertThat("01001-000").matches(CepUtils.CEP_REGEX);
    }
}
