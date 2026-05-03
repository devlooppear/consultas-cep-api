package com.consultas_cep_api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.consultas_cep_api.dto.CepResponse;
import com.consultas_cep_api.service.CepService;
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
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class CepControllerTest {

    @Mock
    private CepService cepService;

    @InjectMocks
    private CepController controller;

    @Test
    void cenarioFeliz_buscarDeveRetornar200ComBody() {
        CepResponse expected = new CepResponse(
                "08030-310", "Rua Teste", "Itaquera", "Sao Paulo", "SP", LocalDateTime.now());
        when(cepService.buscarCep("08030-310")).thenReturn(expected);

        ResponseEntity<CepResponse> response = controller.buscar("08030-310");

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(expected);
        verify(cepService).buscarCep("08030-310");
    }

    @Test
    void cenarioFeliz_listarDeveRetornarPagina() {
        Pageable pageable = PageRequest.of(0, 10);
        CepResponse cep = new CepResponse(
                "01001-000", "Praca da Se", "Se", "Sao Paulo", "SP", LocalDateTime.now());
        Page<CepResponse> page = new PageImpl<>(java.util.List.of(cep), pageable, 1);
        when(cepService.listarConsultas(pageable)).thenReturn(page);

        ResponseEntity<Page<CepResponse>> response = controller.listar(pageable);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).containsExactly(cep);
        verify(cepService).listarConsultas(pageable);
    }
}
