package es.nicolas.asignaturas.services;

import es.nicolas.asignaturas.dto.AsignaturaRequestDto;
import es.nicolas.asignaturas.models.Asignatura;

import java.util.List;

public interface AsignaturaService {
    List<Asignatura> findAll(String nombre);

    Asignatura findById(Long id);

    Asignatura findByNombre(String nombre);

    Asignatura save(AsignaturaRequestDto asignaturaRequestDto);

    Asignatura update(Long id, AsignaturaRequestDto asignaturaRequestDto);

    void deleteById(Long id);

}
