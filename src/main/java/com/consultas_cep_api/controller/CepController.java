package com.consultas_cep_api.controller;

import com.consultas_cep_api.dto.CepResponse;
import com.consultas_cep_api.service.CepService;
import com.consultas_cep_api.utils.CepUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cep")
@RequiredArgsConstructor
@Validated
@Tag(name = "CEP", description = "Consultas de CEP via ViaCEP")
public class CepController {

    private final CepService cepService;

    @GetMapping("/{cep}")
    @Operation(summary = "Buscar CEP", description = "Consulta endereco pelo CEP na API ViaCEP e registra o resultado no banco.")
    public ResponseEntity<CepResponse> buscar(
            @PathVariable
            @Pattern(regexp = CepUtils.CEP_REGEX, message = "CEP deve ter 8 digitos.")
                @Parameter(
                    description = "CEP com ou sem hifen. CEPs disponiveis no mock: 01001000, 20040020, 30130110, 99999999 (erro).",
                    examples = {
                        @ExampleObject(name = "Sao Paulo", value = "01001000"),
                        @ExampleObject(name = "Rio de Janeiro", value = "20040020"),
                        @ExampleObject(name = "Belo Horizonte", value = "30130110"),
                        @ExampleObject(name = "Nao encontrado", value = "99999999")
                    })
            String cep) {
        return ResponseEntity.ok(cepService.buscarCep(cep));
    }

    @GetMapping("/consultas")
    @Operation(summary = "Listar consultas", description = "Retorna historico paginado de consultas de CEP realizadas.")
    public ResponseEntity<Page<CepResponse>> listar(
            @ParameterObject
            @PageableDefault(size = 10, sort = "consultadoEm", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(cepService.listarConsultas(pageable));
    }
}
