package br.uniesp.si.techback.controller;

import br.uniesp.si.techback.dto.FuncionarioDTO;
import br.uniesp.si.techback.service.FuncionarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/funcionarios")
@RequiredArgsConstructor
@Tag(name = "Funcionários", description = "API para gerenciamento de funcionários do projeto")
public class FuncionarioController {

    private final FuncionarioService service;

    @PostMapping
    @Operation(summary = "Criar um novo funcionário", description = "Recebe os dados e salva um novo funcionário validado no banco de dados.")
    public ResponseEntity<FuncionarioDTO> criar(@Valid @RequestBody FuncionarioDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(dto));
    }

    @GetMapping
    @Operation(summary = "Listar todos os funcionários", description = "Retorna uma lista com todos os funcionários cadastrados.")
    public ResponseEntity<List<FuncionarioDTO>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar funcionário por ID", description = "Busca os detalhes de um funcionário específico através do seu ID.")
    public ResponseEntity<FuncionarioDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um funcionário", description = "Atualiza os dados de um funcionário existente com base no ID fornecido.")
    public ResponseEntity<FuncionarioDTO> atualizar(@PathVariable Long id, @Valid @RequestBody FuncionarioDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um funcionário", description = "Remove um funcionário do banco de dados pelo seu ID.")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}