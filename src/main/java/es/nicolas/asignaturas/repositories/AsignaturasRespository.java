package es.nicolas.asignaturas.repositories;

import es.nicolas.asignaturas.models.Asignatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.*;

public interface AsignaturasRespository extends JpaRepository<Asignatura, Long> {

    // Encontrar por nombre
    Optional<Asignatura> findByNombreEqualsIgnoreCase(String nombre);

    // Encontrar por nombre y isDeleted false
    Optional<Asignatura> findByNombreIgnoreCaseAndIsDeletedFalse(String nombre);

    // Lista de asignaturas por nombre
    List<Asignatura> findByNombreContainsIgnoreCase(String nombre);

    // Asignaturas no borradas
    List<Asignatura> findByIsDeleted(Boolean isDeleted);

    // Actualizar la asignatura con isDeleted a true
    // Usar @Modifying y @Query en el servicio
    @Modifying
    @Query("update Asignatura a set a.isDeleted=true where a.id =:id")
    void updateIsDeletedToTrueById(Long id);

    // obtiene si existe una asignatura con id del alumno
    @Query("SELECT CASE WHEN COUNT(al) > 0 then true else false end from Alumno al where al.asignatura.id =:id")
    Boolean existsByAlumnoById(Long id);

}
