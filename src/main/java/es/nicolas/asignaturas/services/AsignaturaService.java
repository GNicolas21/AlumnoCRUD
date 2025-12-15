package es.nicolas.asignaturas.services;

import es.nicolas.asignaturas.dto.AsignaturaRequestDto;
import es.nicolas.asignaturas.models.Asignatura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface AsignaturaService {
    Page<Asignatura> findAll(Optional<String> nombre, Optional<Boolean> isDeleted, Pageable pageable);

    Asignatura findById(Long id);

    Asignatura findByNombre(String nombre);

    Asignatura save(AsignaturaRequestDto asignaturaRequestDto);

    Asignatura update(Long id, AsignaturaRequestDto asignaturaRequestDto);

    void deleteById(Long id);

}
