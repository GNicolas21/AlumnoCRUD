package es.nicolas.rest.asignaturas.services;

import es.nicolas.rest.asignaturas.dto.AsignaturaRequestDto;
import es.nicolas.rest.asignaturas.models.Asignatura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AsignaturaService {
    Page<Asignatura> findAll(Optional<String> nombre, Optional<Boolean> isDeleted, Pageable pageable);

    Asignatura findById(Long id);

    Asignatura findByNombre(String nombre);

    Asignatura save(AsignaturaRequestDto asignaturaRequestDto);

    Asignatura update(Long id, AsignaturaRequestDto asignaturaRequestDto);

    void deleteById(Long id);

}
