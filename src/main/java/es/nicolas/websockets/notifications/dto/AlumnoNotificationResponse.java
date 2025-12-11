package es.nicolas.websockets.notifications.dto;

public record AlumnoNotificationResponse(
    Long id,
    String nombre,
    String apellido,
    String grado,

    String asignatura,

    String createdAt,
    String updatedAt,
    String uuid
){

}
