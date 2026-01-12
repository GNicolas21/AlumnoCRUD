package es.nicolas.rest.asignaturas.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AsignaturaRequestDto {
    @NotBlank(message = "El nombre no puede estár en blanco.")
    private final String nombre;
    @Digits(integer = 3, fraction = 0, message = "La duración en horas no puede tener más de 4 caracteres.")
    private final Integer duracionHoras;
    private final Boolean isDeleted;
}
