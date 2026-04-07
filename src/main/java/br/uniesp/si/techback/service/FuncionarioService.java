package br.uniesp.si.techback.service;

import br.uniesp.si.techback.dto.FuncionarioDTO;
import br.uniesp.si.techback.mapper.FuncionarioMapper;
import br.uniesp.si.techback.model.Funcionario;
import br.uniesp.si.techback.repository.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FuncionarioService {

    private final FuncionarioRepository repository;
    private final FuncionarioMapper mapper;

    // Cria um novo funcionário
    public FuncionarioDTO salvar(FuncionarioDTO dto) {
        Funcionario entidade = mapper.toEntity(dto);
        Funcionario salvo = repository.save(entidade);
        return mapper.toDTO(salvo);
    }

    // Lista todos
    public List<FuncionarioDTO> listarTodos() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    // Busca por ID (se não achar, lança erro 404)
    public FuncionarioDTO buscarPorId(Long id) {
        Funcionario funcionario = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado"));
        return mapper.toDTO(funcionario);
    }

    // Atualiza um funcionário existente
    public FuncionarioDTO atualizar(Long id, FuncionarioDTO dto) {
        Funcionario existente = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado"));

        // Atualiza os dados com base no que veio da web
        existente.setNome(dto.getNome());
        existente.setCargo(dto.getCargo());

        Funcionario atualizado = repository.save(existente);
        return mapper.toDTO(atualizado);
    }

    // Deleta pelo ID
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado");
        }
        repository.deleteById(id);
    }
}