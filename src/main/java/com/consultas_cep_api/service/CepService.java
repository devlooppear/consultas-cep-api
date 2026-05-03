package com.consultas_cep_api.service;

import com.consultas_cep_api.client.CepApiClient;
import com.consultas_cep_api.dto.CepResponse;
import com.consultas_cep_api.dto.ViaCepResponse;
import com.consultas_cep_api.exceptions.CepNotFoundException;
import com.consultas_cep_api.infrastructure.CepRedisCacheService;
import com.consultas_cep_api.mapper.CepMapper;
import com.consultas_cep_api.repository.ConsultaCepRepository;
import com.consultas_cep_api.utils.CepUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CepService {

    private final CepApiClient cepApiClient;
    private final CepRedisCacheService cacheService;
    private final ConsultaCepRepository repository;
    private final CepMapper mapper;

    public CepResponse buscarCep(String cep) {
        String cepNormalizado = CepUtils.normalize(cep);
        log.info("Consultando CEP: {}", cepNormalizado);

        ViaCepResponse response = cacheService.get(cepNormalizado);
        if (response == null) {
            response = cepApiClient.buscar(cepNormalizado);
            cacheService.put(cepNormalizado, response);
        } else {
            log.info("CEP {} retornado do cache Redis.", cepNormalizado);
        }

        if (Boolean.TRUE.equals(response.erro())) {
            throw new CepNotFoundException(cepNormalizado);
        }

        return mapper.toResponse(repository.save(mapper.toEntity(response)));
    }

    public Page<CepResponse> listarConsultas(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }
}
