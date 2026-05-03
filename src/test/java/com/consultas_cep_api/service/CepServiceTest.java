package com.consultas_cep_api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.consultas_cep_api.client.CepApiClient;
import com.consultas_cep_api.domain.entity.ConsultaCep;
import com.consultas_cep_api.dto.CepResponse;
import com.consultas_cep_api.dto.ViaCepResponse;
import com.consultas_cep_api.exceptions.CepNotFoundException;
import com.consultas_cep_api.infrastructure.CepRedisCacheService;
import com.consultas_cep_api.mapper.CepMapper;
import com.consultas_cep_api.repository.ConsultaCepRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CepServiceTest {

    @Mock
    private CepApiClient cepApiClient;
    @Mock
    private CepRedisCacheService cacheService;
    @Mock
    private ConsultaCepRepository repository;
    @Mock
    private CepMapper mapper;

    @InjectMocks
    private CepService cepService;

    @Test
    void cenarioFeliz_deveBuscarSalvarERetornarQuandoNaoHaCache() {
        String cepEntrada = "01001-000";
        String cepNormalizado = "01001000";

        ViaCepResponse via = viaCepOk(cepEntrada);
        ConsultaCep entity = consultaCepBuilder(via).build();
        CepResponse expected = cepResponse(via, entity.getConsultadoEm());

        when(cacheService.get(cepNormalizado)).thenReturn(null);
        when(cepApiClient.buscar(cepNormalizado)).thenReturn(via);
        when(mapper.toEntity(via)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(expected);

        CepResponse result = cepService.buscarCep(cepEntrada);

        assertThat(result).isEqualTo(expected);
        verify(cacheService).get(cepNormalizado);
        verify(cepApiClient).buscar(cepNormalizado);
        verify(cacheService).put(cepNormalizado, via);
        verify(repository).save(entity);
    }

    @Test
    void cenarioTriste_deveLancarExcecaoQuandoViaCepRetornaErro() {
        String cep = "99999999";
        ViaCepResponse viaErro = viaCepErro(cep);

        when(cacheService.get(cep)).thenReturn(null);
        when(cepApiClient.buscar(cep)).thenReturn(viaErro);

        assertThatThrownBy(() -> cepService.buscarCep(cep))
                .isInstanceOf(CepNotFoundException.class)
                .hasMessageContaining(cep);

        verify(cacheService).put(cep, viaErro);
        verify(repository, never()).save(any());
    }

    @Test
    void regraDeNegocio_deveNormalizarCepAntesDeConsultarApi() {
        String cepEntrada = "01001-000";
        String cepNormalizado = "01001000";

        ViaCepResponse via = viaCepOk(cepEntrada);
        ConsultaCep entity = consultaCepBuilder(via).build();
        CepResponse expected = cepResponse(via, entity.getConsultadoEm());

        when(cacheService.get(cepNormalizado)).thenReturn(null);
        when(cepApiClient.buscar(cepNormalizado)).thenReturn(via);
        when(mapper.toEntity(via)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(expected);

        cepService.buscarCep(cepEntrada);

        verify(cacheService).get(eq(cepNormalizado));
        verify(cepApiClient).buscar(eq(cepNormalizado));
        verify(cacheService).put(eq(cepNormalizado), eq(via));
    }

    @Test
    void cenarioFeliz_listarConsultasDeveRetornarPaginaMapeada() {
        Pageable pageable = PageRequest.of(0, 2);
        ViaCepResponse via = viaCepPracaDaSe();
        ConsultaCep entity = consultaCepBuilder(via).build();
        CepResponse response = cepResponse(via, entity.getConsultadoEm());
        Page<ConsultaCep> page = new PageImpl<>(java.util.List.of(entity), pageable, 1);

        when(repository.findAll(pageable)).thenReturn(page);
        when(mapper.toResponse(entity)).thenReturn(response);

        Page<CepResponse> result = cepService.listarConsultas(pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).containsExactly(response);
    }

    private ViaCepResponse viaCepOk(String cep) {
        return new ViaCepResponse(
                cep, "Rua Teste", "", "Itaquera", "Sao Paulo", "SP", "Sao Paulo", "11", false);
    }

    private ViaCepResponse viaCepErro(String cep) {
        return new ViaCepResponse(cep, null, null, null, null, null, null, null, true);
    }

    private ViaCepResponse viaCepPracaDaSe() {
        return new ViaCepResponse(
                "01001-000", "Praca da Se", "lado impar", "Se", "Sao Paulo", "SP", "Sao Paulo", "11", false);
    }

    private ConsultaCep.ConsultaCepBuilder consultaCepBuilder(ViaCepResponse via) {
        return ConsultaCep.builder()
                .cep(via.cep())
                .logradouro(via.logradouro())
                .bairro(via.bairro())
                .localidade(via.localidade())
                .uf(via.uf())
                .consultadoEm(LocalDateTime.now());
    }

    private CepResponse cepResponse(ViaCepResponse via, LocalDateTime consultadoEm) {
        return new CepResponse(
                via.cep(), via.logradouro(), via.bairro(), via.localidade(), via.uf(), consultadoEm);
    }
}
