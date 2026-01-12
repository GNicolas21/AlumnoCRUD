package es.nicolas.asignaturas.mappers;

import es.nicolas.rest.asignaturas.dto.AsignaturaRequestDto;
import es.nicolas.rest.asignaturas.mappers.AsignaturasMapper;
import es.nicolas.rest.asignaturas.models.Asignatura;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AsignaturasMapperTest {
    private final Asignatura asignatura = new Asignatura().builder().id(1L).nombre("Programacion").build();

    // Inyectamos el mapper
    private final AsignaturasMapper asignaturasMapper = new AsignaturasMapper();

    private final AsignaturaRequestDto asignaturaDto = AsignaturaRequestDto.builder().nombre("PROGRAMACION").build();


    @Test
    public void whenNoAsignatura_thenReturnAsignatura() {
        Asignatura mappedAsignatura = asignaturasMapper.toAsignatura(asignaturaDto);

        assertEquals(asignaturaDto.getNombre(), mappedAsignatura.getNombre());
    }

    @Test
    public void whenToAsignaturaWithExistingTitular_thenReturnUpdatedAsignatura() {
        Asignatura updatedAsignatura = asignaturasMapper.toAsignatura(asignaturaDto, asignatura);

        assertEquals(asignaturaDto.getNombre(), updatedAsignatura.getNombre());
    }
}