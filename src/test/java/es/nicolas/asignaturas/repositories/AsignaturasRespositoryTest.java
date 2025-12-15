package es.nicolas.asignaturas.repositories;

import es.nicolas.asignaturas.models.Asignatura;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Sql(value = {"/reset.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DataJpaTest
class AsignaturasRespositoryTest {

    private final Asignatura asignatura = Asignatura.builder().nombre("Programacion").build();

    @Autowired
    private AsignaturasRespository repository;
    @Autowired
    private TestEntityManager entityManager; // EntityManager para hacer las pruebas

    @BeforeEach
    void setUp() {
        // Insertamos el titular antes de cada test
        entityManager.persist(asignatura);

        entityManager.flush();
    }

    @Test
    void findAll() {
        List<Asignatura> asignaturas = repository.findAll();
        assertAll("findAll",
                () -> assertNotNull(asignaturas),
                () -> assertFalse(asignaturas.isEmpty())
        );
    }

    @Test
    void findByNombre() {
        List<Asignatura> asignaturas = repository.findByNombreContainsIgnoreCase("Programacion");

        assertAll("findAllByNombre",
                () -> assertNotNull(asignaturas),
                () -> assertFalse(asignaturas.isEmpty()),
                () -> assertEquals("Programacion", asignaturas.getFirst().getNombre())
                );
    }

    @Test
    void findById() {
        Asignatura asignatura = repository.findById(1L).orElse(null);

        assertAll("findById",
                () -> assertNotNull(asignatura),
                () -> assertEquals("Programacion", asignatura.getNombre())
        );
    }

    @Test
    void findByIdNotFound() {
        Asignatura asignatura = repository.findById(100L).orElse(null);

        assertNull(asignatura);
    }

    @Test
    void save() {
        Asignatura asignatura = Asignatura.builder().nombre("Programacion").build();

        assertAll("save",
                ()-> assertNotNull(asignatura),
                () -> assertEquals("Programacion", asignatura.getNombre())
        );
    }

    @Test
    void update() {
        var asignaturaExistente = repository.findById(1L).orElse(null);

        Asignatura asignaturaToUpdate = Asignatura.builder()
                .id(asignaturaExistente.getId())
                .nombre("Programacion").build();
        Asignatura asignaturaUpdated = repository.save(asignaturaToUpdate);

        assertAll("update",
                () -> assertNotNull(asignaturaUpdated),
                () -> assertEquals("Programacion", asignaturaUpdated.getNombre())
        );
    }

    @Test
    void delete() {
        var asignaturaToDelete = repository.findById(1L).orElse(null);
        repository.delete(asignaturaToDelete);

        Asignatura asignaturaDeleted = repository.findById(1L).orElse(null);

        assertNull(asignaturaDeleted);
    }


    @Test
    void test_FetchType_EAGER_vs_LAZY() {
        entityManager.clear();

        Asignatura asignatura = repository.findById(1L).orElse(null);
        assertNotNull(asignatura);
    }
}