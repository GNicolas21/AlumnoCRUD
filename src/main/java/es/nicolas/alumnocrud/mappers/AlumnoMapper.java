package es.nicolas.alumnocrud.mappers;

import es.nicolas.alumnocrud.dto.AlumnoCreateDto;
import es.nicolas.alumnocrud.dto.AlumnoResponseDto;
import es.nicolas.alumnocrud.dto.AlumnoUpdateDto;
import es.nicolas.alumnocrud.models.Alumno;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class AlumnoMapper {

    public Alumno toAlumno(Long id, AlumnoCreateDto alumnoCreateDto) {
        return new Alumno(
                id,
                alumnoCreateDto.getNombre(),
                alumnoCreateDto.getApellido(),
                alumnoCreateDto.getGrado(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                UUID.randomUUID()
        );
    }

    public Alumno toAlumno(AlumnoUpdateDto alumnoUpdateDto, Alumno alumno) {
        return new Alumno(
                alumno.getId(),
                alumnoUpdateDto.getNombre() != null ? alumnoUpdateDto.getNombre() : alumno.getNombre(),
                alumnoUpdateDto.getApellido() != null ? alumnoUpdateDto.getApellido() : alumno.getApellido(),
                alumnoUpdateDto.getGrado() != null ? alumnoUpdateDto.getGrado() : alumno.getGrado(),
                alumno.getCreatedAt(),
                LocalDateTime.now(),
                alumno.getUuid()
        );
    }

    public AlumnoResponseDto toAlumnoResponseDto(Alumno alumno) {
        return new AlumnoResponseDto(
                alumno.getId(),
                alumno.getNombre(),
                alumno.getApellido(),
                alumno.getGrado(),
                alumno.getCreatedAt(),
                alumno.getUpdatedAt(),
                alumno.getUuid().toString()
        );
    }

    // Mapeamos de modelo a dto
    public List<AlumnoResponseDto> toResponseDtoList(List<Alumno> alumnos) {
        return alumnos.stream()
                .map(this::toAlumnoResponseDto)
                .toList();
    }
}
