package org.example.uexmapapplication.dto.mapper;

import org.example.uexmapapplication.domain.Contato;
import org.example.uexmapapplication.dto.request.ContatoRequestDTO;
import org.example.uexmapapplication.dto.response.ContatoResponse;
import org.springframework.stereotype.Component;

@Component
public class ContatoMapper {

    public Contato toEntity(ContatoRequestDTO dto) {
        Contato contato = new Contato();
        contato.setNome(dto.getNome());
        contato.setCpf(dto.getCpf().replaceAll("[^\\d]", "")); // Salva o CPF limpo
        contato.setTelefone(dto.getTelefone());
        contato.setLogradouro(dto.getLogradouro());
        contato.setNumero(dto.getNumero());
        contato.setBairro(dto.getBairro());
        contato.setCidade(dto.getCidade());
        contato.setUf(dto.getUf());
        contato.setCep(dto.getCep());
        contato.setComplemento(dto.getComplemento());
        return contato;
    }

    public ContatoResponse toResponseDTO(Contato contato) {
        ContatoResponse dto = new ContatoResponse();
        dto.setId(contato.getId());
        dto.setNome(contato.getNome());
        dto.setCpf(contato.getCpf());
        dto.setTelefone(contato.getTelefone());
        dto.setLogradouro(contato.getLogradouro());
        dto.setNumero(contato.getNumero());
        dto.setBairro(contato.getBairro());
        dto.setCidade(contato.getCidade());
        dto.setUf(contato.getUf());
        dto.setCep(contato.getCep());
        dto.setComplemento(contato.getComplemento());
        dto.setLatitude(contato.getLatitude());
        dto.setLongitude(contato.getLongitude());
        return dto;
    }

    public void updateEntityFromDTO(ContatoRequestDTO dto, Contato contato) {
        contato.setNome(dto.getNome());
        contato.setCpf(dto.getCpf().replaceAll("[^\\d]", ""));
        contato.setTelefone(dto.getTelefone());
        contato.setLogradouro(dto.getLogradouro());
        contato.setNumero(dto.getNumero());
        contato.setBairro(dto.getBairro());
        contato.setCidade(dto.getCidade());
        contato.setUf(dto.getUf());
        contato.setCep(dto.getCep());
        contato.setComplemento(dto.getComplemento());
    }
}