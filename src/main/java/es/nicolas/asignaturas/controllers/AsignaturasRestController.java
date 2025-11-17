package es.nicolas.asignaturas.controllers;

import es.nicolas.asignaturas.dto.AsignaturaRequestDto;
import es.nicolas.asignaturas.models.Asignatura;
import es.nicolas.asignaturas.services.AsignaturaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/${api.version}/asignaturas")
public class AsignaturasRestController {
    private final AsignaturaService asignaturaService;

    @GetMapping
    public ResponseEntity<List<Asignatura>> getAll(@RequestParam(required = false) String nombre){
        log.info("Buscando asignaturas por nombre: {}", nombre);
        return ResponseEntity.ok(asignaturaService.findAll(nombre));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Asignatura> getById(@PathVariable Long id){
        log.info("Buscando asignatura por id: {}", id);
        return ResponseEntity.ok(asignaturaService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Asignatura> create(@RequestBody AsignaturaRequestDto asignaturaRequestDto){
        log.info("Creando asignatura: {}", asignaturaRequestDto);
        Asignatura nuevaAsignatura = asignaturaService.save(asignaturaRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaAsignatura);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Asignatura> update(@PathVariable Long id, @RequestBody AsignaturaRequestDto asignaturaRequestDto) {
        log.info("Actualizando asignatura con id: {}", id);
        Asignatura updatedAsignatura = asignaturaService.update(id, asignaturaRequestDto);
        return ResponseEntity.ok(updatedAsignatura);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        log.info("Borrando asignatura con id: {}", id);
        asignaturaService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        BindingResult result = ex.getBindingResult();
        problemDetail.setDetail("Falló la validación para el objeto='" + result.getObjectName()
                + "'. " + "Núm. errores: " + result.getErrorCount());

        Map<String, String> errores = new HashMap<>();
        result.getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errores.put(fieldName, errorMessage);
        });

        problemDetail.setProperty("errores", errores);
        return problemDetail;
    }
}
