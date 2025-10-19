package es.nicolas.exceptions;

public class AlumnoBadUuidException extends AlumnoException{
    public AlumnoBadUuidException(String uuid){
        super("El uuid " + uuid + " no es válido.");
    }
}
