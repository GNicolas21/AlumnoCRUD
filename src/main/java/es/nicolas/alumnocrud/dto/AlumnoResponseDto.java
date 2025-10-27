package es.nicolas.alumnocrud.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class AlumnoResponseDto {
    private Long id;

    private String nombre;
    private String apellido;
    private String grado;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID uuid;
}
