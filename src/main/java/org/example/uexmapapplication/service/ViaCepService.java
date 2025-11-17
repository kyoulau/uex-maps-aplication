package org.example.uexmapapplication.service;

import org.example.uexmapapplication.dto.response.ViaCepResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class ViaCepService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String VIACEP_URL = "https://viacep.com.br/ws";

    /**
     * Busca um endereço único pelo CEP.
     */
    public ViaCepResponseDTO findByCep(String cep) {
        String cleanCep = cleanCep(cep);
        String url = UriComponentsBuilder.fromHttpUrl(VIACEP_URL)
                .pathSegment(cleanCep, "json")
                .encode(StandardCharsets.UTF_8)
                .toUriString();

        System.out.println("Busca por CEP - URL: " + url);
        return restTemplate.getForObject(url, ViaCepResponseDTO.class);
    }

    /**
     * Busca uma *lista* de endereços por UF, Cidade e Logradouro.
     * (Conforme requisito original do PDF )
     */
    public List<ViaCepResponseDTO> findByAddress(String uf, String cidade, String logradouro) {
        try {
            String normalizedUf = normalizeParameter(uf).toUpperCase();
            String normalizedCidade = normalizeParameter(cidade);
            String normalizedLogradouro = normalizeParameter(logradouro);

            URI uri = UriComponentsBuilder.fromHttpUrl(VIACEP_URL)
                    .pathSegment(normalizedUf, normalizedCidade, normalizedLogradouro)
                    .path("json")
                    .encode(StandardCharsets.UTF_8)
                    .build()
                    .toUri();

            System.out.println("Busca por Endereço - URL: " + uri.toString());

            ResponseEntity<List<ViaCepResponseDTO>> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ViaCepResponseDTO>>() {}
            );

            List<ViaCepResponseDTO> result = response.getBody();
            System.out.println("Resultados encontrados: " + (result != null ? result.size() : 0));
            return result != null ? result : List.of();

        } catch (Exception e) {
            System.err.println("Erro ao buscar endereço no ViaCEP: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    private String normalizeParameter(String param) {
        if (param == null) return "";
        return param.trim().replaceAll("\\s+", " ");
    }

    private String cleanCep(String cep) {
        if (cep == null) return "";
        return cep.replaceAll("\\D", "");
    }
}