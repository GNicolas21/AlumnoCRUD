package es.nicolas.alumnos.services;

import es.nicolas.alumnos.dto.AlumnoCreateDto;
import es.nicolas.alumnos.dto.AlumnoResponseDto;
import es.nicolas.alumnos.dto.AlumnoUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AlumnosService {

    Page<AlumnoResponseDto> findAll(Optional<String> nombre, Optional<String> apellido, Optional<Boolean> isDeleted, Pageable pageable);

    AlumnoResponseDto findById(Long id);

    AlumnoResponseDto findByUuid(String uuid);

    AlumnoResponseDto save(AlumnoCreateDto alumnoCreateDto);

    AlumnoResponseDto update(Long id, AlumnoUpdateDto alumnoUpdateDto);

    void deleteById(Long id);

}
