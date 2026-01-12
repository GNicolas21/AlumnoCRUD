package es.nicolas.rest.alumnos.services;

import es.nicolas.rest.alumnos.dto.AlumnoCreateDto;
import es.nicolas.rest.alumnos.dto.AlumnoResponseDto;
import es.nicolas.rest.alumnos.dto.AlumnoUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AlumnosService {

    Page<AlumnoResponseDto> findAll(Optional<String> nombre, Optional<String> apellido, Optional<Boolean> isDeleted, Pageable pageable);

    AlumnoResponseDto findById(Long id);

    Page<AlumnoResponseDto> findByUsuarioId(Long usuarioId, Pageable pageable);
    AlumnoResponseDto findByUsuarioId(Long usuarioId, Long idAlumno);

    AlumnoResponseDto findByUuid(String uuid);

    AlumnoResponseDto save(AlumnoCreateDto alumnoCreateDto);
    AlumnoResponseDto save(AlumnoCreateDto alumnoCreateDto, Long usuarioId);

    AlumnoResponseDto update(Long id, AlumnoUpdateDto alumnoUpdateDto);
    AlumnoResponseDto update(Long id, AlumnoUpdateDto alumnoUpdateDto,  Long usuarioId);

    void deleteById(Long id);
    void deleteById(Long id, Long usuarioId);

}
