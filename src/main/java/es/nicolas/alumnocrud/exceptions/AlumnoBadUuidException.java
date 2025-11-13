package es.nicolas.alumnocrud.exceptions;

public class AlumnoBadUuidException extends AlumnoException{
    public AlumnoBadUuidException(String uuid){
        super("Alumno con uuid " + uuid + " no encontrado.");
    }
}
