package es.nicolas.alumnocrud.repositories;

import es.nicolas.alumnocrud.models.Alumno;

import java.util.List;
import java.util.Optional;

public interface AlumnosRepository {
    List <Alumno> findAll();

    List<Alumno> findAllByNombre(String nombre);

    List<Alumno> findAllByApellido(String apellido);

    Optional<Alumno> findById(Long id);

    List <Alumno> findAllByNombreAndApellido(String nombre, String apellido);


}
