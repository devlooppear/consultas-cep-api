package com.consultas_cep_api.mapper;

import com.consultas_cep_api.domain.entity.ConsultaCep;
import com.consultas_cep_api.dto.CepResponse;
import com.consultas_cep_api.dto.ViaCepResponse;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class CepMapper {

    public ConsultaCep toEntity(ViaCepResponse r) {
        return ConsultaCep.builder()
                .cep(r.cep())
                .logradouro(r.logradouro())
                .bairro(r.bairro())
                .localidade(r.localidade())
                .uf(r.uf())
                .consultadoEm(LocalDateTime.now())
                .build();
    }

    public CepResponse toResponse(ConsultaCep entity) {
        return new CepResponse(
                entity.getCep(),
                entity.getLogradouro(),
                entity.getBairro(),
                entity.getLocalidade(),
                entity.getUf(),
                entity.getConsultadoEm());
    }
}
