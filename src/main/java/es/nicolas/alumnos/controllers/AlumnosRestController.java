package es.nicolas.alumnos.controllers;


import es.nicolas.alumnos.dto.AlumnoCreateDto;
import es.nicolas.alumnos.dto.AlumnoResponseDto;
import es.nicolas.alumnos.dto.AlumnoUpdateDto;
import es.nicolas.alumnos.services.AlumnosService;
import es.nicolas.utils.pagination.PageResponse;
import es.nicolas.utils.pagination.PaginationLinksUtils;
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
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/${api.version}/alumnos")
@RestController
public class AlumnosRestController {

    private final AlumnosService alumnosService; //Uso de la @Req.ArgsConst.
    private final PaginationLinksUtils paginationLinksUtils; // Para las paginaciones

    //Obtener todos los alumnos o filtrar por nombre y/o apellido
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
    @GetMapping("/{id}")
    public ResponseEntity<AlumnoResponseDto> getAlumnoById(@PathVariable Long id) {
        log.info("Buscando alumnos por id: {}", id);
        return ResponseEntity.ok(alumnosService.findById(id));
    }

    //Crear un nuevo alumnoCreateDto, pasado en el body como JSON, y devolver el alumno creado
    @PostMapping()
    public ResponseEntity<AlumnoResponseDto> create(@Valid @RequestBody AlumnoCreateDto alumnoCreateDto) {
        log.info("Creando alumno: {}", alumnoCreateDto);
        var saved = alumnosService.save(alumnoCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Actualiza un alumno existente por su id.
     *
     * @param id                El id del alumno a actualizar.
     * @param *AlumnoUpdateDto con los datos actualizados.
     * @return ResponseEntity con el alumno actualizado.
     * @throws *AlumnoNotFoundException   si no existe el alumno (404)
     * @throws *AlumnoBadRequestException si los datos son inválidos (400)
     *
     */
    @PutMapping("/{id}")
    public ResponseEntity<AlumnoResponseDto> update(@PathVariable Long id, @Valid @RequestBody AlumnoUpdateDto alumnoUpdateDto) {
        log.info("Actualizando alumno con id: {} con alumno: {}", id, alumnoUpdateDto);
        return ResponseEntity.ok(alumnosService.update(id, alumnoUpdateDto));
    }

    /**
     * Actualiza parcialmente un alumno existente por su id.
     *
     * @param id                del alumno a actualizar.
     * @param /*alumnoUpadteDto con los datos a actualizar.
     * @return ResponseEntity con el alumno actualizado.
     * @throws /*AlumnoNotFoundException   si no existe el alumno (404)
     * @throws /*AlumnoBadRequestException si los datos son inválidos (400)
     *
     */
    @PatchMapping("/{id}")
    public ResponseEntity<AlumnoResponseDto> updatePartial(@PathVariable Long id, @Valid @RequestBody AlumnoUpdateDto alumnoUpdateDto) {
        log.info("Actualizando parcialmente alumno con id: {} con alumno: {}", id, alumnoUpdateDto);
        return ResponseEntity.ok(alumnosService.update(id, alumnoUpdateDto));
    }

    /**
     * Elimina un alumno existente por su id.
     *
     * @param id del alumno a eliminar.
     * @return ResponseEntity (204) sin contenido.
     * @throws /*AlumnoNotFoundException si no existe el alumno (404)
     *
     */
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
