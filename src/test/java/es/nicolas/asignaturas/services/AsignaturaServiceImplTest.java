package es.nicolas.asignaturas.services;

import es.nicolas.asignaturas.dto.AsignaturaRequestDto;
import es.nicolas.asignaturas.exceptions.AsignaturaConflictException;
import es.nicolas.asignaturas.mappers.AsignaturasMapper;
import es.nicolas.asignaturas.models.Asignatura;
import es.nicolas.asignaturas.repositories.AsignaturasRespository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsignaturaServiceImplTest {
    private final Asignatura asignatura = Asignatura.builder().id(1L).nombre("Programacion").build();
    private final AsignaturaRequestDto asignaturaRequestDto = AsignaturaRequestDto.builder().nombre("Programacion").build();

    // LA clase a la que se inyectan mocks y spy
    @InjectMocks
    private AsignaturaServiceImpl asignaturaServiceImpl;

    @Mock
    private AsignaturasRespository asignaturasRespository;

    @Mock
    private AsignaturasMapper asignaturasMapper;

    @Test
    public void testFindAll(){
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(List.of(asignatura));
        when(asignaturasRespository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        var res = asignaturaServiceImpl.findAll(Optional.empty(), Optional.empty(), pageable);

        assertAll("findAll",
                ()->assertNotNull(res),
                () -> assertFalse(res.isEmpty())
        );

        verify(asignaturasRespository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    public void testFindByNombre() {
        when(asignaturasRespository.findByNombreEqualsIgnoreCase(anyString())).thenReturn(Optional.of(asignatura));

        var res = asignaturaServiceImpl.findByNombre("Programacion");

        assertAll("findByNombre",
                () -> assertNotNull(res),
                () -> assertEquals("Programacion", res.getNombre())
        );

        verify(asignaturasRespository, times(1)).findByNombreEqualsIgnoreCase(anyString());
    }

    @Test
    public void testFindById() {
        when(asignaturasRespository.findById(anyLong())).thenReturn(Optional.of(asignatura));

        var res = asignaturaServiceImpl.findById(1L);

        assertAll("findById",
                () -> assertNotNull(res),
                () -> assertEquals("Programacion", res.getNombre())
        );

        verify(asignaturasRespository, times(1)).findById(anyLong());
    }

    @Test
    public void testSave() {
        when(asignaturasRespository.findByNombreEqualsIgnoreCase(anyString())).thenReturn(Optional.empty());
        // Simulamos el Mapeer, si no el servicio fallará
        when(asignaturasMapper.toAsignatura(any(AsignaturaRequestDto.class))).thenReturn(asignatura);
        when(asignaturasRespository.save(any(Asignatura.class))).thenReturn(asignatura);

        var resultado = asignaturaServiceImpl.save(asignaturaRequestDto);

        assertAll("save",
                () -> assertNotNull(resultado),
                () -> assertEquals("Programacion", resultado.getNombre())
        );

        verify(asignaturasRespository, times(1)).findByNombreEqualsIgnoreCase(anyString());
        verify(asignaturasRespository, times(1)).save(any(Asignatura.class));
        verify(asignaturasMapper, times(1)).toAsignatura(any(AsignaturaRequestDto.class)); // Verificando el mapper
    }

    @Test
    public void testSaveConflict() {
        when(asignaturasRespository.findByNombreEqualsIgnoreCase(anyString())).thenReturn(Optional.of(asignatura));

        var res = assertThrows(AsignaturaConflictException.class,
                () -> asignaturaServiceImpl.save(asignaturaRequestDto));

        assertAll("saveConflict",
                () -> assertNotNull(res),
                () -> assertEquals("Error al guardar asignatura", res.getMessage())
        );

        verify(asignaturasRespository, times(1)).findByNombreEqualsIgnoreCase(anyString());
        verify(asignaturasRespository, times(0)).save(any(Asignatura.class));
    }

}