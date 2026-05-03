package com.consultas_cep_api.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.consultas_cep_api.domain.entity.ConsultaCep;
import com.consultas_cep_api.dto.CepResponse;
import com.consultas_cep_api.dto.ViaCepResponse;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class CepMapperTest {

    private final CepMapper mapper = new CepMapper();

    @Test
    void cenarioFeliz_toEntityDeveMapearDadosBasicos() {
        ViaCepResponse via = new ViaCepResponse(
                "01001-000", "Praca da Se", "lado impar", "Se", "Sao Paulo", "SP", "Sao Paulo", "11", false);

        ConsultaCep entity = mapper.toEntity(via);

        assertThat(entity.getCep()).isEqualTo("01001-000");
        assertThat(entity.getLogradouro()).isEqualTo("Praca da Se");
        assertThat(entity.getBairro()).isEqualTo("Se");
        assertThat(entity.getLocalidade()).isEqualTo("Sao Paulo");
        assertThat(entity.getUf()).isEqualTo("SP");
        assertThat(entity.getConsultadoEm()).isNotNull();
    }

    @Test
    void regraDeNegocio_toResponseDeveReplicarCamposDaEntidade() {
        LocalDateTime agora = LocalDateTime.now();
        ConsultaCep entity = consultaCepBuilder()
            .cep("30130-110")
            .logradouro("Rua dos Caetes")
            .bairro("Centro")
            .localidade("Belo Horizonte")
            .uf("MG")
            .consultadoEm(agora)
            .build();

        CepResponse response = mapper.toResponse(entity);

        assertThat(response.cep()).isEqualTo("30130-110");
        assertThat(response.logradouro()).isEqualTo("Rua dos Caetes");
        assertThat(response.bairro()).isEqualTo("Centro");
        assertThat(response.localidade()).isEqualTo("Belo Horizonte");
        assertThat(response.uf()).isEqualTo("MG");
        assertThat(response.consultadoEm()).isEqualTo(agora);
    }

    private ConsultaCep.ConsultaCepBuilder consultaCepBuilder() {
        return ConsultaCep.builder();
    }
}
