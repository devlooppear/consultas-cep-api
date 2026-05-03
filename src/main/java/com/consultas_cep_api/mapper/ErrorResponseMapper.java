package com.consultas_cep_api.mapper;

import com.consultas_cep_api.dto.ErrorResponse;
import com.consultas_cep_api.exceptions.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ErrorResponseMapper {

    public ResponseEntity<ErrorResponse> toResponse(ErrorCode error, String endpoint) {
    return ResponseEntity.status(error.getStatus())
        .body(new ErrorResponse(error.getStatus().value(), error.getMensagem(), endpoint));
    }

    public ResponseEntity<ErrorResponse> toResponse(HttpStatus status, String mensagem, String endpoint) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(status.value(), mensagem, endpoint));
    }
}