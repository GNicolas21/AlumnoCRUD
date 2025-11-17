package es.nicolas.alumnos.dto;

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
public class AlumnoResponseDto {
    private Long id;

    private String nombre;
    private String apellido;
    private String grado;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID uuid;

    // La nueva imple
    private String asignatura;
}
