package br.uniesp.si.techback.controller;

import br.uniesp.si.techback.dto.FuncionarioDTO;
import br.uniesp.si.techback.service.FuncionarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FuncionarioController.class)
@DisplayName("Testes do FuncionarioController")
class FuncionarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FuncionarioService service;

    @Autowired
    private ObjectMapper objectMapper;

    private FuncionarioDTO dto;

    @BeforeEach
    void setUp() {
        dto = FuncionarioDTO.builder().id(1L).nome("Guilherme").cargo("Dev").build();
    }

    @Test
    @DisplayName("Deve retornar 201 Created ao criar funcionário válido")
    void deveCriarFuncionario() throws Exception {
        when(service.salvar(any(FuncionarioDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nome").value("Guilherme"));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao mandar nome vazio")
    void deveRetornarErroValidacao() throws Exception {
        FuncionarioDTO dtoInvalido = FuncionarioDTO.builder().cargo("Dev").build();

        mockMvc.perform(post("/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve listar todos os funcionários")
    void deveListarTodosOsFuncionarios() throws Exception {
        when(service.listarTodos()).thenReturn(List.of(dto));

        mockMvc.perform(get("/funcionarios"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Guilherme"))
                .andExpect(jsonPath("$[0].cargo").value("Dev"));
    }

    @Test
    @DisplayName("Deve buscar funcionário por ID")
    void deveBuscarFuncionarioPorId() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(dto);

        mockMvc.perform(get("/funcionarios/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Guilherme"))
                .andExpect(jsonPath("$.cargo").value("Dev"));
    }

    @Test
    @DisplayName("Deve retornar 404 quando buscar funcionário inexistente")
    void deveRetornar404AoBuscarFuncionarioInexistente() throws Exception {
        when(service.buscarPorId(99L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado"));

        mockMvc.perform(get("/funcionarios/99"))
                .andExpect(status().isNotFound());
    }
}
