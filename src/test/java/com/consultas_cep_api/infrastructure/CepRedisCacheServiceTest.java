package com.consultas_cep_api.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.consultas_cep_api.dto.ViaCepResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CepRedisCacheServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private ObjectMapper objectMapper;

    private CepRedisCacheService cacheService;

    @BeforeEach
    void setup() {
        cacheService = new CepRedisCacheService(redisTemplate, objectMapper);
        ReflectionTestUtils.setField(cacheService, "ttlSeconds", 120L);
    }

    @Test
    void cenarioFeliz_getDeveRetornarObjetoDoCache() throws Exception {
        ViaCepResponse expected = viaCepOk();
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("cep:recent:08030310")).thenReturn("json");
        when(objectMapper.readValue("json", ViaCepResponse.class)).thenReturn(expected);

        ViaCepResponse result = cacheService.get("08030310");

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void cenarioTriste_getQuandoNaoHaPayloadDeveRetornarNull() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("cep:recent:08030310")).thenReturn(null);

        ViaCepResponse result = cacheService.get("08030310");

        assertThat(result).isNull();
    }

    @Test
    void cenarioTriste_getComFalhaDeDesserializacaoDeveRetornarNull() throws Exception {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("cep:recent:08030310")).thenReturn("json");
        when(objectMapper.readValue("json", ViaCepResponse.class)).thenThrow(new RuntimeException("erro"));

        ViaCepResponse result = cacheService.get("08030310");

        assertThat(result).isNull();
    }

    @Test
    void regraDeNegocio_putComResponseNullNaoDeveGravar() {
        cacheService.put("08030310", null);

        verify(valueOperations, never()).set(any(), any(), any(Duration.class));
    }

    @Test
    void regraDeNegocio_putComErroTrueNaoDeveGravar() {
        cacheService.put("99999999", new ViaCepResponse("99999999", null, null, null, null, null, null, null, true));

        verify(valueOperations, never()).set(any(), any(), any(Duration.class));
    }

    @Test
    void cenarioFeliz_putDeveGravarComTtl() throws Exception {
        ViaCepResponse response = viaCepOk();
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(objectMapper.writeValueAsString(response)).thenReturn("json");

        cacheService.put("08030310", response);

        verify(valueOperations).set(eq("cep:recent:08030310"), eq("json"), eq(Duration.ofSeconds(120)));
    }

    @Test
    void cenarioTriste_putComFalhaJsonNaoDevePropagarExcecao() throws Exception {
        ViaCepResponse response = viaCepOk();
        when(objectMapper.writeValueAsString(response)).thenThrow(new JsonProcessingException("erro") {
        });

        cacheService.put("08030310", response);

        verify(valueOperations, never()).set(any(), any(), any(Duration.class));
    }

    @Test
    void cenarioTriste_putComFalhaNoRedisNaoDevePropagarExcecao() throws Exception {
        ViaCepResponse response = viaCepOk();
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(objectMapper.writeValueAsString(response)).thenReturn("json");
        org.mockito.Mockito.doThrow(new RuntimeException("redis indisponivel"))
                .when(valueOperations)
                .set(eq("cep:recent:08030310"), eq("json"), eq(Duration.ofSeconds(120)));

        cacheService.put("08030310", response);

        verify(valueOperations).set(eq("cep:recent:08030310"), eq("json"), eq(Duration.ofSeconds(120)));
    }

    private ViaCepResponse viaCepOk() {
        return new ViaCepResponse(
                "08030-310", "Rua Teste", "", "Itaquera", "Sao Paulo", "SP", "Sao Paulo", "11", false);
    }
}
