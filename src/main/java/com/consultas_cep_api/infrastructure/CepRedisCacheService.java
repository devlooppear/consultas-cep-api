package com.consultas_cep_api.infrastructure;

import com.consultas_cep_api.dto.ViaCepResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CepRedisCacheService {

    private static final String KEY_PREFIX = "cep:recent:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${cep.cache.ttl-seconds:${CEP_CACHE_TTL_SECONDS:1800}}")
    private long ttlSeconds;

    public ViaCepResponse get(String cep) {
        try {
            String payload = redisTemplate.opsForValue().get(key(cep));
            if (payload == null) {
                return null;
            }
            return objectMapper.readValue(payload, ViaCepResponse.class);
        } catch (Exception ex) {
            log.warn("Falha ao ler cache Redis para CEP {}: {}", cep, ex.getMessage());
            return null;
        }
    }

    public void put(String cep, ViaCepResponse response) {
        if (response == null || Boolean.TRUE.equals(response.erro())) {
            return;
        }

        try {
            String payload = objectMapper.writeValueAsString(response);
            redisTemplate.opsForValue().set(key(cep), payload, Duration.ofSeconds(ttlSeconds));
        } catch (JsonProcessingException ex) {
            log.warn("Falha ao serializar resposta de CEP {} para cache: {}", cep, ex.getMessage());
        } catch (Exception ex) {
            log.warn("Falha ao gravar cache Redis para CEP {}: {}", cep, ex.getMessage());
        }
    }

    private String key(String cep) {
        return KEY_PREFIX + cep;
    }
}
