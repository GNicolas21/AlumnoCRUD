package es.nicolas.alumnocrud.services;

import es.nicolas.alumnocrud.models.Alumno;
import es.nicolas.alumnocrud.repositories.AlumnosRepository;
import es.nicolas.exceptions.AlumnoBadUuidException;
import es.nicolas.exceptions.AlumnoNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@CacheConfig(cacheNames = {"alumnos"})
@Slf4j
@Service
public class AlumnosServiceImpl implements AlumnosService{
    private final AlumnosRepository alumnosRepository;

    @Autowired
    public AlumnosServiceImpl(AlumnosRepository alumnosRepository) {
        this.alumnosRepository = alumnosRepository;
    }

    @Override
    public List<Alumno> findAll(String nombre, String apellido) {
        //Si está vacio o nulo, devolvemos todos los alumnos
        if ((nombre == null || nombre.isEmpty()) && (apellido == null || apellido.isEmpty())) {
            log.info("Buscando todos los alumnos");
            return alumnosRepository.findAll();
        }

        //Si el nombre no está vacío pero el apellido si, buscamos por nombre
        if((nombre != null && !nombre.isEmpty()) && (apellido == null || apellido.isEmpty())) {
            log.info("Buscando alumnos por nombre: {}", nombre);
            return alumnosRepository.findAllByNombre(nombre);
        }

        //Si el apellido no está vacío pero el nombre si, buscamos por apellido
        if ((nombre == null || nombre.isEmpty())) {
            log.info("Buscando alumnos por apellido: {}", apellido);
            return alumnosRepository.findAllByApellido(apellido);
        }

        //Si el nombre y apellido no están vacíos, buscamos por ambos
        log.info("Buscando alumnos por nombre: {}", nombre + " y apellido: " + apellido);
        return alumnosRepository.findAllByNombreAndApellido(nombre, apellido);
    }

    @Cacheable
    @Override
    public Alumno findById(Long id) {
        log.info("Buscando alumno por id: {}", id);
        return alumnosRepository.findById(id).orElseThrow(() -> new AlumnoNotFoundException(id));
    }

    @Cacheable
    @Override
    public Alumno findByUuid(String uuid) {
        log.info("Buscando alumno por uuid: {}", uuid);
        try {
            var myUUID = UUID.fromString(uuid);
            return alumnosRepository.findByUuid(myUUID).orElseThrow(() -> new AlumnoNotFoundException(myUUID));
        } catch (IllegalArgumentException e) {
            throw new AlumnoBadUuidException(uuid);
        }
    }

    @Override
    @CachePut
    public Alumno save(Alumno alumno) {
        log.info("Guardando alumno: {}", alumno);
        // obtenemos el id del alumno
        Long id = alumnosRepository.nextId();
        //Creamos un nuevo alumno con los datos que nos vienen
        Alumno newAlumno = new Alumno(
                id,
                alumno.getNombre(),
                alumno.getApellido(),
                alumno.getGrado(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                UUID.randomUUID()
                );

        // La guardamos en el repositorio
        return alumnosRepository.save(newAlumno);
    }

    @Override
    @CachePut
    public Alumno update(Long id, Alumno alumno) {
        log.info("Actualizando alumno con id: {}", id);
        var alumnoActual = this.findById(id);
        // Actualizamos los campos del alumno
        Alumno alumnoActualizado = new Alumno(
                alumnoActual.getId(),
                alumno.getNombre() != null ? alumno.getNombre() : alumnoActual.getNombre(),
                alumno.getApellido() != null ? alumno.getApellido() : alumnoActual.getApellido(),
                alumno.getGrado() != null ? alumno.getGrado() : alumnoActual.getGrado(),
                alumnoActual.getCreatedAt(),
                LocalDateTime.now(),
                alumnoActual.getUuid()
        );
        //Lo guardamos en el repositorio
        return alumnosRepository.save(alumnoActualizado);
    }

    @Override
    @CacheEvict
    public void deleteById(Long id) {
        log.info("Borrando alumno por id: {}", id);
        var alumnoEncontrado = this.findById(id);
        // Si lo encontramos, lo borramos
        if (alumnoEncontrado != null) {
            alumnosRepository.deleteById(id);
        }

    }

}
