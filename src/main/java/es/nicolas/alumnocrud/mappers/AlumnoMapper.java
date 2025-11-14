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

    public Alumno toAlumno(AlumnoCreateDto alumnoCreateDto) {
        return Alumno.builder()
                .id(null)
                .nombre(alumnoCreateDto.getNombre())
                .apellido(alumnoCreateDto.getApellido())
                .grado(alumnoCreateDto.getGrado())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .uuid(UUID.randomUUID())
                .build();
//                new Alumno(
//                id,
//                alumnoCreateDto.getNombre(),
//                alumnoCreateDto.getApellido(),
//                alumnoCreateDto.getGrado(),
//                LocalDateTime.now(),
//                LocalDateTime.now(),
//                UUID.randomUUID()
    }

    public Alumno toAlumno(AlumnoUpdateDto alumnoUpdateDto, Alumno alumno) {
        return Alumno.builder()
                .id(alumno.getId())
                .nombre(alumnoUpdateDto.getNombre() != null ? alumnoUpdateDto.getNombre() : alumno.getNombre())
                .apellido(alumnoUpdateDto.getApellido() != null ? alumnoUpdateDto.getApellido() : alumno.getApellido())
                .grado(alumnoUpdateDto.getGrado() != null ? alumnoUpdateDto.getGrado() : alumno.getGrado())
                .createdAt(alumno.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .uuid(alumno.getUuid())
                .build();
    }

    public AlumnoResponseDto toAlumnoResponseDto(Alumno alumno) {
        return AlumnoResponseDto.builder()
                .id(alumno.getId())
                .nombre(alumno.getNombre())
                .apellido(alumno.getApellido())
                .grado(alumno.getGrado())
                .createdAt(alumno.getCreatedAt())
                .updatedAt(alumno.getUpdatedAt())
                .uuid(alumno.getUuid())
                .build();
//        return new AlumnoResponseDto(
//                alumno.getId(),
//                alumno.getNombre(),
//                alumno.getApellido(),
//                alumno.getGrado(),
//                alumno.getCreatedAt(),
//                alumno.getUpdatedAt(),
//                alumno.getUuid().toString()
//        );
    }

    // Mapeamos de modelo a dto
    public List<AlumnoResponseDto> toResponseDtoList(List<Alumno> alumnos) {
        return alumnos.stream()
                .map(this::toAlumnoResponseDto)
                .toList();
    }
}
