package org.example.uexmapapplication.service;

import org.example.uexmapapplication.domain.Usuario;
import org.example.uexmapapplication.dto.request.LoginRequest;
import org.example.uexmapapplication.dto.request.RegisterRequest;
import org.example.uexmapapplication.dto.response.JwtResponse;
import org.example.uexmapapplication.repository.UsuarioRepository;
import org.example.uexmapapplication.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    public JwtResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getSenha()
                )
        );

        //o usuário é válido
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Gera o token
        String jwt = tokenProvider.generateToken(authentication);
        return new JwtResponse(jwt);
    }

    public void registrar(RegisterRequest registerRequest) {
        if (usuarioRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Erro: E-mail já está em uso!");
        }

        // Criptografa a senha ANTES de salvar
        Usuario usuario = new Usuario(
                registerRequest.getEmail(),
                passwordEncoder.encode(registerRequest.getSenha())
        );

        usuarioRepository.save(usuario);
    }
}
