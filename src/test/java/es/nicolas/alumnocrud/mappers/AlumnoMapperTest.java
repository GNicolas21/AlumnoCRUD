package es.nicolas.alumnocrud.mappers;

import es.nicolas.alumnocrud.dto.AlumnoCreateDto;
import es.nicolas.alumnocrud.dto.AlumnoUpdateDto;
import es.nicolas.alumnocrud.models.Alumno;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AlumnoMapperTest {

    // Inyectamos el mapper
    private final AlumnoMapper alumnoMapper = new AlumnoMapper();

    @Test
    void toAlumno_create() {
        // Arrange
        AlumnoCreateDto alumnoCreateDto = AlumnoCreateDto.builder()
                .nombre("Nicolas")
                .apellido("Osorio")
                .grado("2 DAW")
                .build();

        // Act
        var res = alumnoMapper.toAlumno(alumnoCreateDto);

        // Assert
        assertAll(
                () -> assertEquals(alumnoCreateDto.getNombre(), res.getNombre()),
                () -> assertEquals(alumnoCreateDto.getApellido(), res.getApellido()),
                () -> assertEquals(alumnoCreateDto.getGrado(), res.getGrado())
        );
    }

    @Test
    void toAlumno_update() {
        // Arrange
        Long id = 1L;
        AlumnoUpdateDto alumnoUpdateDto = AlumnoUpdateDto.builder()
                .nombre("Nicolas")
                .apellido("Osorio")
                .grado("2 DAW")
                .build();

        Alumno alumno = Alumno.builder()
                .id(id)
                .nombre(alumnoUpdateDto.getNombre())
                .apellido(alumnoUpdateDto.getApellido())
                .grado(alumnoUpdateDto.getGrado())
                .build();

        // Act
        var res = alumnoMapper.toAlumno(alumnoUpdateDto, alumno);

        // Assert
        assertAll(
                () -> assertEquals(id, res.getId()),
                () -> assertEquals(alumnoUpdateDto.getNombre(), res.getNombre()),
                () -> assertEquals(alumnoUpdateDto.getApellido(), res.getApellido()),
                () -> assertEquals(alumnoUpdateDto.getGrado(), res.getGrado())
        );
    }

    @Test
    void toAlumnoResponseDto() {
        // Arrange
        Alumno alumno = Alumno.builder()
                .id(1L)
                .nombre("Nicolas")
                .apellido("Osorio")
                .grado("2 DAW")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .uuid(java.util.UUID.randomUUID())
                .build();

        // Act
        var res = alumnoMapper.toAlumnoResponseDto(alumno);
        // Assert
        assertAll(
                () -> assertEquals(alumno.getId(), res.getId()),
                () -> assertEquals(alumno.getNombre(), res.getNombre()),
                () -> assertEquals(alumno.getApellido(), res.getApellido()),
                () -> assertEquals(alumno.getGrado(), res.getGrado()),
                () -> assertEquals(alumno.getCreatedAt(), res.getCreatedAt()),
                () -> assertEquals(alumno.getUpdatedAt(), res.getUpdatedAt()),
                () -> assertEquals(alumno.getUuid(), res.getUuid())
        );

    }
}