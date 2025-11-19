package es.nicolas.asignaturas.dto;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Builder
@Data
public class AsignaturaRequestDto {
    private final String nombre;
    @Length(max = 4, message = "La duración en horas no puede tener más de 4 caracteres.")
    private final Integer duracionHoras;
    private final Boolean isDeleted;
}
