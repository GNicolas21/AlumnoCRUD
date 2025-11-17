package es.nicolas.asignaturas.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AsignaturaNotFoundException extends AsignaturaException{
    public AsignaturaNotFoundException(Long id){
        super("Asignatura con id " + id + " no encontrada.");
    }

    public AsignaturaNotFoundException(String nombre){
        super("Asignatura con nombre " + nombre + " no encontrada.");
    }
}
