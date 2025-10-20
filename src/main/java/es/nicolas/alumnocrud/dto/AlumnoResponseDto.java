package es.nicolas.alumnocrud.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlumnoResponseDto {
    private final Long id;

    private final String nombre;
    private final String apellido;
    private final String grado;

    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final String uuid;
}
