package org.example.uexmapapplication.controller;

import jakarta.validation.Valid;
import org.example.uexmapapplication.dto.request.ContatoRequestDTO;
import org.example.uexmapapplication.dto.response.ContatoResponse;
import org.example.uexmapapplication.service.ContatoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contacts")

public class ContatoController {

    @Autowired
    private ContatoService contatoService;

    // POST /contacts
    @PostMapping
    public ResponseEntity<ContatoResponse> create(@Valid @RequestBody ContatoRequestDTO requestDTO) {
        ContatoResponse responseDTO = contatoService.createContato(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    // GET /contacts
    @GetMapping
    public ResponseEntity<Page<ContatoResponse>> list(
            // Requisito: Ordenação padrão alfabética
            @PageableDefault(sort = "nome") Pageable pageable,
            // Requisito: Filtro por nome ou CPF
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cpf
    ) {
        Page<ContatoResponse> page = contatoService.listContatos(pageable, nome, cpf);
        return ResponseEntity.ok(page);
    }

    // GET /contacts/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ContatoResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(contatoService.getContatoById(id));
    }

    // PUT /contacts/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ContatoResponse> update(@PathVariable Long id, @Valid @RequestBody ContatoRequestDTO requestDTO) {
        return ResponseEntity.ok(contatoService.updateContato(id, requestDTO));
    }

    // DELETE /contacts/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        contatoService.deleteContato(id);
        return ResponseEntity.noContent().build();
    }
}