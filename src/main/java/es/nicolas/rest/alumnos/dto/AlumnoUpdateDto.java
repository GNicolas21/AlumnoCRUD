package es.nicolas.rest.alumnos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
@Schema(description = "Alumno a actualizar")
public class AlumnoUpdateDto {
    @Schema(description = "Nombre del alumno", example = "Juan")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñÜü\\s]{1,30}$", message = "El nombre solo puede contener letras y espacios, y debe tener entre 1 y 30 caracteres.")
    private final String nombre;
    // Aquí le quito la anotación @NotBlank para poder dejar en null este campo (para probar el update())
    @Schema(description = "Apellido del alumno", example = "Pérez")
    private final String apellido;
    @Schema(description = "Grado del alumno", example = "1 DAW")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñÜü0-9\\s]{1,40}$", message = "El grado solo puede contener letras, numeros y espacios, y debe tener entre 1 y 40 caracteres.")
    private final String grado;
    //Aqui no ponemos la asignatura porque no la cambiaramos ?D
    @Schema(description = "Asignatura del alumno", example = "Matemáticas")
    private final String asignatura;
}
