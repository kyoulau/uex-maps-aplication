package org.example.uexmapapplication.service;

import org.example.uexmapapplication.dto.response.ViaCepResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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
        String url = UriComponentsBuilder.fromHttpUrl(VIACEP_URL)
                .pathSegment(cep, "json")
                .toUriString();

        return restTemplate.getForObject(url, ViaCepResponseDTO.class);
    }

    /**
     * Busca uma *lista* de endereços por UF, Cidade e Logradouro.
     * (Conforme requisito original do PDF )
     */
    public List<ViaCepResponseDTO> findByAddress(String uf, String cidade, String logradouro) {
        String url = UriComponentsBuilder.fromHttpUrl(VIACEP_URL)
                .pathSegment(uf, cidade, logradouro, "json")
                .toUriString();

        // Como o ViaCep retorna um array JSON, precisamos usar o ParameterizedTypeReference
        ResponseEntity<List<ViaCepResponseDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ViaCepResponseDTO>>() {}
        );
        return response.getBody();
    }
}