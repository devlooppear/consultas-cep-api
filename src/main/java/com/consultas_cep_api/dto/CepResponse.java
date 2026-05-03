package com.consultas_cep_api.dto;

import java.time.LocalDateTime;

public record CepResponse(
        String cep,
        String logradouro,
        String bairro,
        String localidade,
        String uf,
        LocalDateTime consultadoEm) {}
