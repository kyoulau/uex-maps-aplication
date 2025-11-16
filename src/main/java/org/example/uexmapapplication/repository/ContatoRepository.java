package org.example.uexmapapplication.repository;

import org.example.uexmapapplication.domain.Contato;
import org.example.uexmapapplication.domain.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface ContatoRepository {

    Page<Contato> findByUsuario(Usuario usuario, Pageable pageable);

    Page<Contato> findByUsuarioAndNomeContainingIgnoreCase(Usuario usuario, String nome, Pageable pageable);

    Page<Contato> findByUsuarioAndCpfContaining(Usuario usuario, String cpf, Pageable pageable);
}
