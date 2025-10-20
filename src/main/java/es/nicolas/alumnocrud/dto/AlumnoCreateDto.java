package es.nicolas.alumnocrud.dto;

import lombok.Data;

@Data
public class AlumnoCreateDto {
    private final String nombre;
    private final String apellido;
    private final String grado;

}
