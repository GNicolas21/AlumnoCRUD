package es.nicolas.alumnos.dto;

import es.nicolas.alumnos.validators.Nombre;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AlumnoCreateDto {
    @Nombre
    private final String nombre;
    @NotBlank(message = "El apellido no puede estar vacío.")
    private final String apellido;
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñÜ0-9\\s]{1,30}$", message = "El grado solo puede contener letras, numeros y espacios, y debe tener entre 1 y 40 caracteres.")
    private final String grado;

    // Agregar esto para tener el campo
    @NotBlank(message = "La asignatura no puede estár vacía")
    private final String asignatura;
}
