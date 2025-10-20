package es.nicolas.alumnocrud.dto;

import lombok.Data;

@Data
public class AlumnoUpdateDto {
    private final String nombre;
    private final String apellido;
    private final String grado;
}
