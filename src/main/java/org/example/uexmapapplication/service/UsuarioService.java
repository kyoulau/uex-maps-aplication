package org.example.uexmapapplication.service;

import org.example.uexmapapplication.domain.Usuario;
import org.example.uexmapapplication.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Método auxiliar para pegar o usuário logado
    public Usuario getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }

    public void deleteAccount(String senha) {
        Usuario usuario = getAuthenticatedUser();

        // O requisito: "O usuário deve informar a sua senha para excluir a conta"
        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new RuntimeException("Senha inválida!");
        }

        usuarioRepository.delete(usuario);
    }
}