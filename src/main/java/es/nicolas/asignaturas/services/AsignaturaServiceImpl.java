package es.nicolas.asignaturas.services;

import es.nicolas.asignaturas.dto.AsignaturaRequestDto;
import es.nicolas.asignaturas.exceptions.AsignaturaNotFoundException;
import es.nicolas.asignaturas.mappers.AsignaturasMapper;
import es.nicolas.asignaturas.models.Asignatura;
import es.nicolas.asignaturas.repositories.AsignaturasRespository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
    public List<Asignatura> findAll(String nombre) {
        log.info("Bucando asignaturas por nombre: {}", nombre);
        if(nombre == null || nombre.isEmpty()){
            return asignaturasRespository.findAll();
        } else {
            return asignaturasRespository.findByNombreContainsIgnoreCase(nombre);
        }
    }

    @Override
    public Asignatura findByNombre(String nombre) {
        log.info("Bucando asignaturas por nombre: {}", nombre);
        return asignaturasRespository.findByNombreEqualsIgnoreCase(nombre)
                .orElseThrow(()-> new AsignaturaNotFoundException(nombre));
    }

    @Cacheable
    @Override
    public Asignatura findById(Long id) {
        log.info("Bucando asignaturas por id: {}", id);
        return asignaturasRespository.findById(id)
                .orElseThrow(()-> new AsignaturaNotFoundException(id));
    }

    @CachePut
    @Override
    public Asignatura save(AsignaturaRequestDto asignaturaRequestDto) {
        log.info("Guardando asignatura: {}", asignaturaRequestDto);
        Asignatura asignatura = asignaturasMapper.toAsignatura(asignaturaRequestDto);
        return asignaturasRespository.save(asignatura);
    }

    @CachePut
    @Override
    public Asignatura update(Long id, AsignaturaRequestDto asignaturaRequestDto) {
        log.info("Actualizando asignatura con id: {}", id);
        var asignaturaExistente = findById(id);
        Asignatura asignaturaActualizada = asignaturasMapper.toAsignatura(asignaturaRequestDto, asignaturaExistente);
        return asignaturasRespository.save(asignaturaActualizada);
    }

    @CacheEvict
    @Override
    @Transactional // Necesario para que funcione el @Modifying en el repositorio
    public void deleteById(Long id) {
        log.info("Eliminando asignatura con id: {}", id);
        findById(id); // Verifica si existe, lanza excepción si no
        asignaturasRespository.deleteById(id);

    }
}
