package es.nicolas.alumnos.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.nicolas.alumnos.dto.AlumnoCreateDto;
import es.nicolas.alumnos.dto.AlumnoResponseDto;
import es.nicolas.alumnos.dto.AlumnoUpdateDto;
import es.nicolas.alumnos.exceptions.AlumnoBadUuidException;
import es.nicolas.alumnos.exceptions.AlumnoNotFoundException;
import es.nicolas.alumnos.mappers.AlumnoMapper;
import es.nicolas.alumnos.models.Alumno;
import es.nicolas.alumnos.repositories.AlumnosRepository;
import es.nicolas.asignaturas.models.Asignatura;
import es.nicolas.asignaturas.services.AsignaturaService;
import es.nicolas.config.websockets.WebSocketConfig;
import es.nicolas.config.websockets.WebSocketHandler;
import es.nicolas.websockets.notifications.mappers.AlumnoNotificationMapper;
import es.nicolas.websockets.notifications.models.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.*;
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
            .asignatura(Asignatura.builder()
                    .id(1L)
                    .nombre("Matematicas")
                    .build())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .uuid(UUID.fromString("267ed00a-6c21-4c4a-8626-db28bcca7a26"))
            .build();
    private final Alumno alumno2 = Alumno.builder()
            .id(2L)
            .nombre("Gabriel")
            .apellido("Bauti")
            .grado("3 DAW")
            .asignatura(Asignatura.builder()
                    .id(2L)
                    .nombre("Lengua")
                    .build())
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

    // Mock de AsignaturaService para inyectarlo en AlumnosServiceImpl
    @Mock
    private AsignaturaService asignaturaService;

    // Captor de argumentos
    // El captor es para que comprueba que los argumentos
    // que se pasan a un metodo son correctos
    @Captor
    private ArgumentCaptor<Alumno> alumnoCaptor;

    // Parte de WebSockets
    @Mock
    private WebSocketConfig webSocketConfig;

    @Mock
    private AlumnoNotificationMapper alumnoNotificationMapper;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private WebSocketHandler webSocketService;

    @BeforeEach
    void setUp() {
        alumnoResponse1 = alumnoMapper.toAlumnoResponseDto(alumno1);
        alumnosService.setWebSocketService(webSocketService);
    }

    @Test
    void findAll_ShouldReturnAllAlumnos_WhenNoParametersProvided() {
        // Arrange
        List <Alumno> expectedAlumnos = Arrays.asList(alumno1, alumno2);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Alumno> expectedPage = new PageImpl<>(expectedAlumnos);
        // el WHEN es para definir el comportamiento del mock
        // en este caso cuando se llame al metodo findAll del repositorio
        // devuelva la lista de alumnos esperada, osea aisla el sevice del repositorio
        when(alumnosRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(expectedPage);

        // Act
        Page <AlumnoResponseDto> actualPage =
                alumnosService.findAll(Optional.empty(), Optional.empty(), Optional.empty(), pageable);

        // Assert
        assertAll("findAll",
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertTrue(actualPage.getTotalPages() > 0)
        );

        // Verify
        // Verifica que el findAll del repositorio se haya llamado una sola vez
        verify(alumnosRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void findAll_ShouldReturnAlumnosByNombre_WhenNombreParameterProvided(){
        // Arrange
        Optional<String> nombre = Optional.of("Nicolas");
        List <Alumno> expectedAlumnos = List.of(alumno1);
        // Creamos el objeto Pageable
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Alumno> expectedPage = new PageImpl<>(expectedAlumnos);

        when(alumnosRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(expectedPage);

        // Act
        Page <AlumnoResponseDto> actualPage = alumnosService.findAll(nombre, Optional.empty(), Optional.empty(), pageable);

        // Assert
//        assertIterableEquals( expectedAlumnoResponses,actualAlumnoResponses);
        assertAll("findAll",
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertTrue(actualPage.getTotalPages() > 0)
        );

        // Verify
        // Verifica que solo se ejecuta ese metodo
        verify(alumnosRepository, only()).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void findAll_ShouldReturnAlumnosByApellido_WhenApellidoParameterProvided() {
        // Arrange
        Optional <String> apellido = Optional.of("Osorio");
        List <Alumno> expectedAlumnos = List.of(alumno1);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        Page<Alumno> expectedPage = new PageImpl<>(expectedAlumnos);
        when(alumnosRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);

        // Act
        Page<AlumnoResponseDto> actualPage =  alumnosService.findAll(Optional.empty(), apellido, Optional.empty(), pageable);

        // Assert
        assertAll("findAll",
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertTrue(actualPage.getTotalPages() > 0)
        );

        // Verify
        verify(alumnosRepository, only()).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void findAll_ShouldReturnAlumnosByNombreAndApellido_WhenBothParametersProvided() {
        // Arrange
        Optional<String> nombre = Optional.of("Nicolas");
        Optional<String> apellido = Optional.of("Osorio");
        List <Alumno> expectedAlumnos = List.of(alumno1);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        Page<Alumno> expectedPage = new PageImpl<>(expectedAlumnos);

        when(alumnosRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(expectedPage);

        // Act
        Page <AlumnoResponseDto> actualPage = alumnosService.findAll(nombre, apellido, Optional.empty(), pageable);

        // Assert
        assertAll("findAll",
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertTrue(actualPage.getTotalPages() > 0)
        );

        // Verify
        verify(alumnosRepository, only()).findAll(any(Specification.class), any(Pageable.class));
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
        verify(alumnosRepository).findById(id);
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
        String uuid = "1234";

        // Act & Assert
        var res = assertThrows(AlumnoBadUuidException.class, () -> alumnosService.findByUuid(uuid));
        assertEquals("Alumno con uuid " + uuid + " no encontrado.", res.getMessage());

        // Verify
        verify(alumnosRepository, never()).findByUuid(any());
    }

    @Test
    void save_ShouldReturnSavedTarjeta_WhenValidAlumnoCreateDtoProvided() throws IOException {
        // Arrange
        AlumnoCreateDto alumnoCreateDto = AlumnoCreateDto.builder()
                .nombre("Nicolas")
                .apellido("Osorio")
                .grado("2 DAW")
                .asignatura("Matematicas")
                .build();

        Alumno expectedAlumno = Alumno.builder()
                .id(1L)
                .nombre("Nicolas")
                .apellido("Osorio")
                .grado("2 DAW")
                .asignatura(Asignatura.builder()
                        .id(1L)
                        .nombre("Matematicas")
                        .build())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .uuid(UUID.randomUUID())
                .build();

        Asignatura asignaturaEncontrada = Asignatura.builder()
                .id(1L)
                .nombre("Matematicas")
                .build();

        Alumno alumnoMapeado = Alumno.builder()
                .nombre("Nicolas")
                .apellido("Osorio")
                .grado("2 DAW")
                .asignatura(asignaturaEncontrada)
                .build();

        when(asignaturaService.findByNombre("Matematicas")).thenReturn(asignaturaEncontrada);

        // Convertimos el alumno esperado a AlumnoResponseDto))
        AlumnoResponseDto expectedAlumnoResponse = alumnoMapper.toAlumnoResponseDto(expectedAlumno);

        when(alumnoMapper.toAlumno(alumnoCreateDto, asignaturaEncontrada)).thenReturn(alumnoMapeado);
        when(alumnosRepository.save(any(Alumno.class))).thenReturn(expectedAlumno);
        when(alumnoMapper.toAlumnoResponseDto(expectedAlumno)).thenReturn(expectedAlumnoResponse);
        doNothing().when(webSocketService).sendMessage(any());

        // Act
        AlumnoResponseDto actualAlumnoResponse = alumnosService.save(alumnoCreateDto);

        // Assert
        assertEquals(expectedAlumnoResponse, actualAlumnoResponse);

        // Verify
            verify(alumnosRepository).save(alumnoCaptor.capture());

        Alumno alumnoCaptured = alumnoCaptor.getValue();
        assertEquals(expectedAlumno.getNombre(), alumnoCaptured.getNombre());
    }


    @Test
    void update_ShouldReturnUpdateAlumno_WhenValidAndalumnoUpdateDtoProvided() throws IOException {
        // Arrange
        Long id = 1L;
        String nombre = "Nicolas Updated";
        when(alumnosRepository.findById(id)).thenReturn(Optional.of(alumno1));

        AlumnoUpdateDto alumnoUpdateDto = AlumnoUpdateDto.builder()
                .nombre(nombre)
                .build();

        Alumno alumnoUpdate = alumnoMapper.toAlumno(alumnoUpdateDto, alumno1);
        when(alumnosRepository.save(any(Alumno.class))).thenReturn(alumnoUpdate);

        alumnoResponse1.setNombre(nombre);
        AlumnoResponseDto existingAlumnoResponse = alumnoResponse1;
        doNothing().when(webSocketService).sendMessage(any());

        // Act
        AlumnoResponseDto actualAlumnoResponse = alumnosService.update(id, alumnoUpdateDto);

        // Assert este da error
        // assertEquals(existingAlumnoResponse, actualAlumnoResponse);
        assertThat(actualAlumnoResponse)
                .usingRecursiveComparison()
                .ignoringFields("updatedAt")
                .isEqualTo(existingAlumnoResponse);

        // Verify
        verify(alumnosRepository).findById(id);
        verify(alumnosRepository).save(any());
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
//        var res = assertThrows(AlumnoNotFoundException.class, () -> alumnosService.update(id, alumnoUpdateDto));
//        assertEquals("Alumno con id " + id + " no encontrado.", res.getMessage());

        // Con AssertJ
        assertThatThrownBy(
                () -> alumnosService.update(id, alumnoUpdateDto))
                .isInstanceOf(AlumnoNotFoundException.class)
                .hasMessage("Alumno con id " + id + " no encontrado."
        );

        // Verify
        verify(alumnosRepository).findById(id);
        verify(alumnosRepository, never()).save(any());
    }

    @Test
    void deleteById_ShouldDeleteAlumno_WhenValidIdProvided() throws  IOException{
        // Arrange
        Long id = 1L;
        lenient().when(alumnosRepository.findById(id)).thenReturn(Optional.of(alumno1));
        doNothing().when(webSocketService).sendMessage(any());

        // AssertJ
        assertThatCode(() -> alumnosService.deleteById(id))
                .doesNotThrowAnyException();

        // Assert
        verify(alumnosRepository, times(1)).deleteById(id);
        verify(alumnosRepository).findById(id);
    }

    @Test
    void deleteById_ShouldThrowAlumnoNotFound_WhenInValidIdProvided(){
        // Arrange
        Long id = 1L;
        when(alumnosRepository.findById(id)).thenReturn(Optional.empty());

        // AssertJ
        assertThatThrownBy(
                () -> alumnosService.deleteById(id))
                .isInstanceOf(AlumnoNotFoundException.class)
                .hasMessage("Alumno con id " + id + " no encontrado.");

        // Verify
        verify(alumnosRepository, times(0)).deleteById(id);
    }

    @Test
    void onChange_ShouldSendMessage_WhenValidDataProvided() throws IOException{
        // Arrange
        doNothing().when(webSocketService).sendMessage(any());

        // Act
        alumnosService.onChange(Notification.Tipo.CREATE, alumno1);
    }
}