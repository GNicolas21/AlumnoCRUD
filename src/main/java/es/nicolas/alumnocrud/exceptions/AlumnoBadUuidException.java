package es.nicolas.alumnocrud.exceptions;

public class AlumnoBadUuidException extends AlumnoException{
    public AlumnoBadUuidException(String uuid){
        super("El uuid " + uuid + " no es válido.");
    }
}
