package es.nicolas.alumnos.controllers;


import es.nicolas.alumnos.dto.AlumnoCreateDto;
import es.nicolas.alumnos.dto.AlumnoResponseDto;
import es.nicolas.alumnos.dto.AlumnoUpdateDto;
import es.nicolas.alumnos.services.AlumnosService;
import es.nicolas.utils.pagination.PageResponse;
import es.nicolas.utils.pagination.PaginationLinksUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

/**
 * Controlador de productos del tipo RestController
 * Fijamos la ruta de acceso a este controlador
 * /*@RequiredArgsConstructor es una anotación Lombok que nos permite inyectar dependencias basadas
 * en las anotaciones @Controller, @Service, @Component, etc.
 * y que se encuentren en nuestro contenedor de Spring.
 */
@Tag(name = "Alumnos", description = "Endpoint de Alumnos de mi API")
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/${api.version}/alumnos")
@RestController
public class AlumnosRestController {

    private final AlumnosService alumnosService; //Uso de la @Req.ArgsConst.
    private final PaginationLinksUtils paginationLinksUtils; // Para las paginaciones

    //Obtener todos los alumnos o filtrar por nombre y/o apellido
    // Podemos activar CORS en SecurityConfig de manera centralizada
    // o por método de esta manera
    @Operation(summary = "Obtenemos todos los alumnos", description = "Obtiene una lista de alumnos")
    @Parameters({
            @Parameter(name = "nombre", description = "Nombre del alumno", example = ""),
            @Parameter(name = "apellido", description = "Apellido del alumno", example = ""),
            @Parameter(name = "isDeleted", description = "Si está borrado o no", example = "false"),
            @Parameter(name = "page", description = "Número de página", example = "0"),
            @Parameter(name = "size", description = "Tamaño de página", example = "10"),
            @Parameter(name = "sortBy", description = "Campo por el que ordenar", example = "id"),
            @Parameter(name = "direction", description = "Dirección de ordenación", example = "asc")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de alumnos"),
    })
    //@CrossOrigin(origins = "http://mifrontend.es")
    @GetMapping()
    public ResponseEntity<PageResponse<AlumnoResponseDto>> getAllAlumnos(@RequestParam(required = false) Optional<String> nombre,
                                                                         @RequestParam(required = false) Optional<String> apellido,
                                                                         @RequestParam(required = false) Optional<Boolean> isDeleted,
                                                                         @RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "10") int size,
                                                                         @RequestParam(defaultValue = "id") String sortBy,
                                                                         @RequestParam(defaultValue = "asc") String direction,
                                                                         HttpServletRequest request) {
        log.info("Buscando alumnos por nombre: {}, apellido: {}, isDeleted: {}", nombre, apellido, isDeleted);
        // Creamos el objeto de ordenacion Sort
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        // Creamos la vista de la paginacion
        Pageable pageable = PageRequest.of(page, size, sort);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(request.getRequestURL().toString());

        Page<AlumnoResponseDto> pageResult = alumnosService.findAll(nombre, apellido, isDeleted, pageable);
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
    }

    //Obtener un AlumnoResponseDto por su id, pasado como path variable el id
    //Si existe, lo devuelve, si no, lanza un 404 Not Found
    @Operation(summary = "Obtenemos un alumno por su id", description = "Obtiene un alumno por su id")
    @Parameters({
            @Parameter(name = "id", description = "Identificador de el alumno", example = "1", required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alumno encontrado"),
            @ApiResponse(responseCode = "404", description = "Alumno no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AlumnoResponseDto> getAlumnoById(@PathVariable Long id) {
        log.info("Buscando alumnos por id: {}", id);
        return ResponseEntity.ok(alumnosService.findById(id));
    }

    //Crear un nuevo alumnoCreateDto, pasado en el body como JSON, y devolver el alumno creado
    @Operation(summary = "Crea un nuevo alumno", description = "Crea un nuevo alumno con los datos proporcionados")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del alumno a crear", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Alumno creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos para crear el alumno")
    })
    @PostMapping()
    public ResponseEntity<AlumnoResponseDto> create(@Valid @RequestBody AlumnoCreateDto alumnoCreateDto) {
        log.info("Creando alumno: {}", alumnoCreateDto);
        var saved = alumnosService.save(alumnoCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Actualiza un alumno existente por su id.
     * @param id                El id del alumno a actualizar.
     * @param AlumnoUpdateDto con los datos actualizados.
     * @return ResponseEntity con el alumno actualizado.
     * @throws AlumnoNotFoundException   si no existe el alumno (404)
     * @throws AlumnoBadRequestException si los datos son inválidos (400)
     */
    @Operation(summary = "Actualiza un alumno existente", description = "Actualiza un alumno existente con los datos proporcionados")
    @Parameters({
            @Parameter(name = "id", description = "Identificador de el alumno", example = "1", required = true)
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del alumno a actualizar", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alumno actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos para actualizar el alumno"),
            @ApiResponse(responseCode = "404", description = "Alumno no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AlumnoResponseDto> update(@PathVariable Long id, @Valid @RequestBody AlumnoUpdateDto alumnoUpdateDto) {
        log.info("Actualizando alumno con id: {} con alumno: {}", id, alumnoUpdateDto);
        return ResponseEntity.ok(alumnosService.update(id, alumnoUpdateDto));
    }

    /**
     * Actualiza parcialmente un alumno existente por su id.
     * @param id                del alumno a actualizar.
     * @param /*alumnoUpadteDto con los datos a actualizar.
     * @return ResponseEntity con el alumno actualizado.
     * @throws AlumnoNotFoundException   si no existe el alumno (404)
     * @throws AlumnoBadRequestException si los datos son inválidos (400)
     */
    @Operation(summary = "Actualiza un alumno parcialmente", description = "Actualiza parcialmente un alumno existente con los datos proporcionados")
    @Parameters({
            @Parameter(name = "id", description = "Identificador de el alumno", example = "1", required = true)
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del alumno a actualizar", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alumno actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos para actualizar el alumno"),
            @ApiResponse(responseCode = "404", description = "Alumno no encontrado")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<AlumnoResponseDto> updatePartial(@PathVariable Long id, @Valid @RequestBody AlumnoUpdateDto alumnoUpdateDto) {
        log.info("Actualizando parcialmente alumno con id: {} con alumno: {}", id, alumnoUpdateDto);
        return ResponseEntity.ok(alumnosService.update(id, alumnoUpdateDto));
    }

    /**
     * Elimina un alumno existente por su id.
     * @param id del alumno a eliminar.
     * @return ResponseEntity (204) sin contenido.
     * @throws AlumnoNotFoundException si no existe el alumno (404)
     */
    @Operation(summary = "Elimina un alumno por su id", description = "Elimina un alumno existente por su id")
    @Parameters({
            @Parameter(name = "id", description = "Identificador de el alumno", example = "1", required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Alumno borrado"),
            @ApiResponse(responseCode = "404", description = "Alumno no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        log.info("Eliminando alumno con id: {}", id);
        alumnosService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Manejador de excepciones de Validacion: 400 Bad Request
     *
     * @param ex La excepción lanzada.
     * @return Mapa de errores de validación con el campo y el mensaje.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail =
                ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        BindingResult result = ex.getBindingResult();
        problemDetail.setDetail("Fallos la validación del objeto= '" +
                result.getObjectName() + "'." + " Núm. errores: " + result.getErrorCount());

        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach((error) -> {
            String errorMessage = error.getDefaultMessage();
            String fieldName = ((FieldError) error).getField();
            errors.put(fieldName, errorMessage);
        });
        problemDetail.setProperty("errors", errors);
        return problemDetail;
    }

}
