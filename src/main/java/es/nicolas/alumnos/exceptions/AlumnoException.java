package es.nicolas.alumnos.exceptions;

public abstract class AlumnoException extends RuntimeException {
    public AlumnoException(String message) {
        super(message);
    }
}
