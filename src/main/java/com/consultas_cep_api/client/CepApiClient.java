package com.consultas_cep_api.client;

import com.consultas_cep_api.dto.ViaCepResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cep-api", url = "${viacep.url}")
public interface CepApiClient {

    @GetMapping("/{cep}/json")
    ViaCepResponse buscar(@PathVariable("cep") String cep);
}
