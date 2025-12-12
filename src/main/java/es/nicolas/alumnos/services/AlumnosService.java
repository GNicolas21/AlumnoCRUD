package es.nicolas.alumnos.services;

import es.nicolas.alumnos.dto.AlumnoCreateDto;
import es.nicolas.alumnos.dto.AlumnoResponseDto;
import es.nicolas.alumnos.dto.AlumnoUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AlumnosService {

    Page<AlumnoResponseDto> findAll(String nombre, String apellido, Pageable pageable);

    AlumnoResponseDto findById(Long id);

    AlumnoResponseDto findByUuid(String uuid);

    AlumnoResponseDto save(AlumnoCreateDto alumnoCreateDto);

    AlumnoResponseDto update(Long id, AlumnoUpdateDto alumnoUpdateDto);

    void deleteById(Long id);

}
