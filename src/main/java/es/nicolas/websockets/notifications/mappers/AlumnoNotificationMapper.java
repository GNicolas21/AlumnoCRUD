package es.nicolas.websockets.notifications.mappers;


import es.nicolas.alumnos.models.Alumno;
import es.nicolas.websockets.notifications.dto.AlumnoNotificationResponse;
import org.springframework.stereotype.Component;

@Component
public class AlumnoNotificationMapper {
    public AlumnoNotificationResponse toAlumnoNotificationDto(Alumno alumno) {
        return new AlumnoNotificationResponse(
                alumno.getId(),
                alumno.getNombre(),
                alumno.getApellido(),
                alumno.getAsignatura().getNombre(),
                alumno.getGrado(),
                alumno.getCreatedAt().toString(),
                alumno.getUpdatedAt().toString(),
                alumno.getUuid().toString()
        );
    }
}

