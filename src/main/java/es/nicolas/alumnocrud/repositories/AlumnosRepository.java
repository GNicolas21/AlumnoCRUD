package es.nicolas.alumnocrud.repositories;

import es.nicolas.alumnocrud.models.Alumno;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AlumnosRepository {
    List <Alumno> findAll();

    List<Alumno> findAllByNombre(String nombre);

    List<Alumno> findAllByApellido(String apellido);

    List <Alumno> findAllByNombreAndApellido(String nombre, String apellido);

    Optional<Alumno> findById(Long id);

    Optional<Alumno> findByUuid(UUID uuid);

    boolean existsById(Long id);

    boolean existsByUUID(UUID uuid);

    Alumno save(Alumno alumno);

    void deleteById(Long id);

    void deleteByUuid(UUID uuid);

    Long nextId();

}
