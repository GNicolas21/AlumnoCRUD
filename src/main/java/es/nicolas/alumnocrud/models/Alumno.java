package es.nicolas.alumnocrud.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data

public class Alumno {
    private final Long id;
    private final String nombre;
    private final String apellido;
    private final String grado;

    private final LocalDateTime createAt;
    private final LocalDateTime updateAt;
    private final UUID uuid;
}
