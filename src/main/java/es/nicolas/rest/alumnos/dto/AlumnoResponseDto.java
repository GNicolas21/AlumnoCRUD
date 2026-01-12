package es.nicolas.rest.alumnos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Alumno a devolver como respuesta")
public class AlumnoResponseDto {
    @Schema(description = "ID del alumno", example = "1")
    private Long id;

    @Schema(description = "Nombre del alumno", example = "Juan")
    private String nombre;
    @Schema(description = "Apellido del alumno", example = "Pérez")
    private String apellido;
    @Schema(description = "Grado del alumno", example = "1 DAW")
    private String grado;

    @Schema(description = "Fecha de creación del alumno", example = "2024-01-01T12:00:00")
    private LocalDateTime createdAt;
    @Schema(description = "Fecha de última actualización del alumno", example = "2024-01-02T12:12:12")
    private LocalDateTime updatedAt;
    @Schema(description = "UUID del alumno", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID uuid;

    // La nueva imple
    @Schema(description = "Asignatura del alumno", example = "Matemáticas")
    private String asignatura;
}
