package es.nicolas.alumnos.services;

import es.nicolas.alumnos.dto.AlumnoCreateDto;
import es.nicolas.alumnos.dto.AlumnoResponseDto;
import es.nicolas.alumnos.dto.AlumnoUpdateDto;
import es.nicolas.alumnos.mappers.AlumnoMapper;
import es.nicolas.alumnos.models.Alumno;
import es.nicolas.alumnos.repositories.AlumnosRepository;
import es.nicolas.alumnos.exceptions.AlumnoBadUuidException;
import es.nicolas.alumnos.exceptions.AlumnoNotFoundException;
import es.nicolas.asignaturas.services.AsignaturaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@CacheConfig(cacheNames = {"alumnos"})
@Slf4j
@Service
public class AlumnosServiceImpl implements AlumnosService{
    private final AlumnosRepository alumnosRepository;
    private final AlumnoMapper alumnoMapper;
    private final AsignaturaService asignaturaService;

    @Override
    public List<AlumnoResponseDto> findAll(String nombre, String apellido) {
        //Si todos los args están vacios o nulos, devolvemos todos los alumnos
        if ((nombre == null || nombre.isEmpty()) && (apellido == null || apellido.isEmpty())) {
            log.info("Buscando todos los alumnos");
            return alumnoMapper.toResponseDtoList(alumnosRepository.findAll());
        }

        //Si el nombre no está vacío pero el apellido si, buscamos por nombre
        if((nombre != null && !nombre.isEmpty()) && (apellido == null || apellido.isEmpty())) {
            log.info("Buscando alumnos por nombre: {}", nombre);
            return alumnoMapper.toResponseDtoList(alumnosRepository.findByNombre(nombre));
        }

        //Si el apellido no está vacío pero el nombre si, buscamos por apellido
        if ((nombre == null || nombre.isEmpty())) {
            log.info("Buscando alumnos por apellido: {}", apellido);
            return alumnoMapper.toResponseDtoList(alumnosRepository.findByApellidoContainsIgnoreCase(apellido));
        }

        //Si el nombre y apellido no están vacíos, buscamos por ambos
        log.info("Buscando alumnos por nombre: {}", nombre + " y apellido: " + apellido);
        return alumnoMapper.toResponseDtoList(alumnosRepository.findByNombreAndApellidoContainsIgnoreCase(nombre, apellido));
    }

    // Cachea con el id como key
    @Cacheable(key = "#id")
    @Override
    public AlumnoResponseDto findById(Long id) {
        log.info("Buscando alumno por id: {}", id);
        return alumnoMapper.toAlumnoResponseDto(alumnosRepository.findById(id)
                .orElseThrow(() -> new AlumnoNotFoundException(id)));
    }

    // Cachea con el uuid como key
    @Cacheable(key = "#uuid")
    @Override
    public AlumnoResponseDto findByUuid(String uuid) {
        log.info("Buscando alumno por uuid: {}", uuid);
        try {
            var myUUID = UUID.fromString(uuid);
            return alumnoMapper.toAlumnoResponseDto(alumnosRepository.findByUuid(myUUID)
                    .orElseThrow(() -> new AlumnoNotFoundException(myUUID)));
        } catch (IllegalArgumentException e) {
            throw new AlumnoBadUuidException(uuid);
        }
    }

    // Cachea con el id del resultado de la operacion como key
    @CachePut(key = "#result.id")
    @Override
    public AlumnoResponseDto save(AlumnoCreateDto alumnoCreateDto) {
        log.info("Guardando alumno: {}", alumnoCreateDto);
        //Creamos un nuevo alumno con los datos que nos vienen
        var asignatura = asignaturaService.findByNombre(alumnoCreateDto.getNombre());
        Alumno nuevoAlumno = alumnoMapper.toAlumno(alumnoCreateDto, asignatura);
        // La guardamos en el repositorio
        return alumnoMapper.toAlumnoResponseDto(alumnosRepository.save(nuevoAlumno));
    }

    @CachePut(key = "#result.id")
    @Override
    public AlumnoResponseDto update(Long id, AlumnoUpdateDto alumnoUpdateDto) {
        log.info("Actualizando alumno con id: {}", id);
        //Si no existe, lanzamos una excepcion
        var alumnoActual = alumnosRepository.findById(id).orElseThrow(() -> new AlumnoNotFoundException(id));
        // Actualizamos los campos del alumno
        Alumno alumnoActualizado = alumnoMapper.toAlumno(alumnoUpdateDto, alumnoActual);
        //Lo guardamos en el repositorio
        return alumnoMapper.toAlumnoResponseDto(alumnosRepository.save(alumnoActualizado));
    }

    // El key es opcional, si no se pone, usa todos los parametros del metodo
    @CacheEvict(key = "#id")
    @Override
    public void deleteById(Long id) {
        log.info("Borrando alumno por id: {}", id);
        // Si no existe, lanzamos una excepcion
        alumnosRepository.findById(id).orElseThrow(() -> new AlumnoNotFoundException(id));
        // Si lo encontramos, lo borramos
        alumnosRepository.deleteById(id);
    }

}
