package org.example.uexmapapplication.controller;

import org.example.uexmapapplication.dto.response.ViaCepResponseDTO;
import org.example.uexmapapplication.service.ViaCepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;

@RestController
@RequestMapping("/address") // Protegido pela Fase 2 (SecurityConfig)
@SecurityRequirement(name = "bearerAuth")
public class AddressController {

    @Autowired
    private ViaCepService viaCepService;

    /**
     * Endpoint de proxy para buscar endereço por CEP.
     */
    @Operation(
            summary = "Busca endereço por CEP",
            description = "Proxy obrigatório para o ViaCep. Busca um endereço único pelo CEP."
    )
    @ApiResponse(responseCode = "200", description = "Endereço encontrado")
    @GetMapping("/cep/{cep}")
    public ResponseEntity<ViaCepResponseDTO> searchByCep(@PathVariable String cep) {
        return ResponseEntity.ok(viaCepService.findByCep(cep));
    }

    /**
     * Endpoint de proxy para buscar endereços por UF, Cidade e Logradouro.
     */
    @Operation(
            summary = "Busca endereços por Logradouro",
            description = "Proxy para o ViaCep que busca uma lista de possíveis endereços baseado na UF, Cidade e Logradouro."
    )
    @ApiResponse(responseCode = "200", description = "Lista de endereços encontrada (pode estar vazia)")
    @GetMapping("/search")
    public ResponseEntity<List<ViaCepResponseDTO>> searchByAddress(
            @RequestParam String uf,
            @RequestParam String cidade,
            @RequestParam String logradouro) {
        return ResponseEntity.ok(viaCepService.findByAddress(uf, cidade, logradouro));
    }
}