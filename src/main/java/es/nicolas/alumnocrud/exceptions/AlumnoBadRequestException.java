package es.nicolas.alumnocrud.exceptions;

public class AlumnoBadRequestException extends  RuntimeException {
    public AlumnoBadRequestException(String message) {
        super(message);
    }
}
