package org.example.uexmapapplication.service;

import org.example.uexmapapplication.domain.Contato;
import org.example.uexmapapplication.domain.Usuario;
import org.example.uexmapapplication.dto.request.ContatoRequestDTO;
import org.example.uexmapapplication.dto.response.ContatoResponse;
import org.example.uexmapapplication.dto.mapper.ContatoMapper;
import org.example.uexmapapplication.repository.ContatoRepository;
import org.example.uexmapapplication.service.GeocodingService;
import org.example.uexmapapplication.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContatoService {

    @Autowired
    private ContatoRepository contatoRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private GeocodingService geocodingService;

    @Autowired
    private ContatoMapper contatoMapper;

    // CREATE
    @Transactional
    public ContatoResponse createContato(ContatoRequestDTO requestDTO) {
        // 1. Pega o usuário autenticado
        Usuario usuario = usuarioService.getAuthenticatedUser();

        // 2. Obtém as coordenadas
        GeocodingService.Coordenadas coords = geocodingService.getCoordenadas(requestDTO);

        // 3. Mapeia DTO para Entidade
        Contato contato = contatoMapper.toEntity(requestDTO);

        // 4. Associa o usuário e as coordenadas
        contato.setUsuario(usuario);
        contato.setLatitude(coords.lat());
        contato.setLongitude(coords.lng());

        // 5. Salva
        Contato savedContato = contatoRepository.save(contato);

        return contatoMapper.toResponseDTO(savedContato);
    }

    // READ (Listagem com filtro e paginação)
    @Transactional(readOnly = true)
    public Page<ContatoResponse> listContatos(Pageable pageable, String nome, String cpf) {
        Usuario usuario = usuarioService.getAuthenticatedUser();
        Page<Contato> contatosPage;

        if (nome != null && !nome.isBlank()) {
            contatosPage = contatoRepository.findByUsuarioAndNomeContainingIgnoreCase(usuario, nome, pageable);
        } else if (cpf != null && !cpf.isBlank()) {
            contatosPage = contatoRepository.findByUsuarioAndCpfContaining(usuario, cpf, pageable);
        } else {
            // Ordenação padrão (alfabética)
            contatosPage = contatoRepository.findByUsuario(usuario, pageable);
        }

        return contatosPage.map(contatoMapper::toResponseDTO);
    }

    // READ (By ID)
    @Transactional(readOnly = true)
    public ContatoResponse getContatoById(Long id) {
        Contato contato = findContatoByIdAndUsuario(id);
        return contatoMapper.toResponseDTO(contato);
    }

    // UPDATE
    @Transactional
    public ContatoResponse updateContato(Long id, ContatoRequestDTO requestDTO) {
        // Garante que o contato pertence ao usuário logado
        Contato contato = findContatoByIdAndUsuario(id);

        // Atualiza os dados da entidade com o DTO
        contatoMapper.updateEntityFromDTO(requestDTO, contato);

        // Recalcula as coordenadas, pois o endereço pode ter mudado
        GeocodingService.Coordenadas coords = geocodingService.getCoordenadas(requestDTO);
        contato.setLatitude(coords.lat());
        contato.setLongitude(coords.lng());

        Contato updatedContato = contatoRepository.save(contato);
        return contatoMapper.toResponseDTO(updatedContato);
    }

    // DELETE
    @Transactional
    public void deleteContato(Long id) {
        // Garante que o contato pertence ao usuário logado
        Contato contato = findContatoByIdAndUsuario(id);
        contatoRepository.delete(contato);
    }

    // Método auxiliar para garantir que o usuário não mexa no contato dos outros
    private Contato findContatoByIdAndUsuario(Long contatoId) {
        Usuario usuario = usuarioService.getAuthenticatedUser();
        return contatoRepository.findById(contatoId)
                .filter(contato -> contato.getUsuario().equals(usuario))
                .orElseThrow(() -> new RuntimeException("Contato não encontrado ou não pertence a este usuário"));
    }
}