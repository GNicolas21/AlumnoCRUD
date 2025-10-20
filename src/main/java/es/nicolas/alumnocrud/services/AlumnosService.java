package es.nicolas.alumnocrud.services;

import es.nicolas.alumnocrud.dto.AlumnoCreateDto;
import es.nicolas.alumnocrud.dto.AlumnoResponseDto;
import es.nicolas.alumnocrud.dto.AlumnoUpdateDto;

import java.util.List;

public interface AlumnosService {

    List<AlumnoResponseDto> findAll(String nombre, String apellido);

    AlumnoResponseDto findById(Long id);

    AlumnoResponseDto findByUuid(String uuid);

    AlumnoResponseDto save(AlumnoCreateDto alumnoCreateDto);

    AlumnoResponseDto update(Long id, AlumnoUpdateDto alumnoUpdateDto);

    void deleteById(Long id);

}
