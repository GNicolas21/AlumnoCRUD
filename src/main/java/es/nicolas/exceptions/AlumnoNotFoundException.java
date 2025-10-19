package es.nicolas.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

// Excepción lanzada cuando no se encuentra un alumno por su id o uuid (ERROR - 404 Not Found)
@ResponseStatus(HttpStatus.NOT_FOUND)
public class AlumnoNotFoundException extends AlumnoException{
    public AlumnoNotFoundException(Long id){
        super("Alumno con id " + id + " no encontrado.");
    }

    public AlumnoNotFoundException(UUID uuid){
        super("Alumno con uuid " + uuid + " no encontrado.");
    }
}
