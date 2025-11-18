package org.example.uexmapapplication;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.uexmapapplication.dto.request.ContatoRequestDTO;
import org.example.uexmapapplication.dto.request.RegisterRequest;
import org.example.uexmapapplication.repository.ContatoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
public class ContatoControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ContatoRepository contatoRepository;

    private String jwtTokenUser1;
    private String jwtTokenUser2;

    private final String CPF_TESTE_VALIDO = "14051017923";

    @BeforeEach
    void setUp() throws Exception {
        jwtTokenUser1 = setupUserAndGetToken("userlaura1.test@exemplo.com");
        jwtTokenUser2 = setupUserAndGetToken("userlaura2.test@exemplo.com");
    }

    private String setupUserAndGetToken(String email) throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail(email);
        registerRequest.setSenha("123456");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = loginResult.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("token").asText();
    }

    private ContatoRequestDTO createValidContatoDTO(String nome, String cpf) {
        ContatoRequestDTO dto = new ContatoRequestDTO();
        dto.setNome(nome);
        dto.setCpf(cpf);
        dto.setTelefone("999999999");
        dto.setLogradouro("Rua Visconde de Nacar");
        dto.setNumero("1470");
        dto.setBairro("Centro");
        dto.setCidade("Curitiba");
        dto.setUf("PR");
        dto.setCep("80410201");
        return dto;
    }

    @Test
    void naoDeveCriarContato_QuandoCpfDuplicadoParaMesmoUsuario() throws Exception {
        ContatoRequestDTO dto = createValidContatoDTO("Contato 1", CPF_TESTE_VALIDO);

        // 1. Cria o primeiro contato (DEVE FUNCIONAR)
        mockMvc.perform(post("/contacts")
                        .header("Authorization", "Bearer " + jwtTokenUser1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        // 2. Tenta criar o *mesmo* contato (mesmo CPF)
        // O Banco de Dados (UniqueConstraint) deve rejeitar isso.
        // e retornaria 400. Sem ele, o Spring retorna 500.
        mockMvc.perform(post("/contacts")
                        .header("Authorization", "Bearer " + jwtTokenUser1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError()); // 500 (ou 400 se você tratou a exceção)
    }
    @Test
    void deveListarContatos_EFiltrarPorNome() throws Exception {
        // 1. Cria dois contatos
        ContatoRequestDTO contatoA = createValidContatoDTO("Alice", "11111111111");
        ContatoRequestDTO contatoB = createValidContatoDTO("Bob", "22222222222");

        mockMvc.perform(post("/contacts").header("Authorization", "Bearer " + jwtTokenUser1)
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(contatoA)));
        mockMvc.perform(post("/contacts").header("Authorization", "Bearer " + jwtTokenUser1)
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(contatoB)));

        // 2. Testa a listagem COMPLETA (deve ter 2 contatos)
        mockMvc.perform(get("/contacts")
                        .header("Authorization", "Bearer " + jwtTokenUser1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));

        // 3. Testa o FILTRO (deve ter 1 contato)
        mockMvc.perform(get("/contacts")
                        .header("Authorization", "Bearer " + jwtTokenUser1)
                        .param("nome", "Ali")) // Filtro por "Ali"
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].nome", is("Alice")));
    }

    // --- TESTE 3: DELETE ---
    @Test
    void deveDeletarContato_QuandoUsuarioEDono() throws Exception {
        // 1. Cria o contato
        ContatoRequestDTO dto = createValidContatoDTO("Contato Para Deletar", "33333333333");
        MvcResult createResult = mockMvc.perform(post("/contacts")
                        .header("Authorization", "Bearer " + jwtTokenUser1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = createResult.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        long contatoId = jsonNode.get("id").asLong();

        mockMvc.perform(delete("/contacts/" + contatoId)
                        .header("Authorization", "Bearer " + jwtTokenUser1))
                .andExpect(status().isNoContent()); // 204

        assertFalse(contatoRepository.findById(contatoId).isPresent());
    }

    @Test
    void naoDeveDeletarContato_QuandoUsuarioNaoEDono() throws Exception {
        ContatoRequestDTO dto = createValidContatoDTO("Contato do User 1", "44444444444");
        MvcResult createResult = mockMvc.perform(post("/contacts")
                        .header("Authorization", "Bearer " + jwtTokenUser1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        long contatoId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();

        // 2. Usuário 2 (o invasor) tenta deletar
        // O ContatoService deve lançar a RuntimeException("Contato não encontrado...")
        // que o Spring vai traduzir para 500 (Internal Server Error).
        mockMvc.perform(delete("/contacts/" + contatoId)
                        .header("Authorization", "Bearer " + jwtTokenUser2))
                .andExpect(status().isInternalServerError());
    }
}
