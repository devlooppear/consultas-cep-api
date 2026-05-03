package com.consultas_cep_api.exceptions;

import com.consultas_cep_api.dto.ErrorResponse;
import com.consultas_cep_api.mapper.ErrorResponseMapper;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorResponseMapper errorResponseMapper;

    @ExceptionHandler(CepNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCepNotFound(CepNotFoundException ex, HttpServletRequest request) {
        log.warn("CEP nao encontrado: {}", ex.getMessage());
        return errorResponseMapper.toResponse(ErrorCode.CEP_NAO_ENCONTRADO, request.getRequestURI());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ConstraintViolationException ex, HttpServletRequest request) {
        return errorResponseMapper.toResponse(ErrorCode.CEP_INVALIDO, request.getRequestURI());
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeign(FeignException ex, HttpServletRequest request) {
        log.warn("Erro ao consultar ViaCEP: status={}", ex.status());
        HttpStatusCode statusCode = HttpStatusCode.valueOf(Math.max(400, ex.status()));
        String mensagem = statusCode.is5xxServerError()
                ? ErrorCode.ERRO_CONSULTA_EXTERNA.getMensagem()
                : ErrorCode.CEP_NAO_ENCONTRADO.getMensagem();
        HttpStatus httpStatus = HttpStatus.resolve(statusCode.value());
        if (httpStatus == null) {
            httpStatus = ErrorCode.ERRO_CONSULTA_EXTERNA.getStatus();
        }
        return errorResponseMapper.toResponse(httpStatus, mensagem, request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Erro interno inesperado: {}", ex.getMessage());
        return errorResponseMapper.toResponse(ErrorCode.ERRO_INTERNO, request.getRequestURI());
    }
}
