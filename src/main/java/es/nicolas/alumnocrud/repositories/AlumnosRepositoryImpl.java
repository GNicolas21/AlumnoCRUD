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
        return alumnos.values().stream().toList();
    }

    @Override
    public List<Alumno> findByNombre(String nombre) {
        return alumnos.values().stream()
                .filter(al -> al.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .toList();
    }

    @Override
    public Optional<Alumno> findById(long id) {
        // condicion ? valor si true : valor si false
        return alumnos.get(id) != null ? Optional.of(alumnos.get(id)) : Optional.empty();
    }
}
