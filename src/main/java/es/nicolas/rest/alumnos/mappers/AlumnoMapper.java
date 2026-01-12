package es.nicolas.rest.alumnos.mappers;

import es.nicolas.rest.alumnos.dto.AlumnoCreateDto;
import es.nicolas.rest.alumnos.dto.AlumnoResponseDto;
import es.nicolas.rest.alumnos.dto.AlumnoUpdateDto;
import es.nicolas.rest.alumnos.models.Alumno;
import es.nicolas.rest.asignaturas.models.Asignatura;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class AlumnoMapper {

    public Alumno toAlumno(AlumnoCreateDto alumnoCreateDto, Asignatura asignatura) {
        return Alumno.builder()
                .id(null)
                .nombre(alumnoCreateDto.getNombre())
                .apellido(alumnoCreateDto.getApellido())
                .grado(alumnoCreateDto.getGrado())
                .asignatura(asignatura)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .uuid(UUID.randomUUID())
                .build();
    }

    public Alumno toAlumno(AlumnoUpdateDto alumnoUpdateDto, Alumno alumno) {
        return Alumno.builder()
                .id(alumno.getId())
                .nombre(alumnoUpdateDto.getNombre() != null ? alumnoUpdateDto.getNombre() : alumno.getNombre())
                .apellido(alumnoUpdateDto.getApellido() != null ? alumnoUpdateDto.getApellido() : alumno.getApellido())
                .grado(alumnoUpdateDto.getGrado() != null ? alumnoUpdateDto.getGrado() : alumno.getGrado())
                .asignatura(alumno.getAsignatura())
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
                // Incluimos el nombre de la asignatura del alumno
                .asignatura(alumno.getAsignatura().getNombre())
                .createdAt(alumno.getCreatedAt())
                .updatedAt(alumno.getUpdatedAt())
                .uuid(alumno.getUuid())
                .build();
    }

    // Mapeamos de modelo a dto
    public List<AlumnoResponseDto> toResponseDtoList(List<Alumno> alumnos) {
        return alumnos.stream()
                .map(this::toAlumnoResponseDto)
                .toList();
    }

    // Mapeamos de model a DTO (page)
    public Page<AlumnoResponseDto> toResponseDtoPage(Page<Alumno> alumnos) {
        return alumnos.map(this::toAlumnoResponseDto);
    }
}
