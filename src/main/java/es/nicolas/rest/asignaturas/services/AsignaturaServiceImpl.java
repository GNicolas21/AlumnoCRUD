package es.nicolas.rest.asignaturas.services;

import es.nicolas.rest.asignaturas.dto.AsignaturaRequestDto;
import es.nicolas.rest.asignaturas.exceptions.AsignaturaConflictException;
import es.nicolas.rest.asignaturas.exceptions.AsignaturaNotFoundException;
import es.nicolas.rest.asignaturas.mappers.AsignaturasMapper;
import es.nicolas.rest.asignaturas.models.Asignatura;
import es.nicolas.rest.asignaturas.repositories.AsignaturasRespository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"asignaturas"})
public class AsignaturaServiceImpl implements  AsignaturaService{
    private final AsignaturasRespository asignaturasRespository;
    private final AsignaturasMapper asignaturasMapper;

    @Override
    public Page<Asignatura> findAll(Optional<String> nombre, Optional<Boolean> isDeleted, Pageable pageable) {
        log.info("Bucando asignaturas por nombre: {}, isDeleted: {}", nombre, isDeleted);
        // Criterio de busqueda por numero y luego por isDeleted
        Specification<Asignatura> specNombreAsignatura = (root, query, criteriaBuilder) ->
                nombre.map(n -> criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" +
                        n.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Asignatura> specIsDeleted = (root, query, criteriaBuilder) ->
                isDeleted.map(d -> criteriaBuilder.equal(root.get("isDeleted"), d))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Asignatura> criterio = Specification.allOf(specNombreAsignatura, specIsDeleted);
        return  asignaturasRespository.findAll(criterio, pageable);
    }

    @Cacheable
    @Override
    public Asignatura findById(Long id) {
        log.info("Bucando asignaturas por id: {}", id);
        return asignaturasRespository.findById(id)
                .orElseThrow(()-> new AsignaturaNotFoundException(id));
    }

    @Override
    @Cacheable
    public Asignatura findByNombre(String nombre) {
        log.info("Bucando asignaturas por nombre: {}", nombre);
        return asignaturasRespository.findByNombreEqualsIgnoreCase(nombre)
                .orElseThrow(()-> new AsignaturaNotFoundException(nombre));
    }

    @CachePut
    @Override
    public Asignatura save(AsignaturaRequestDto asignaturaRequestDto) {
        log.info("Guardando asignatura: {}", asignaturaRequestDto);
        asignaturasRespository.findByNombreEqualsIgnoreCase(asignaturaRequestDto.getNombre()).ifPresent(asignatura -> {
            throw new AsignaturaConflictException("Error al guardar asignatura");
        });
        Asignatura asignatura = asignaturasMapper.toAsignatura(asignaturaRequestDto);
        return asignaturasRespository.save(asignatura);
    }

    @CachePut
    @Override
    public Asignatura update(Long id, AsignaturaRequestDto asignaturaRequestDto) {
        log.info("Actualizando asignatura con id: {}", id);
        var asignaturaExistente = findById(id);
        // No debe existir dos asignaturas con el mismo nombre
        asignaturasRespository.findByNombreEqualsIgnoreCase(asignaturaRequestDto.getNombre()).ifPresent(asig-> {
            if(!asig.getId().equals(id)){
                throw new AsignaturaConflictException("Ya existe una asignatura con el nombre " + asignaturaRequestDto.getNombre());
            }
        });
        // Actualizamos los datos
        Asignatura asignaturaActualizada = asignaturasMapper.toAsignatura(asignaturaRequestDto, asignaturaExistente);
        return asignaturasRespository.save(asignaturaActualizada);
    }

    @CacheEvict
    @Override
    @Transactional // Necesario para que funcione el @Modifying en el repositorio
    public void deleteById(Long id) {
        log.info("Eliminando asignatura con id: {}", id);
        Asignatura asignatura = findById(id); // Verifica si existe, lanza excepción si no
        // O lo marcamos como borrado, para evitar problemas de cascada, no podemos borrar titulares con tarjetas!!!
        // La otra forma es que comprobáramos si hay tarjetas para borrarlas antes
        if (asignaturasRespository.existsByAlumnoById(id)) {
            String mensaje = "No se puede borrar la asignatura con id: " +  id + " porque tiene alumnos asociados";
            log.warn(mensaje);
            throw new AsignaturaConflictException(mensaje);
        } else {
            asignaturasRespository.deleteById(id);
        }
//        asignaturasRespository.deleteById(id);
    }
}
