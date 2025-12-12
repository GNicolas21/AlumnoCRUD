package es.nicolas.alumnos.repositories;

import es.nicolas.alumnos.models.Alumno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository // <-- No obligatorio
public interface AlumnosRepository extends JpaRepository<Alumno, Long> {

    Page<Alumno> findByNombre(String nombre, Pageable pageable);

    Page<Alumno> findByApellidoContainsIgnoreCase(String apellido, Pageable pageable);

    //List<Alumno> findByApellidoContainsIgnoreCaseAndIsDeletedFalse(String apellido);

    Page<Alumno> findByNombreAndApellidoContainsIgnoreCase(String nombre, String apellido, Pageable pageable);

    //List<Alumno> findByNombreAndApellidoContainsIgnoreCaseAndIsDeletedFalse(String nombre, String apellido);

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
