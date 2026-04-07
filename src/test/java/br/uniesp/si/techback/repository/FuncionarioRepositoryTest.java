package br.uniesp.si.techback.repository;

import br.uniesp.si.techback.model.Funcionario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Testes do FuncionarioRepository")
class FuncionarioRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FuncionarioRepository repository;

    private Funcionario funcionario;

    @BeforeEach
    void setUp() {
        funcionario = Funcionario.builder()
                .nome("Carlos Guilherme Saldanha")
                .cargo("Desenvolvedor Back-End")
                .build();
    }

    @Test
    @DisplayName("Deve salvar um funcionário com sucesso")
    void deveSalvarFuncionario() {
        Funcionario salvo = repository.save(funcionario);

        assertThat(salvo).isNotNull();
        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getNome()).isEqualTo("Carlos Guilherme Saldanha");
    }

    @Test
    @DisplayName("Deve encontrar funcionário por ID")
    void deveEncontrarFuncionarioPorId() {
        Funcionario salvo = entityManager.persistAndFlush(funcionario);
        Optional<Funcionario> encontrado = repository.findById(salvo.getId());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getCargo()).isEqualTo("Desenvolvedor Back-End");
    }
}