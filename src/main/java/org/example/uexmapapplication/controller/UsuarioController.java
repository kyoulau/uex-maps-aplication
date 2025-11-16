package org.example.uexmapapplication.controller;

import jakarta.validation.Valid;
import org.example.uexmapapplication.dto.request.DeleteAccountRequest;
import org.example.uexmapapplication.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @DeleteMapping("/me")
    public ResponseEntity<?> deleteMyAccount(@Valid @RequestBody DeleteAccountRequest deleteRequest) {
        usuarioService.deleteAccount(deleteRequest.getSenha());
        return ResponseEntity.ok("Conta excluída com sucesso.");
    }
}