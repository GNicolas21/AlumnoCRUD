package es.nicolas.alumnocrud.repositories;

import es.nicolas.alumnocrud.models.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AlumnosRepository extends JpaRepository<Alumno, Long> {

    List<Alumno> findByNombre(String nombre);

    List<Alumno> findByApellidoContainsIgnoreCase(String apellido);

    List <Alumno> findByNombreAndApellidoContainsIgnoreCase(String nombre, String apellido);

    Optional<Alumno> findByUuid(UUID uuid);

    boolean existsByUuid(UUID uuid);

    void deleteByUuid(UUID uuid);

}
