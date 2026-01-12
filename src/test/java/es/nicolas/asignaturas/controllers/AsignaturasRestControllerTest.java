package es.nicolas.asignaturas.controllers;

import es.nicolas.rest.asignaturas.dto.AsignaturaRequestDto;
import es.nicolas.rest.asignaturas.exceptions.AsignaturaConflictException;
import es.nicolas.rest.asignaturas.exceptions.AsignaturaNotFoundException;
import es.nicolas.rest.asignaturas.models.Asignatura;
import es.nicolas.rest.asignaturas.services.AsignaturaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class AsignaturasRestControllerTest {

    private final String ENDPOINT = "/api/v1/asignaturas";

    private final Asignatura asignatura1 = Asignatura.builder().id(1L).nombre("Programacion").build();
    private final Asignatura asignatura2 = Asignatura.builder().id(2L).nombre("Base de Datos").build();

    @Autowired
    private MockMvcTester mockMvcTester;

    @MockitoBean
    private AsignaturaService asignaturaService;

    @Test
    void getAll() {
        var asignaturas = List.of(asignatura1, asignatura2);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(asignaturas);

        when(asignaturaService.findAll(Optional.empty(), Optional.empty(),  pageable)).thenReturn(page);

        var result = mockMvcTester.get()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(result)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.content.length()").isEqualTo(asignaturas.size());
                    assertThat(json).extractingPath("$.content[0]")
                            .convertTo(Asignatura.class).usingRecursiveComparison().isEqualTo(asignatura1);
                    assertThat(json).extractingPath("$.content[1]")
                            .convertTo(Asignatura.class).usingRecursiveComparison().isEqualTo(asignatura2);
                });

        verify(asignaturaService, times(1))
                .findAll(Optional.empty(), Optional.empty(), pageable);
    }

    @Test
    void getAllByNombre() {
        var asignaturas = List.of(asignatura1);
        String queryString = "?nombre=" +  asignatura1.getNombre();

        Optional<String> nombre = Optional.of(asignatura1.getNombre());
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(asignaturas);
        when(asignaturaService.findAll(nombre, Optional.empty(), pageable)).thenReturn(page);

        var result = mockMvcTester.get()
                .uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(result)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.content.length()").isEqualTo(asignaturas.size());
                    assertThat(json).extractingPath("$.content[0]")
                            .convertTo(Asignatura.class).usingRecursiveComparison().isEqualTo(asignatura1);
                });
        verify(asignaturaService, times(1))
                .findAll(nombre, Optional.empty(), pageable);
    }

    @Test
    void getById() {
        Long id  = 1L;
        when(asignaturaService.findById(id)).thenReturn(asignatura1);

        var result = mockMvcTester.get()
                .uri(ENDPOINT+"/"+ id)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(result)
                .hasStatusOk()
                .bodyJson()
                .convertTo(Asignatura.class).usingRecursiveComparison().isEqualTo(asignatura1);
    }

    @Test
    void getById_ShouldThrowAsignaturaNotFound_WhenInvalidIdProvided() {
        Long id  = 3L;
        when(asignaturaService.findById(anyLong())).thenThrow(new AsignaturaNotFoundException(id));

        var result = mockMvcTester.get()
                .uri(ENDPOINT+"/"+ id)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(result)
                .hasStatus4xxClientError()
                .hasFailed().failure()
                .isInstanceOf(AsignaturaNotFoundException.class)
                .hasMessageContaining("no encontrada");

        verify(asignaturaService, only()).findById(anyLong());
    }


    @Test
    void create() {
        String requestBody = """
                {
                    "nombre" : " HTML"
                }
                """;

        var asignaturaSaved = Asignatura.builder().id(1L).nombre("Programacion").build();
        when(asignaturaService.save(any(AsignaturaRequestDto.class))).thenReturn(asignaturaSaved);

        var result = mockMvcTester.post()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        assertThat(result)
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(Asignatura.class)
                .usingRecursiveComparison()
                .isEqualTo(asignaturaSaved);

        verify(asignaturaService, only()).save(any(AsignaturaRequestDto.class));
    }

    @Test
    void create_whenBadRequest() {
        String requestBody = """
                {
                    "nombre" : null
                }
                """;

        var result = mockMvcTester.post()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        assertThat(result)
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .hasPathSatisfying("$.errores", path ->
                        assertThat(path).hasFieldOrProperty("nombre"));

        verify(asignaturaService, never()).save(any(AsignaturaRequestDto.class));
    }


    @Test
    void create_WhenNombreExists() {
        String requestBody = """
                {
                    "nombre" : "Programacion"
                }
                """;

        when(asignaturaService.save(any(AsignaturaRequestDto.class)))
                .thenThrow(new AsignaturaConflictException("Ya existe una asignatura con el nombre Programacion"));

        var result = mockMvcTester.post()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        assertThat(result)
                .hasStatus(HttpStatus.CONFLICT)
                .hasFailed().failure()
                .isInstanceOf(AsignaturaConflictException.class)
                .hasMessageContaining("Ya existe una asignatura");

    }


    @Test
    void update() {
        long id = 1L;
        String requestBody = """
                {
                    "nombre" : "PROGRAMACION"
                }
        """;

        var asignaturaSaved = Asignatura.builder().id(1L).nombre("PROGRAMACION").build();

        when(asignaturaService.update(anyLong(), any(AsignaturaRequestDto.class))).thenReturn(asignaturaSaved);

        var result = mockMvcTester.put()
                .uri(ENDPOINT+"/"+ id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        assertThat(result)
                .hasStatusOk()
                .bodyJson()
                .convertTo(Asignatura.class)
                .usingRecursiveComparison()
                .isEqualTo(asignaturaSaved);

        verify(asignaturaService, only()).update(anyLong(), any(AsignaturaRequestDto.class));
    }


    @Test
    void update_ShouldThrowAsignaturaNotFound() {
        Long id = 3L;
        String requestBody = """
                {
                    "nombre" : "Programacionnn"
                }
                """;
        when(asignaturaService.update(anyLong(), any(AsignaturaRequestDto.class))).thenThrow(new AsignaturaNotFoundException(id));

        var result =  mockMvcTester.put()
                .uri(ENDPOINT+"/"+ id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        assertThat(result)
                .hasStatus(HttpStatus.NOT_FOUND)
                .hasFailed().failure()
                .isInstanceOf(AsignaturaNotFoundException.class)
                .hasMessageContaining("no encontrada");

        verify(asignaturaService, only()).update(anyLong(), any());
    }


    @Test
    void update_ShoulThrowBadRequest() {
        long id = 3L;
        String requestBody = """
                {
                    "nombre" : ""
                    }
        """;

        var result = mockMvcTester.put()
                .uri(ENDPOINT+"/"+ id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        assertThat(result)
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .hasPathSatisfying("$.errores", path ->
                        assertThat(path).hasFieldOrProperty("nombre"));

        verify(asignaturaService, never()).update(anyLong(), any(AsignaturaRequestDto.class));
    }

    @Test
    void update_WhenNombreExists() {
        long id = 1L;
        String requestBody = """
                {
                    "nombre" : "Programacion"
                }   
        """;

        when(asignaturaService.update(anyLong(), any(AsignaturaRequestDto.class)))
                .thenThrow(new AsignaturaConflictException("Ya existe una asignatura con el nombre Programacion"));

        var result = mockMvcTester.put()
                .uri(ENDPOINT+"/"+ id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        assertThat(result)
                .hasStatus(HttpStatus.CONFLICT)
                .hasFailed().failure()
                .isInstanceOf(AsignaturaConflictException.class)
                .hasMessageContaining("Ya existe una asignatura");

        verify(asignaturaService, only()).update(anyLong(), any(AsignaturaRequestDto.class));
    }

    @Test
    void delete() {
        long id = 1L;
        doNothing().when(asignaturaService).deleteById(anyLong());

        var result = mockMvcTester.delete()
                .uri(ENDPOINT+"/"+ id)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(result)
            .hasStatus(HttpStatus.NO_CONTENT);

        verify(asignaturaService, only()).deleteById(anyLong());
    }

    @Test
    void delete_ShouldThrowAsignaturaNotFound() {
        long id = 1L;
        doThrow(new AsignaturaNotFoundException(id)).when(asignaturaService).deleteById(anyLong());

        var result = mockMvcTester.delete()
                .uri(ENDPOINT+"/"+ id)
                .exchange();

        assertThat(result)
                .hasStatus(HttpStatus.NOT_FOUND);

        verify(asignaturaService, only()).deleteById(anyLong());
    }
}