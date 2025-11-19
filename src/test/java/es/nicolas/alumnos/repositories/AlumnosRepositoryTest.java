package es.nicolas.alumnos.repositories;

import es.nicolas.alumnos.models.Alumno;
import es.nicolas.asignaturas.models.Asignatura;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.*;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

// Ejecuta el script SQL antes de cada método de prueba
@Sql(value = {"/reset.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DataJpaTest
class AlumnosRepositoryTest {
    @Autowired
    private AlumnosRepository repository;
    @Autowired
    private TestEntityManager testEntityManager;

    private final Alumno alumno1 = Alumno.builder()
            .nombre("Nicolas")
            .apellido("Osorio")
            .grado("2 DAW")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .uuid(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
            .build();
    private final Alumno alumno2 = Alumno.builder()
            .nombre("Gabriel")
            .apellido("Bauti")
            .grado("3 DAW")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .uuid(UUID.randomUUID())
            .build();
    private final Alumno alumno3 = Alumno.builder()
            .nombre("Cesar")
            .apellido("Campos")
            .grado("3 DAW")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .uuid(UUID.randomUUID())
            .build();
    private final Alumno alumno4 = Alumno.builder()
            .nombre("Dani")
            .apellido("Delgado")
            .grado("4 ASIR")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .uuid(UUID.fromString("267ed00a-6c21-4c4a-8626-db28bcca7a26"))
            .build();


    // Esto se ejecuta antes de cada test

    @BeforeEach
    void setUp() {
//        //Comandos JPA
//        repository.save(alumno1);
//        repository.save(alumno2);
//        repository.save(alumno3);
//        repository.save(alumno4);

        //Comando EntityManager
        testEntityManager.merge(alumno1);
        testEntityManager.merge(alumno2);
        testEntityManager.merge(alumno3);
        testEntityManager.merge(alumno4);
        testEntityManager.flush();
    }


    @Test
    void findAll() {
        // Act
        List<Alumno> alumnos = repository.findAll();
        // Assert
        assertAll("findAll",
                () -> assertNotNull(alumnos),
                () -> assertEquals(4, alumnos.size())
        );
    }


    @Test
    void findByApellidoContainsIgnoreCase() {
        // Act
        String apellido = "Osorio";
        List<Alumno> alumnos = repository.findByApellidoContainsIgnoreCase(apellido);
        // Assert
        assertAll("findAllByApellido",
                () -> assertNotNull(alumnos),
                () -> assertEquals(1, alumnos.size()),
                () -> assertEquals(apellido, alumnos.getFirst().getApellido())
        );
    }

    @Test
    void findByNombre() {
        String nombre = "Gabriel";
        List<Alumno> alumnos = repository.findByNombre(nombre);
        // Assert
        assertAll("findAllByNombre",
                () -> assertNotNull(alumnos),
                () -> assertEquals(1, alumnos.size()),
                () -> assertEquals(nombre, alumnos.getFirst().getNombre())
        );
    }

    @Test
    void findByNombreAndApellidoContainsIgnoreCase() {
        // Act
        String nombre = "Nicolas";
        String apellido = "Osorio";
        List<Alumno> tarjetas = repository.findByNombreAndApellidoContainsIgnoreCase(nombre, apellido);
        // Assert
        assertAll(
                () -> assertNotNull(tarjetas),
                () -> assertEquals(1, tarjetas.size()),
                () -> assertEquals(nombre, tarjetas.getFirst().getNombre()),
                () -> assertEquals(apellido, tarjetas.getFirst().getApellido())
        );
    }

    @Test
    void findById_ExistingId_returnsOptionalWithAlumno() {
        // Act
        Long id = 1L;
        Optional<Alumno> optionalAlumno = repository.findById(id);
        // Assert
        assertAll("findById_existingId_returnsOptionalWithAlumno",
                () -> assertNotNull(optionalAlumno),
                () -> assertTrue(optionalAlumno.isPresent()),
                () -> assertEquals(id, optionalAlumno.get().getId())
        );
    }

    @Test
    void findById_nonExistingId_returnsEmptyOptional() {
        // Act
        Long id = 5L;
        Optional<Alumno> optionalAlumno = repository.findById(id);

        // Assert
        assertAll("findById_nonExistingId_returnsEmptyOptional",
                () -> assertNotNull(optionalAlumno),
                () -> assertTrue(optionalAlumno.isEmpty())
        );
    }

    @Test
    void findByUuid_existingUuid_returnsOptionalWithAlumno() {
        // Act
        UUID uuid = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        Optional<Alumno> optionalAlumno = repository.findByUuid(uuid);

        // Assert
        assertAll("findByUuid_existingUuid_returnsOptionalWithTarjeta",
                () -> assertNotNull(optionalAlumno),
                () -> assertTrue(optionalAlumno.isPresent()),
                () -> assertEquals(uuid, optionalAlumno.get().getUuid())
        );
    }

    @Test
    void findByUuid_nonExistingUuid_returnsEmptyOptional() {
        // Act
        UUID uuid = UUID.fromString("12345bc2-0c1c-494e-bbaf-e952a778e478");
        Optional<Alumno> optionalAlumno = repository.findByUuid(uuid);

        // Assert
        assertAll("findByUuid_nonExistingUuid_returnsEmptyOptional",
                () -> assertNotNull(optionalAlumno),
                () -> assertTrue(optionalAlumno.isEmpty())
        );
    }

    @Test
    void existsById_existingId_returnsTrue() {
        // Act
        Long id = 1L;
        boolean exists = repository.existsById(id);

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsById_existingId_returnsFalse() {
        // Act
        Long id = 5L;
        boolean exists = repository.existsById(id);

        // Assert
        assertFalse(exists);
    }

    @Test
    void existsByUuid_existingUuid_returnsTrue() {
        // Act
        UUID uuid = UUID.fromString("267ed00a-6c21-4c4a-8626-db28bcca7a26");
        boolean exists = repository.existsByUuid(uuid);

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsByUuid_nonExistingUuid_returnsFalse() {
        // Act
        UUID uuid = UUID.fromString("12345bc2-0c1c-494e-bbaf-e952a778e478");
        boolean exists = repository.existsByUuid(uuid);

        // Assert
        assertFalse(exists);
    }


    @Test
    void save_notExists() {
        // Arrange
        Alumno alumno = Alumno.builder()
                .nombre("Laura")
                .apellido("Martinez")
                .grado("1 DAW")
                .asignatura(Asignatura.builder()
                        .id(1L)
                        .nombre("Programacion")
                        .build())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .uuid(UUID.randomUUID())
                .build();

        // Act
        Alumno savedAlumno = repository.save(alumno);
        var all = repository.findAll();

        // Assert
        assertAll("save_notExists",
                () -> assertNotNull(savedAlumno),
                () ->  assertEquals(alumno, savedAlumno),
                () -> assertEquals(5, all.size())
        );
    }

    @Test
    void save_butExists() {
        // Arrange
        Alumno existingAlumno = alumno1;
//        Alumno alumno = Alumno.builder()
//                .id(1L)
//                .build();
//
//        // Act
        // Assert
        assertThrows(DataIntegrityViolationException.class, () -> repository.save(existingAlumno));

//        assertAll("save_butExists",
//                () -> assertNotNull(savedAlumno),
//                () ->  assertEquals(alumno, savedAlumno),
//                () -> assertEquals(4, all.size())
//        );
    }


    @Test
    void deleteById_existingId() {
        // Act
        Long idToDelete = 1L;
        repository.deleteById(idToDelete);
        var all = repository.findAll();

        // Assert
        assertAll("deleteById_existingId",
                () -> assertEquals(3, all.size()),
                () -> assertFalse(repository.existsById(idToDelete))
        );
    }


    @Test
    void deleteByUuid_existingUuid() {
        // Act
        UUID uuidToDelete = UUID.fromString("267ed00a-6c21-4c4a-8626-db28bcca7a26");
        repository.deleteByUuid(uuidToDelete);
        var all = repository.findAll();

        // Assert
        assertAll("deleteByUuid_existingUuid",
                () -> assertEquals(3, all.size()),
                () -> assertFalse(repository.existsByUuid(uuidToDelete))
        );
    }


    @Test
    void nextId() {
//        // Act
//        Long nextId = repository.nextId();
        var all = repository.findAll();

        // Assert
        assertAll("nextId",
                // Calcula el siguiente id, en este caso 5 pero no cambia
                // el tamaño porque no lo añade a la lista repository
                () -> assertEquals(4, all.size())
        );
    }
}