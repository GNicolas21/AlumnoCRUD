package es.nicolas.alumnocrud.models;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Alumno {
    private final Long id;

    private final String nombre;
    private final String apellido;
    private final String grado;

    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final UUID uuid;
}
