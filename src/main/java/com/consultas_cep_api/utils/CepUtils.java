package com.consultas_cep_api.utils;

public final class CepUtils {

    public static final String CEP_REGEX = "\\d{5}-?\\d{3}";

    private CepUtils() {
    }

    public static String normalize(String cep) {
        return cep == null ? "" : cep.replaceAll("[^0-9]", "");
    }
}