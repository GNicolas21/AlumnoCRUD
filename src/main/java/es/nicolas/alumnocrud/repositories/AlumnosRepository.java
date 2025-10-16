package es.nicolas.alumnocrud.repositories;

import es.nicolas.alumnocrud.models.Alumno;

import java.util.List;
import java.util.Optional;

public interface AlumnosRepository {
    List <Alumno> findAll();

    List<Alumno> findByNombre(String nombre);

    Optional<Alumno> findById(long id);
}
