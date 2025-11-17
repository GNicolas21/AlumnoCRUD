package es.nicolas.alumnos.controllers;

import es.nicolas.alumnos.dto.AlumnoCreateDto;
import es.nicolas.alumnos.dto.AlumnoResponseDto;
import es.nicolas.alumnos.dto.AlumnoUpdateDto;
import es.nicolas.alumnos.exceptions.AlumnoNotFoundException;
import es.nicolas.alumnos.services.AlumnosService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.http.MediaType;



import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class AlumnosRestControllerTest {
    private final String ENDPOINT = "/api/v1/alumnos";

    private final AlumnoResponseDto alumnoResponse1 = AlumnoResponseDto.builder()
            .id(1L)
            .nombre("Nicolas")
            .apellido("Osorio")
            .grado("2 DAW")
            .build();

    private final AlumnoResponseDto alumnoResponse2 = AlumnoResponseDto.builder()
            .id(2L)
            .nombre("Gabriel")
            .apellido("Bauti")
            .grado("3 DAW")
            .build();

    @Autowired
    private MockMvcTester mockMvcTester;

    @MockitoBean
    private AlumnosService alumnosService;

    @Test
    void getAll() {
        // Arrange
        var alumnoResponses = List.of(alumnoResponse1, alumnoResponse2);
        when(alumnosService.findAll(null, null)).thenReturn(alumnoResponses);

        // Act. Consultar el endpoint
        var result = mockMvcTester.get()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.length()").isEqualTo(alumnoResponses.size());
                    assertThat(json).extractingPath("$[0]")
                            .convertTo(AlumnoResponseDto.class).isEqualTo(alumnoResponse1);
                    assertThat(json).extractingPath("$[1]")
                            .convertTo(AlumnoResponseDto.class).isEqualTo(alumnoResponse2);
                });

        // Verify
        verify(alumnosService, times(1)).findAll(null, null);
    }

    @Test
    void getAllByNombre(){
        // Arrange
        var alumnoResponses = List.of(alumnoResponse2);
        String queryString = "?nombre=" + alumnoResponse2.getNombre();
        when(alumnosService.findAll(anyString(), isNull())).thenReturn(alumnoResponses);

        // Act. Consultar el endpoint
        var result = mockMvcTester.get()
                .uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.length()").isEqualTo(alumnoResponses.size());
                    assertThat(json).extractingPath("$[0]")
                            .convertTo(AlumnoResponseDto.class).isEqualTo(alumnoResponse2);
                });

        // Verify
        verify(alumnosService, times(1)).findAll(anyString(), isNull());
    }

    @Test
    void getAllByApellido(){
        // Arrange
        var alumnoResponses = List.of(alumnoResponse1);
        String queryString = "?apellido= " + alumnoResponse1.getApellido();
        when(alumnosService.findAll(isNull(), anyString())).thenReturn(alumnoResponses);

        // Act. Consultar el endpoint
        var result = mockMvcTester.get()
                .uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.length()").isEqualTo(alumnoResponses.size());
                    assertThat(json).extractingPath("$[0]")
                            .convertTo(AlumnoResponseDto.class).isEqualTo(alumnoResponse1);
                });

        // Verify
        verify(alumnosService, times(1)).findAll(isNull(), anyString());
    }

    @Test
    void getAllByNombreAndApellido(){
        // Arrange
        var alumnoResponses = List.of(alumnoResponse1);
        String queryString = "?nombre= " + alumnoResponse1.getNombre() + "&apellido=" + alumnoResponse1.getApellido();
        when(alumnosService.findAll(anyString(), anyString())).thenReturn(alumnoResponses);

        // Act. Consultar el endpoint
        var result = mockMvcTester.get()
                .uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.length()").isEqualTo(alumnoResponses.size());
                    assertThat(json).extractingPath("$[0]")
                            .convertTo(AlumnoResponseDto.class).isEqualTo(alumnoResponse1);
                });

        // Verify
        verify(alumnosService, times(1)).findAll(anyString(), anyString());
    }


    @Test
    void getById_shouldReturnJsonWithAlumno_whenValidIdProvided() {
        // Arrange
        Long alumnoId = alumnoResponse1.getId();
        when(alumnosService.findById(alumnoId)).thenReturn(alumnoResponse1);

        // Act
        var result = mockMvcTester.get()
                .uri(ENDPOINT + "/" + alumnoId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatusOk()
                .bodyJson()
                .convertTo(AlumnoResponseDto.class)
                .isEqualTo(alumnoResponse1);

        // Verify
        verify(alumnosService, only()).findById(anyLong());
    }

    @Test
    void getById_shouldThrowAlumnoNotFound_whenInvalidIdProvided(){
        // Arrange
        Long id = 3L;
        when(alumnosService.findById(id)).thenThrow(new AlumnoNotFoundException(id));

        // Act
        var result = mockMvcTester.get()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatus4xxClientError()
                // throws AlumnoNotFoundException
                .hasFailed().failure()
                .isInstanceOf(AlumnoNotFoundException.class)
                .hasMessageContaining("Alumno con id " + id + " no encontrado");

        // Verify
        verify(alumnosService, only()).findById(anyLong());
    }

    @Test
    void create() {
        // Arrange
        String requestBody = """
                {
                    "nombre": "Giorgio",
                    "apellido": "Bautista",
                    "grado": "3 DAW"
                }
                """;

        var alumnoSaved = AlumnoResponseDto.builder()
                .id(1L)
                .nombre("Giorgio")
                .apellido("Bautista")
                .grado("4 DAW")
                .build();

        when(alumnosService.save(any(AlumnoCreateDto.class))).thenReturn(alumnoSaved);

        // Act
        var result = mockMvcTester.post()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(AlumnoResponseDto.class)
                .isEqualTo(alumnoSaved);

        // Verify
        verify(alumnosService, only()).save(any(AlumnoCreateDto.class));
    }


    @Test
    void createWhenBadRequest() {
        // Arrange
        String requestBody = """
                {
                    "nombre": "Nicolas",
                    "apellido": "",
                    "grado": "DAW 2"
                }
                """;

        // Act
        var result = mockMvcTester.post()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                        .hasPathSatisfying("$.errors", path -> {
                            assertThat(path).hasFieldOrProperty("apellido");
                        });

        // Verify
        verify(alumnosService, never()).save(any(AlumnoCreateDto.class));
    }


    @Test
    void update() {
        // Arrange
        Long id = 1L;
        String requestBody = """
                {
                "grado": "4 DAW"
                }
                """;

        var alumnoSaved = AlumnoResponseDto.builder()
                .id(1L)
                .nombre("Nicolas")
                .apellido("Osorio")
                .grado("4 DAW")
                .build();

        when(alumnosService.update(anyLong(), any(AlumnoUpdateDto.class))).thenReturn(alumnoSaved);

        // Act
        var result = mockMvcTester.put()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatusOk()
                .bodyJson()
                .convertTo(AlumnoResponseDto.class)
                .isEqualTo(alumnoSaved);

        // Verify
        verify(alumnosService, only()).update(anyLong(), any(AlumnoUpdateDto.class));
    }

    @Test
    void update_shouldthrowAlumnoNotFound_whenInvalidIdProvided() {
        // Arrange
        Long id = 5L;
        String requestBody = """
                {
                "grado": "4 DAW"
                }
                """;

        when(alumnosService.update(anyLong(), any(AlumnoUpdateDto.class)))
                .thenThrow(new AlumnoNotFoundException(id));

        // Act
        var result = mockMvcTester.put()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatus(HttpStatus.NOT_FOUND)
                // throws AlumnoNotFoundException
                .hasFailed().failure()
                .isInstanceOf(AlumnoNotFoundException.class)
                .hasMessageContaining("Alumno con id " + id + " no encontrado.");

        // Verify
        verify(alumnosService, only()).update(anyLong(), any());
    }

    @Test
    void updatePartial() {
        // Arrange
        Long id = 1L;
        String requestBody = """
                {
                "grado": "4 DAW"
                }
                """;

        var alumnoSaved = AlumnoResponseDto.builder()
                .id(1L)
                .nombre("Nicolas")
                .apellido("Osorio")
                .grado("4 DAW")
                .build();

        when(alumnosService.update(anyLong(), any(AlumnoUpdateDto.class))).thenReturn(alumnoSaved);

        // Act
        var result = mockMvcTester.patch()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatusOk()
                .bodyJson()
                .convertTo(AlumnoResponseDto.class)
                .isEqualTo(alumnoSaved);

        // Verify
        verify(alumnosService, only()).update(anyLong(), any(AlumnoUpdateDto.class));
    }

    @Test
    void delete(){
        // Arrange
        Long id = 1L;
        doNothing().when(alumnosService).deleteById(anyLong());

        // Act
        var result = mockMvcTester.delete()
                .uri(ENDPOINT + "/" + id)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatus(HttpStatus.NO_CONTENT);

        // Verify
        verify(alumnosService, only()).deleteById(anyLong());
    }

    @Test
    void delete_shouldThrowAlumnoNotFound_whenInvalidIdProvided(){
        // Arrange
        Long id = 3L;
        doThrow(new AlumnoNotFoundException(id)).when(alumnosService).deleteById(anyLong());

        // Act
        var result = mockMvcTester.delete()
                .uri(ENDPOINT + "/" + id)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatus(HttpStatus.NOT_FOUND)
                // throws AlumnoNotFoundException
                .hasFailed().failure()
                .isInstanceOf(AlumnoNotFoundException.class)
                .hasMessageContaining("Alumno con id " + id + " no encontrado.");

        // Verify
        verify(alumnosService, only()).deleteById(anyLong());
    }
}