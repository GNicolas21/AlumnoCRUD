package es.nicolas.alumnocrud.repositories;

import es.nicolas.alumnocrud.models.Alumno;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.*;

@Slf4j
@Repository
public class AlumnosRepositoryImpl implements AlumnosRepository {

    private final Map<Long, Alumno> alumnos = new LinkedHashMap<>(
            Map.of(
                    1L, new Alumno(1L, "Nicolas", "Osorio", "2 DAW", LocalDateTime.now(), LocalDateTime.now(), UUID.randomUUID()),
                    2L, new Alumno(2L, "Bart", "Benavente", "8 DAM", LocalDateTime.now(), LocalDateTime.now(), UUID.randomUUID()),
                    3L, new Alumno(3L, "Cesar", "Campos", "3 DAW", LocalDateTime.now(), LocalDateTime.now(), UUID.randomUUID()),
                    4L, new Alumno(4L, "Dani", "Delgado", "4 ASIR", LocalDateTime.now(), LocalDateTime.now(), UUID.randomUUID())
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
}
