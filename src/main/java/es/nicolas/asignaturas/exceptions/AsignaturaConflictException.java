package es.nicolas.asignaturas.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class AsignaturaConflictException extends AsignaturaException{

    public  AsignaturaConflictException(String message){
        super(message);
    }
}
