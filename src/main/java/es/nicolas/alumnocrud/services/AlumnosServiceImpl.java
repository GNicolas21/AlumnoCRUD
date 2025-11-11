package es.nicolas.alumnocrud.services;

import es.nicolas.alumnocrud.dto.AlumnoCreateDto;
import es.nicolas.alumnocrud.dto.AlumnoResponseDto;
import es.nicolas.alumnocrud.dto.AlumnoUpdateDto;
import es.nicolas.alumnocrud.mappers.AlumnoMapper;
import es.nicolas.alumnocrud.models.Alumno;
import es.nicolas.alumnocrud.repositories.AlumnosRepository;
import es.nicolas.alumnocrud.exceptions.AlumnoBadUuidException;
import es.nicolas.alumnocrud.exceptions.AlumnoNotFoundException;
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


    @Override
    public List<AlumnoResponseDto> findAll(String nombre, String apellido) {
        //Si está vacio o nulo, devolvemos todos los alumnos
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

    @Cacheable(key = "#id")
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
        Alumno nuevoAlumno = alumnoMapper.toAlumno(alumnoCreateDto);
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
