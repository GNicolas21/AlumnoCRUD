package es.nicolas.alumnocrud.services;

import es.nicolas.alumnocrud.dto.AlumnoResponseDto;
import es.nicolas.alumnocrud.mappers.AlumnoMapper;
import es.nicolas.alumnocrud.models.Alumno;
import es.nicolas.alumnocrud.repositories.AlumnosRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Con esta anotación indicamos que usaremos Mockito en este test
@ExtendWith(MockitoExtension.class)
class AlumnosServiceImplTest {

    private final Alumno alumno1 = Alumno.builder()
            .id(1L)
            .nombre("Nicolas")
            .apellido("Osorio")
            .grado("2 DAW")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .uuid(UUID.fromString("267ed00a-6c21-4c4a-8626-db28bcca7a26"))
            .build();
    private final Alumno alumno2 = Alumno.builder()
            .id(2L)
            .nombre("Gabriel")
            .apellido("Bauti")
            .grado("3 DAW")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .uuid(UUID.fromString("367ed00a-6c21-4c4a-8626-db28bcca7a27"))
            .build();
    private final AlumnoResponseDto alumnoResponse1 = AlumnoResponseDto.builder()
            .id(1L)
            .nombre("Nicolas")
            .apellido("Osorio")
            .grado("2 DAW")
            .createdAt(alumno1.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .uuid(UUID.randomUUID())
            .build();
    private final AlumnoResponseDto alumnoResponse2 = AlumnoResponseDto.builder()
            .id(2L)
            .nombre("Gabriel")
            .apellido("Bauti")
            .grado("3 DAW")
            .build();

    @Mock
    private AlumnosRepository alumnosRepository;

    @Mock
    private AlumnoMapper alumnoMapper;

    @InjectMocks
    private AlumnosServiceImpl alumnosService;

    @Captor // Captor de argumentos
    // El captor es para que comprueba que los argumentos
    // que se pasan a un metodo son correctos
    private ArgumentCaptor<Alumno> alumnoCaptor;

    @Test
    void findAll_ShouldReturnAllAlumnos_WhenNoParametersProvided() {
        // Arrange
        List <Alumno> expectedAlumnos = Arrays.asList(alumno1, alumno2);
        List<AlumnoResponseDto> expectedAlumnoResponses =
                Arrays.asList(alumnoResponse1, alumnoResponse2);
        // el WHEN es para definir el comportamiento del mock
        // en este caso cuando se llame al metodo findAll del repositorio
        // devuelva la lista de alumnos esperada, osea aisla el sevice del repositorio
        when(alumnosRepository.findAll()).thenReturn(expectedAlumnos);
        when(alumnoMapper.toResponseDtoList(expectedAlumnos)).thenReturn(expectedAlumnoResponses);

        // Act
        List <AlumnoResponseDto> actualAlumnoResponses = alumnosService.findAll(null, null);

        // Assert
        assertIterableEquals( expectedAlumnoResponses,actualAlumnoResponses);

        // Verify
        verify(alumnosRepository, times(1)).findAll();
        verify(alumnoMapper, times(1)).toResponseDtoList(anyList());

    }

    @Test
    void findAll_ShouldReturnAlumnosByNombre_WhenNombreParameterProvided() {
        // Arrange
        List <Alumno> expectedAlumnos = Arrays.asList(alumno1, alumno2);
        List<AlumnoResponseDto> expectedAlumnoResponses =
                Arrays.asList(alumnoResponse1, alumnoResponse2);

        when(alumnosRepository.findByNombre("Nicolas")).thenReturn(expectedAlumnos);
        when(alumnoMapper.toResponseDtoList(expectedAlumnos)).thenReturn(expectedAlumnoResponses);

        // Act
        List <AlumnoResponseDto> actualAlumnoResponses = alumnosService.findAll("Nicolas", null);

        // Assert
        assertIterableEquals( expectedAlumnoResponses,actualAlumnoResponses);

        // Verify
        verify(alumnosRepository, times(1)).findByNombre("Nicolas");
        verify(alumnoMapper, times(1)).toResponseDtoList(anyList());
    }

    @Test
    void findByUuid() {
    }

    @Test
    void save() {
    }

    @Test
    void update() {
    }

    @Test
    void deleteById() {
    }
}