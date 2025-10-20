package es.nicolas.alumnocrud.controllers;


import es.nicolas.alumnocrud.dto.AlumnoCreateDto;
import es.nicolas.alumnocrud.dto.AlumnoResponseDto;
import es.nicolas.alumnocrud.dto.AlumnoUpdateDto;
import es.nicolas.alumnocrud.services.AlumnosService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Controlador de productos del tipo RestController
 * Fijamos la ruta de acceso a este controlador
 * Usamos el repositorio de productos y lo inyectamos en el constructor con Autowired
 * /*@Autowired es una anotación que nos permite inyectar dependencias basadas
 * en las anotaciones @Controller, @Service, @Component, etc.
 * y que se encuentren en nuestro contenedor de Spring.
 */
@Slf4j
@RequestMapping("api/${api.version}/alumnos")
@RestController
public class AlumnosRestController {

    private final AlumnosService alumnosService;

    @Autowired
    public AlumnosRestController(AlumnosService alumnosService) {
        this.alumnosService = alumnosService;
    }

    //Obtener todos los alumno o filtrar por nombre y/o apellido
    @GetMapping()
    public ResponseEntity<List<AlumnoResponseDto>> getAllAlumnos(@RequestParam(required = false) String nombre,
                                                      @RequestParam(required = false) String apellido){
        log.info("Buscando alumnos por nombre: {} y apellido: {}", nombre, apellido);
        return ResponseEntity.ok(alumnosService.findAll(nombre, apellido));
    }

    //Obtener un AlumnoResponseDto por su id, pasado como path variable el id
    //Si existe, lo devuelve, si no, lanza un 404 Not Found
    @GetMapping("/{id}")
    public ResponseEntity<AlumnoResponseDto> getAlumnoById(@PathVariable Long id){
        log.info("Buscando alumnos por id: {}", id);
        return ResponseEntity.ok(alumnosService.findById(id));
    }

    //Crear un nuevo alumnoCreateDto, pasado en el body como JSON, y devolver el alumno creado
    @PostMapping()
    public ResponseEntity<AlumnoResponseDto> create(@Valid @RequestBody AlumnoCreateDto alumnoCreateDto){
        log.info("Creando alumno: {}", alumnoCreateDto);
        var saved = alumnosService.save(alumnoCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Actualiza un alumno existente por su id.
     * @param id El id del alumno a actualizar.
     * @param /*AlumnoUpdateDto con los datos actualizados.
     * @return ResponseEntity con el alumno actualizado.
     * @throws /*AlumnoNotFoundException si no existe el alumno (404)
     * @throws /*AlumnoBadRequestException si los datos son inválidos (400)
     * */
    @PutMapping("/{id}")
    public ResponseEntity<AlumnoResponseDto> update(@PathVariable Long id,@Valid @RequestBody AlumnoUpdateDto alumnoUpdateDto) {
        log.info("Actualizando alumno con id: {} con alumno: {}", id, alumnoUpdateDto);
        return ResponseEntity.ok(alumnosService.update(id, alumnoUpdateDto));
    }

    /**
     * Actualiza parcialmente un alumno existente por su id.
     * @param id del alumno a actualizar.
     * @param /*alumnoUpadteDto con los datos a actualizar.
     * @return ResponseEntity con el alumno actualizado.
     * @throws /*AlumnoNotFoundException si no existe el alumno (404)
     * @throws /*AlumnoBadRequestException si los datos son inválidos (400)
     * */
    @PatchMapping("/{id}")
    public ResponseEntity<AlumnoResponseDto> updatePartial(@PathVariable Long id,@Valid @RequestBody AlumnoUpdateDto alumnoUpdateDto){
        log.info("Actualizando parcialmente alumno con id: {} con alumno: {}", id, alumnoUpdateDto);
        return ResponseEntity.ok(alumnosService.update(id, alumnoUpdateDto));
    }

    /**
     * Elimina un alumno existente por su id.
     * @param id del alumno a eliminar.
     * @return ResponseEntity (204) sin contenido.
     * @throws /*AlumnoNotFoundException si no existe el alumno (404)
     * */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id){
        log.info("Eliminando alumno con id: {}", id);
        alumnosService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Manejador de excepciones de Validacion: 400 Bad Request
     * @param ex La excepción lanzada.
     * @return Mapa de errores de validación con el campo y el mensaje.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

}
