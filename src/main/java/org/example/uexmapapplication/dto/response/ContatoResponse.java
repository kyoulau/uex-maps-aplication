package org.example.uexmapapplication.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContatoResponse {

    private Long id;
    private String nome;
    private String cpf;
    private String telefone;
    private String logradouro;
    private String numero;
    private String bairro;
    private String cidade;
    private String uf;
    private String cep;
    private String complemento;
    private Double latitude;
    private Double longitude;
}