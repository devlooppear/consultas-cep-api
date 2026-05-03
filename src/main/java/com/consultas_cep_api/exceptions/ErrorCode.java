package com.consultas_cep_api.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    CEP_NAO_ENCONTRADO(HttpStatus.NOT_FOUND, "CEP nao encontrado"),
    CEP_INVALIDO(HttpStatus.BAD_REQUEST, "CEP invalido. Use o formato XXXXXXXX ou XXXXX-XXX"),
    ERRO_CONSULTA_EXTERNA(HttpStatus.BAD_GATEWAY, "Erro ao consultar servico externo de CEP"),
    ERRO_INTERNO(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor");

    private final HttpStatus status;
    private final String mensagem;

    ErrorCode(HttpStatus status, String mensagem) {
        this.status = status;
        this.mensagem = mensagem;
    }
}