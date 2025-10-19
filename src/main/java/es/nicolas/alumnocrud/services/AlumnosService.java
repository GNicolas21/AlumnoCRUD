package es.nicolas.alumnocrud.services;

import es.nicolas.alumnocrud.models.Alumno;

import java.util.List;

public interface AlumnosService {

    List<Alumno> findAll(String nombre, String apellido);

    Alumno findById(Long id);

    Alumno findByUuid(String uuid);

    Alumno save(Alumno alumno);

    Alumno update(Long id, Alumno alumno);

    void deleteById(Long id);

}
