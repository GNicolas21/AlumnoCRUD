package es.nicolas.alumnos.services;

import es.nicolas.alumnos.dto.AlumnoCreateDto;
import es.nicolas.alumnos.dto.AlumnoResponseDto;
import es.nicolas.alumnos.dto.AlumnoUpdateDto;

import java.util.List;

public interface AlumnosService {

    List<AlumnoResponseDto> findAll(String nombre, String apellido);

    AlumnoResponseDto findById(Long id);

    AlumnoResponseDto findByUuid(String uuid);

    AlumnoResponseDto save(AlumnoCreateDto alumnoCreateDto);

    AlumnoResponseDto update(Long id, AlumnoUpdateDto alumnoUpdateDto);

    void deleteById(Long id);

}
