package br.uniesp.si.techback.service;

import br.uniesp.si.techback.dto.FuncionarioDTO;
import br.uniesp.si.techback.mapper.FuncionarioMapper;
import br.uniesp.si.techback.model.Funcionario;
import br.uniesp.si.techback.repository.FuncionarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do FuncionarioService")
class FuncionarioServiceTest {

    @Mock
    private FuncionarioRepository repository;

    @Mock
    private FuncionarioMapper mapper;

    @InjectMocks
    private FuncionarioService service;

    private Funcionario funcionario;
    private FuncionarioDTO dto;

    @BeforeEach
    void setUp() {
        funcionario = Funcionario.builder().id(1L).nome("Bruna").cargo("Analista").build();
        dto = FuncionarioDTO.builder().id(1L).nome("Bruna").cargo("Analista").build();
    }

    @Test
    @DisplayName("Deve salvar um novo funcionário")
    void deveSalvarNovoFuncionario() {
        when(mapper.toEntity(any(FuncionarioDTO.class))).thenReturn(funcionario);
        when(repository.save(any(Funcionario.class))).thenReturn(funcionario);
        when(mapper.toDTO(any(Funcionario.class))).thenReturn(dto);

        FuncionarioDTO resultado = service.salvar(dto);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("Bruna");
        verify(repository).save(any(Funcionario.class));
    }

    @Test
    @DisplayName("Deve listar todos os funcionários")
    void deveListarTodosOsFuncionarios() {
        when(repository.findAll()).thenReturn(List.of(funcionario));
        when(mapper.toDTO(funcionario)).thenReturn(dto);

        List<FuncionarioDTO> resultado = service.listarTodos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.getFirst().getNome()).isEqualTo("Bruna");
        verify(repository).findAll();
        verify(mapper).toDTO(funcionario);
    }

    @Test
    @DisplayName("Deve buscar funcionário por ID quando existir")
    void deveBuscarFuncionarioPorIdQuandoExistir() {
        when(repository.findById(1L)).thenReturn(Optional.of(funcionario));
        when(mapper.toDTO(funcionario)).thenReturn(dto);

        FuncionarioDTO resultado = service.buscarPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getCargo()).isEqualTo("Analista");
        verify(repository).findById(1L);
        verify(mapper).toDTO(funcionario);
    }

    @Test
    @DisplayName("Deve lançar erro 404 ao buscar ID que não existe")
    void deveLancarErroAoBuscarIdInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(erro -> {
                    ResponseStatusException exception = (ResponseStatusException) erro;
                    assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(exception.getReason()).isEqualTo("Funcionário não encontrado");
                });
    }

    @Test
    @DisplayName("Deve atualizar funcionário existente")
    void deveAtualizarFuncionarioExistente() {
        FuncionarioDTO dtoAtualizado = FuncionarioDTO.builder()
                .id(1L)
                .nome("Bruna Medeiros")
                .cargo("Coordenadora")
                .build();

        Funcionario funcionarioAtualizado = Funcionario.builder()
                .id(1L)
                .nome("Bruna Medeiros")
                .cargo("Coordenadora")
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(funcionario));
        when(repository.save(funcionario)).thenReturn(funcionarioAtualizado);
        when(mapper.toDTO(funcionarioAtualizado)).thenReturn(dtoAtualizado);

        FuncionarioDTO resultado = service.atualizar(1L, dtoAtualizado);

        assertThat(resultado.getNome()).isEqualTo("Bruna Medeiros");
        assertThat(funcionario.getNome()).isEqualTo("Bruna Medeiros");
        assertThat(funcionario.getCargo()).isEqualTo("Coordenadora");
        verify(repository).findById(1L);
        verify(repository).save(funcionario);
        verify(mapper).toDTO(funcionarioAtualizado);
    }

    @Test
    @DisplayName("Deve lançar erro 404 ao atualizar ID inexistente")
    void deveLancarErroAoAtualizarIdInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.atualizar(99L, dto))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(erro -> {
                    ResponseStatusException exception = (ResponseStatusException) erro;
                    assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(exception.getReason()).isEqualTo("Funcionário não encontrado");
                });

        verify(repository).findById(99L);
        verify(repository, never()).save(any(Funcionario.class));
        verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("Deve deletar funcionário existente")
    void deveDeletarFuncionarioExistente() {
        when(repository.existsById(1L)).thenReturn(true);

        service.deletar(1L);

        verify(repository).existsById(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar erro 404 ao deletar ID inexistente")
    void deveLancarErroAoDeletarIdInexistente() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.deletar(99L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(erro -> {
                    ResponseStatusException exception = (ResponseStatusException) erro;
                    assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(exception.getReason()).isEqualTo("Funcionário não encontrado");
                });

        verify(repository).existsById(99L);
        verify(repository, never()).deleteById(any(Long.class));
    }
}
