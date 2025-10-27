package es.nicolas.alumnocrud.repositories;

import es.nicolas.alumnocrud.models.Alumno;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Repository
public class AlumnosRepositoryImpl implements AlumnosRepository {

    private final Map<Long, Alumno> alumnos = new LinkedHashMap<>(
            Map.of(
                    1L, Alumno.builder()
                            .id(1L)
                            .nombre("Nicolas")
                            .apellido("Osorio")
                            .grado("2 DAW")
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .uuid(UUID.randomUUID())
                            .build(),
                    2L, Alumno.builder()
                            .id(2L)
                            .nombre("Bart")
                            .apellido("Benavente")
                            .grado("8 DAM")
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .uuid(UUID.randomUUID())
                            .build(),
                    3L, Alumno.builder()
                            .id(3L)
                            .nombre("Cesar")
                            .apellido("Campos")
                            .grado("3 DAW")
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .uuid(UUID.randomUUID())
                            .build(),
                    4L, Alumno.builder()
                            .id(4L)
                            .nombre("Dani")
                            .apellido("Delgado")
                            .grado("4 ASIR")
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .uuid(UUID.randomUUID())
                            .build()
            )
    );




    @Override
    public List<Alumno> findAll() {
        log.info("Buscando alumnos");
        return alumnos.values().stream().toList();
    }

    @Override
    public List<Alumno> findAllByApellido(String apellido) {
        log.info("Buscando alumnos por apellido: {}", apellido);
        return alumnos.values().stream()
                .filter(al -> al.getApellido().toLowerCase().contains(apellido.toLowerCase()))
                .toList();
    }

    @Override
    public List<Alumno> findAllByNombre(String nombre) {
        log.info("Buscando alumnos por nombre: {}", nombre);
        return alumnos.values().stream()
                .filter(al -> al.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .toList();
    }

    @Override
    public Optional<Alumno> findById(Long id) {
        log.info("Buscando alumnos por id: {}", id);
        // condicion ? valor si true : valor si false
        return alumnos.get(id) != null ? Optional.of(alumnos.get(id)) : Optional.empty();
    }

    @Override
    public List<Alumno> findAllByNombreAndApellido(String nombre, String apellido) {
        log.info("Buscando alumnos por nombre: {} y apellido: {}", nombre, apellido);
        return alumnos.values().stream()
                .filter(al -> al.getNombre().toLowerCase().contains(nombre.toLowerCase()) &&
                        al.getApellido().toLowerCase().contains(apellido.toLowerCase()))
                .toList();
    }


    @Override
    public Optional<Alumno> findByUuid(UUID uuid) {
        log.info("Buscando alumno por uuid: {}", uuid);
        return alumnos.values().stream()
                .filter(al -> al.getUuid().equals(uuid))
                .findFirst();
    }

    @Override
    public boolean existsById(Long id) {
        log.info("Comprobando si existe el alumno por id: {}", id);
        return alumnos.get(id) != null;
    }

    @Override
    public boolean existsByUUID(UUID uuid) {
        log.info("Comprobando si existe el alumno por uuid: {}", uuid);
        return alumnos.values().stream()
                .anyMatch(al -> al.getUuid().equals(uuid));
    }

    @Override
    public Alumno save(Alumno alumno) {
        log.info("Guardando alumno: {}", alumno);
        alumnos.put(alumno.getId(), alumno);
        return alumno;
    }

    @Override
    public void deleteById(Long id) {
        log.info("Eliminando alumno por id: {}", id);
        alumnos.remove(id);
    }

    @Override
    public void deleteByUuid(UUID uuid) {
        log.info("Eliminando alumno por uuid: {}", uuid);
        alumnos.values().removeIf(al -> al.getUuid().equals(uuid));
    }

    @Override
    public Long nextId() {
        log.info("Obteniendo el siguiente id de alumno");
        return alumnos.keySet().stream()
                .mapToLong(v -> v)
                .max()
                .orElse(0L) + 1;
    }
}
