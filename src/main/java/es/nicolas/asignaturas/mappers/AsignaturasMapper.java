package es.nicolas.asignaturas.mappers;

import es.nicolas.asignaturas.dto.AsignaturaRequestDto;
import es.nicolas.asignaturas.models.Asignatura;
import org.springframework.stereotype.Component;

@Component
public class AsignaturasMapper {
    // Mapeamos de dto a modelo
    public Asignatura toAsignatura(AsignaturaRequestDto asignaturaRequestDto) {
        return Asignatura.builder()
                .id(null)
                .nombre(asignaturaRequestDto.getNombre())
                .build();
    }

    // Mapeamos de dto a modelo para actualizaciones parciales
    public Asignatura toAsignatura(AsignaturaRequestDto dto, Asignatura asignatura) {
        return Asignatura.builder()
                .id(asignatura.getId())
                .nombre(dto.getNombre() != null ? dto.getNombre() : asignatura.getNombre())
                .createdAt(asignatura.getCreatedAt())
                // Este campo es automatico
                // .updatedAt(LocalDateTime.now())
                .isDeleted(dto.getIsDeleted() != null ? dto.getIsDeleted() : asignatura.getIsDeleted())
                .build();
    }
}
