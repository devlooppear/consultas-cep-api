package com.consultas_cep_api.dto;

public record ErrorResponse(int status, String mensagem, String endpoint) {}
