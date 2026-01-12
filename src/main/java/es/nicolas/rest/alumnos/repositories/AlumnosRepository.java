package es.nicolas.rest.alumnos.repositories;

import es.nicolas.rest.alumnos.models.Alumno;
import es.nicolas.rest.asignaturas.models.Asignatura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository // <-- No obligatorio
public interface AlumnosRepository extends JpaRepository<Alumno, Long>, JpaSpecificationExecutor<Alumno> {

//    Page<Alumno> findByNombre(String nombre, Pageable pageable);

//    Page<Alumno> findByApellidoContainsIgnoreCase(String apellido, Pageable pageable);

    //List<Alumno> findByApellidoContainsIgnoreCaseAndIsDeletedFalse(String apellido);

//    Page<Alumno> findByNombreAndApellidoContainsIgnoreCase(String nombre, String apellido, Pageable pageable);

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

    @Query("SELECT a FROM Alumno a WHERE a.asignatura.usuario.id = :usuarioId")
    Page<Alumno> findByUsuarioId(Long usuarioId, Pageable pageable);

    @Query("SELECT a FROM Alumno a WHERE a.asignatura.usuario.id = :usuarioId")
    List<Alumno> findByUsuarioId(Long usuarioId);

    // Obtiene si existe un alumno con el id del usuario
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Alumno a WHERE a.asignatura.usuario.id = :id")
    Boolean existsByUsuarioId(Long id);

    Object findByNombreEqualsIgnoreCase(String nombre);

    // Añadido para consulta GraphQL
    List<Alumno> findByAsignatura(Asignatura asignatura);
}
