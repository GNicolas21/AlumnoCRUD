package es.nicolas.alumnocrud.repositories;

import es.nicolas.alumnocrud.models.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AlumnosRepository extends JpaRepository<Alumno, Long> {

    List<Alumno> findByNombre(String nombre);

    List<Alumno> findByNombreAndIsDeletedFalse(String nombre);

    List<Alumno> findByApellidoContainsIgnoreCase(String apellido);

    List<Alumno> findByApellidoContainsIgnoreCaseAndIsDeletedFalse(String apellido);

    List<Alumno> findByNombreAndApellidoContainsIgnoreCase(String nombre, String apellido);

    List<Alumno> findByNombreAndApellidoContainsIgnoreCaseAndIsDeletedFalse(String nombre, String apellido);

    // Por UUID
    Optional<Alumno> findByUuid(UUID uuid);

    boolean existsByUuid(UUID uuid);
    void deleteByUuid(UUID uuid);

    // Si está borrado
    List <Alumno> findByIsDeleted(Boolean isDeleted);

    // Actualizar la tarjeta con isDeleted a true
    @Modifying // Para indicar que es una consulta de modificación
    @Query("UPDATE Alumno a SET a.isDeleted = true WHERE a.id =:id")
    void updateIsDeletedToTrueById(Long id);
}
