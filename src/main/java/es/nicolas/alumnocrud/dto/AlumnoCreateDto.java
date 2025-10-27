package es.nicolas.alumnocrud.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AlumnoCreateDto {
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñÜü\\s]{1,30}$", message = "El nombre solo puede contener letras y espacios, y debe tener entre 1 y 30 caracteres.")
    private final String nombre;
    @NotBlank(message = "El apellido no puede estar vacío.")
    private final String apellido;
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñÜü0-9\\s]{1,40}$", message = "El grado solo puede contener letras, numeros y espacios, y debe tener entre 1 y 40 caracteres.")
    private final String grado;
}
