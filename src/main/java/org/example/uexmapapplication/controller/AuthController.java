package org.example.uexmapapplication.controller;

import jakarta.validation.Valid;
import org.example.uexmapapplication.dto.request.LoginRequest;
import org.example.uexmapapplication.dto.request.RegisterRequest;
import org.example.uexmapapplication.dto.response.JwtResponse;
import org.example.uexmapapplication.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.login(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        authService.registrar(registerRequest);
        return ResponseEntity.ok("Usuário registrado com sucesso!");
    }
}