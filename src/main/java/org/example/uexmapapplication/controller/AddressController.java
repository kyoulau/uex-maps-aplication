package org.example.uexmapapplication.controller;

import jakarta.validation.constraints.NotBlank;
import org.example.uexmapapplication.dto.response.ViaCepResponseDTO;
import org.example.uexmapapplication.service.ViaCepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;

@RestController
@RequestMapping("/address") // Protegido pela Fase 2 (SecurityConfig)
@SecurityRequirement(name = "bearerAuth")
@Validated
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
            @RequestParam @NotBlank(message = "O parâmetro 'uf' não pode ser vazio") String uf,
            @RequestParam @NotBlank(message = "O parâmetro 'cidade' não pode ser vazio") String cidade,
            @RequestParam @NotBlank(message = "O parâmetro 'logradouro' não pode ser vazio") String logradouro) {
        try {
            System.out.println("=== BUSCA VIA CEP INICIADA ===");
            System.out.println("Parâmetros recebidos:");
            System.out.println("UF: '" + uf + "'");
            System.out.println("Cidade: '" + cidade + "'");
            System.out.println("Logradouro: '" + logradouro + "'");

            List<ViaCepResponseDTO> results = viaCepService.findByAddress(uf, cidade, logradouro);

            System.out.println("=== BUSCA VIA CEP CONCLUÍDA ===");
            System.out.println("Total de resultados: " + results.size());

            return ResponseEntity.ok(results);

        } catch (Exception e) {
            System.err.println("=== ERRO NO CONTROLLER ===");
            System.err.println("Erro: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(List.of());
        }
    }
}