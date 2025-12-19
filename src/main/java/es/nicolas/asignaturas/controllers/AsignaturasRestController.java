package es.nicolas.asignaturas.controllers;

import es.nicolas.asignaturas.dto.AsignaturaRequestDto;
import es.nicolas.asignaturas.models.Asignatura;
import es.nicolas.asignaturas.services.AsignaturaService;
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

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/${api.version}/asignaturas")
public class AsignaturasRestController {
    private final AsignaturaService asignaturaService;
    private final PaginationLinksUtils paginationLinksUtils;

    @GetMapping
    public ResponseEntity<PageResponse<Asignatura>> getAll(
            @RequestParam(required = false) Optional<String> nombre,
            @RequestParam(required = false) Optional<Boolean> isDeleted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc")  String direction,
            HttpServletRequest request
    ){
        log.info("Buscando asignaturas por nombre: {}, isDeleted: {}", nombre, isDeleted);
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(request.getRequestURL().toString());
        Page <Asignatura> pageResult = asignaturaService.findAll(nombre, isDeleted, pageable);
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Asignatura> getById(@PathVariable Long id){
        log.info("Buscando asignatura por id: {}", id);
        return ResponseEntity.ok(asignaturaService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Asignatura> create(@Valid @RequestBody AsignaturaRequestDto asignaturaRequestDto){
        log.info("Creando asignatura: {}", asignaturaRequestDto);
        Asignatura nuevaAsignatura = asignaturaService.save(asignaturaRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaAsignatura);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Asignatura> update(@PathVariable Long id,@Valid @RequestBody AsignaturaRequestDto asignaturaRequestDto) {
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
