package es.nicolas.rest.user.exceptions;

public abstract class UserException extends RuntimeException {
  public UserException(String message) {
    super(message);
  }
}
