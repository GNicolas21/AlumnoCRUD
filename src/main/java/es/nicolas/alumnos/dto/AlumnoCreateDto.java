package es.nicolas.alumnos.dto;

import es.nicolas.alumnos.validators.Nombre;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@Schema(description = "Alumno a crear")
public class AlumnoCreateDto {
    @Nombre
    @Schema(description = "Nombre del alumno", example = "Juan")
    private final String nombre;
    @Schema(description = "Apellido del alumno", example = "Pérez")
    @NotBlank(message = "El apellido no puede estar vacío.")
    private final String apellido;
    @Schema(description = "Grado del alumno", example = "1 DAW")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñÜ0-9\\s]{1,30}$", message = "El grado solo puede contener letras, numeros y espacios, y debe tener entre 1 y 40 caracteres.")
    private final String grado;

    // Agregar esto para tener el campo
    @Schema(description = "Asignatura del alumno", example = "Matemáticas")
    @NotBlank(message = "La asignatura no puede estár vacía")
    private final String asignatura;
}
