package es.nicolas.alumnocrud.services;

import es.nicolas.alumnocrud.dto.AlumnoCreateDto;
import es.nicolas.alumnocrud.dto.AlumnoResponseDto;
import es.nicolas.alumnocrud.dto.AlumnoUpdateDto;
import es.nicolas.alumnocrud.exceptions.AlumnoNotFoundException;
import es.nicolas.alumnocrud.mappers.AlumnoMapper;
import es.nicolas.alumnocrud.models.Alumno;
import es.nicolas.alumnocrud.repositories.AlumnosRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Con esta anotación indicamos que usaremos Mockito(y JUnit5) en este test
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

    private AlumnoResponseDto alumnoResponse1;

    // Usamos un repositorio simulado (mock)
    @Mock
    private AlumnosRepository alumnosRepository;

    // Usamos el mapper real aunque en modo espía (spy) que nos permite simular algunas partes del mismo
    @Spy
    private AlumnoMapper alumnoMapper;

    // Es la clase que se testea y a la que se le inyectan los mocks y spies automaticamente
    @InjectMocks
    private AlumnosServiceImpl alumnosService;

    // Captor de argumentos
    // El captor es para que comprueba que los argumentos
    // que se pasan a un metodo son correctos
    @Captor
    private ArgumentCaptor<Alumno> alumnoCaptor;

    @BeforeEach
    void setUp() {
        alumnoResponse1 = alumnoMapper.toAlumnoResponseDto(alumno1);
    }

    @Test
    void findAll_ShouldReturnAllAlumnos_WhenNoParametersProvided() {
        // Arrange
        List <Alumno> expectedAlumnos = Arrays.asList(alumno1, alumno2);
        List<AlumnoResponseDto> expectedAlumnoResponses = alumnoMapper.toResponseDtoList(expectedAlumnos);
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
        // Verifica que el findAll del repositorio se haya llamado una sola vez
        verify(alumnosRepository, times(1)).findAll();
        verify(alumnoMapper, times(1)).toResponseDtoList(anyList());

    }

    @Test
    void findAll_ShouldReturnAlumnosByNombre_WhenNombreParameterProvided() {
        // Arrange
        String nombre = "Nicolas";
        List <Alumno> expectedAlumnos = List.of(alumno1);
        List<AlumnoResponseDto> expectedAlumnoResponses = alumnoMapper.toResponseDtoList(expectedAlumnos);

        when(alumnosRepository.findAllByNombre(nombre)).thenReturn(expectedAlumnos);
        when(alumnoMapper.toResponseDtoList(anyList())).thenReturn(expectedAlumnoResponses);

        // Act
        List <AlumnoResponseDto> actualAlumnoResponses = alumnosService.findAll(nombre, null);

        // Assert
        assertIterableEquals( expectedAlumnoResponses,actualAlumnoResponses);

        // Verify
        // Verifica que solo se ejecuta ese metodo
        verify(alumnosRepository, only()).findAllByNombre(nombre);
    }

    @Test
    void findAll_ShouldReturnAlumnosByApellido_WhenApellidoParameterProvided() {
        // Arrange
        String apellido = "Osorio";
        List <Alumno> expectedAlumnos = List.of(alumno1);
        List<AlumnoResponseDto> expectedAlumnoResponses = alumnoMapper.toResponseDtoList(expectedAlumnos);

        when(alumnosRepository.findAllByApellido(apellido)).thenReturn(expectedAlumnos);

        // Act
        List <AlumnoResponseDto> actualAlumnoResponses = alumnosService.findAll(null, apellido);

        // Assert
        assertIterableEquals( expectedAlumnoResponses,actualAlumnoResponses);

        // Verify
        verify(alumnosRepository, only()).findAllByApellido(apellido);
    }

    @Test
    void findAll_ShouldReturnAlumnosByNombreAndApellido_WhenBothParametersProvided() {
        // Arrange
        String nombre = "Nicolas";
        String apellido = "Osorio";
        List <Alumno> expectedAlumnos = List.of(alumno1);
        List<AlumnoResponseDto> expectedAlumnoResponses = alumnoMapper.toResponseDtoList(expectedAlumnos);

        when(alumnosRepository.findAllByNombreAndApellido(nombre, apellido)).thenReturn(expectedAlumnos);
        when(alumnoMapper.toResponseDtoList(anyList())).thenReturn(expectedAlumnoResponses);

        // Act
        List <AlumnoResponseDto> actualAlumnoResponses = alumnosService.findAll(nombre, apellido);

        // Assert
        assertIterableEquals( expectedAlumnoResponses,actualAlumnoResponses);

        // Verify
        verify(alumnosRepository, only()).findAllByNombreAndApellido(nombre, apellido);
    }

    @Test
    void findById_ShouldReturnAlumno_WhenValidIdProvided() {
        // Arrange
        Long id = 1L;

        AlumnoResponseDto expectedAlumnoResponse = alumnoResponse1;

        when(alumnosRepository.findById(id)).thenReturn(java.util.Optional.ofNullable(alumno1));

        // Act
        AlumnoResponseDto actualAlumnoResponse = alumnosService.findById(id);

        // Assert
        assertEquals(expectedAlumnoResponse, actualAlumnoResponse);

        // Verify
        verify(alumnosRepository, only()).findById(id);
    }

    @Test
    void findById_ShouldThrowAlumnoNotFound_WhenInvalidIdProvided() {
        // Arrange
        Long id = 1L;
        when(alumnosRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        var res = assertThrows(AlumnoNotFoundException.class, () -> alumnosService.findById(id));
        assertEquals("Alumno con id " + id + " no encontrado.", res.getMessage());

        // Verify
        verify(alumnosRepository.findById(id));
    }

    @Test
    void findByUuid_ShouldReturnAlumno_WhenValidUuidProvided() {
        // Arrange
        UUID expectedUuid = alumno1.getUuid();
        AlumnoResponseDto expectedAlumnoResponse = alumnoResponse1;

        when(alumnosRepository.findByUuid(expectedUuid)).thenReturn(Optional.of(alumno1));

        // Act
        AlumnoResponseDto actualAlumnoResponse = alumnosService.findByUuid(expectedUuid.toString());

        // Assert
        assertEquals(expectedAlumnoResponse, actualAlumnoResponse);

        // Verify
        verify(alumnosRepository, only()).findByUuid(expectedUuid);
    }

    // ME quedé aquí, el github service test 3
    @Test
    void findByUuid_ShouldThrowAlumnoBadUuid_WhenInvalidUuidProvided() {
        // Arrange
        String uuid = "00000000-0000-0000-0000-000000000000";

        // Act & Assert
        var res = assertThrows(AlumnoNotFoundException.class, () -> alumnosService.findByUuid(uuid));
        assertEquals("Alumno con uuid " + uuid + " no encontrado.", res.getMessage());

        // Verify
        verify(alumnosRepository, times(1)).findByUuid(UUID.fromString(uuid));
    }

    @Test
    void save_ShouldReturnSavedTarjeta_WhenValidTarjetaCreateDtoProvided() {
        // Arrange
        AlumnoCreateDto alumnoCreateDto = AlumnoCreateDto.builder()
                .nombre("Nicolas")
                .apellido("Osorio")
                .grado("2 DAW")
                .build();

        Alumno expectedAlumno = Alumno.builder()
                .id(1L)
                .nombre("Nicolas")
                .apellido("Osorio")
                .grado("2 DAW")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .uuid(UUID.randomUUID())
                .build();

        AlumnoResponseDto expectedAlumnoResponse = AlumnoResponseDto.builder()
                .id(1L)
                .nombre("Nicolas")
                .apellido("Osorio")
                .grado("2 DAW")
                .createdAt(expectedAlumno.getCreatedAt())
                .updatedAt(expectedAlumno.getUpdatedAt())
                .uuid(expectedAlumno.getUuid())
                .build();

        when(alumnosRepository.nextId()).thenReturn(1L);
        when(alumnoMapper.toAlumno(1L, alumnoCreateDto)).thenReturn(expectedAlumno);
        when(alumnosRepository.save(expectedAlumno)).thenReturn(expectedAlumno);
        when(alumnoMapper.toAlumnoResponseDto(any(Alumno.class))).thenReturn(expectedAlumnoResponse);

        // Act
        AlumnoResponseDto actualAlumnoResponse = alumnosService.save(alumnoCreateDto);

        // Assert
        assertEquals(expectedAlumnoResponse, actualAlumnoResponse);

        // Verify
        verify(alumnosRepository, times(1)).nextId();
        verify(alumnosRepository, times(1)).save(alumnoCaptor.capture());
        verify(alumnoMapper, times(1)).toAlumno(1L, alumnoCreateDto);
        verify(alumnoMapper, times(1)).toAlumnoResponseDto(any(Alumno.class));
    }


    @Test
    void update_ShouldReturnUpdateAlumno_WhenValidAndalumnoUpdateDtoProvided() {
        // Arrange
        Long id = 1L;
        AlumnoUpdateDto alumnoUpdateDto = AlumnoUpdateDto.builder()
                .nombre("Nicolas Updated")
                .build();

        Alumno existingAlumno = alumno1;

        AlumnoResponseDto existingAlumnoResponse = AlumnoResponseDto.builder()
                .id(1L)
                .nombre("Nicolas Updated")
                .apellido("Osorio")
                .grado("2 DAW")
                .createdAt(existingAlumno.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .uuid(existingAlumno.getUuid())
                .build();

        when(alumnosRepository.findById(id)).thenReturn(Optional.of(existingAlumno));
        when(alumnosRepository.save(existingAlumno)).thenReturn(existingAlumno);
        when(alumnoMapper.toAlumno(alumnoUpdateDto, alumno1)).thenReturn(existingAlumno);
        when(alumnoMapper.toAlumnoResponseDto(any(Alumno.class))).thenReturn(existingAlumnoResponse);

        // Act
        AlumnoResponseDto actualAlumnoResponse = alumnosService.update(id, alumnoUpdateDto);

        // Assert
        assertEquals(existingAlumnoResponse, actualAlumnoResponse);

        // Verify
        verify(alumnosRepository, times(1)).findById(id);
        verify(alumnosRepository, times(1)).save(alumnoCaptor.capture());
        verify(alumnoMapper, times(1)).toAlumno(alumnoUpdateDto, alumno1);
        verify(alumnoMapper, times(1)).toAlumnoResponseDto(any(Alumno.class));
    }

    @Test
    void update_ShouldThrowAlumnoNotFound_WhenInValidIdProvided(){
        // Arrange
        Long id = 1L;
        AlumnoUpdateDto alumnoUpdateDto = AlumnoUpdateDto.builder()
                .nombre("Nicolas Updated")
                .build();

        when(alumnosRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        var res = assertThrows(AlumnoNotFoundException.class, () -> alumnosService.update(id, alumnoUpdateDto));
        assertEquals("Alumno con id " + id + " no encontrado.", res.getMessage());

        // Verify
        verify(alumnosRepository, times(0)).save(any(Alumno.class));
    }

    @Test
    void deleteById_ShouldDeleteAlumno_WhenValidIdProvided() {
        // Arrange
        Long id = 1L;
        Alumno existingAlumno = alumno1;
        when(alumnosRepository.findById(id)).thenReturn(Optional.of(existingAlumno));

        // Act
        alumnosService.deleteById(id);

        // Verify
        verify(alumnosRepository, times(1)).findById(id);
    }

    @Test
    void deleteById_ShouldThrowAlumnoNotFound_WhenInValidIdProvided(){
        // Arrange
        Long id = 1L;
        when(alumnosRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        var res = assertThrows(AlumnoNotFoundException.class, () -> alumnosService.deleteById(id));
        assertEquals("Alumno con id " + id + " no encontrado.", res.getMessage());

        // Verify
        verify(alumnosRepository, times(0)).deleteById(id);
    }


}