package com.consultas_cep_api.exceptions;

public class CepNotFoundException extends RuntimeException {

    public CepNotFoundException(String cep) {
        super("CEP nao encontrado: " + cep);
    }
}
