package org.example.uexmapapplication.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.uexmapapplication.dto.request.ContatoRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
public class GeocodingService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("AIzaSyCL_hkZhxb2wTaWmMYTjWgwZG0ITjtyAhQ")
    private String apiKey;

    private static final String GEOCODING_API_URL = "https://maps.googleapis.com/maps/api/geocode/json";

    public static record Coordenadas(Double lat, Double lng) {}

    public Coordenadas getCoordenadas(ContatoRequestDTO dto) {
        try {
            String address = String.join(", ",
                    dto.getLogradouro(),
                    dto.getNumero(),
                    dto.getBairro(),
                    dto.getCidade(),
                    dto.getUf());

            String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);

            String url = UriComponentsBuilder.fromHttpUrl(GEOCODING_API_URL)
                    .queryParam("address", encodedAddress)
                    .queryParam("key", apiKey)
                    .toUriString();

            JsonNode response = restTemplate.getForObject(url, JsonNode.class);

            if (response != null && response.path("status").asText().equals("OK")) {
                JsonNode location = response.path("results").get(0).path("geometry").path("location");
                Double lat = location.path("lat").asDouble();
                Double lng = location.path("lng").asDouble();
                return new Coordenadas(lat, lng);
            } else {
                throw new RuntimeException("Falha ao obter geocodificação: " + response.path("status").asText());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao chamar API de Geocoding: " + e.getMessage());
        }
    }
}