package es.nicolas.alumnos.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.nicolas.alumnos.dto.AlumnoCreateDto;
import es.nicolas.alumnos.dto.AlumnoResponseDto;
import es.nicolas.alumnos.dto.AlumnoUpdateDto;
import es.nicolas.alumnos.mappers.AlumnoMapper;
import es.nicolas.alumnos.models.Alumno;
import es.nicolas.alumnos.repositories.AlumnosRepository;
import es.nicolas.alumnos.exceptions.AlumnoBadUuidException;
import es.nicolas.alumnos.exceptions.AlumnoNotFoundException;
import es.nicolas.asignaturas.services.AsignaturaService;
import es.nicolas.config.websockets.WebSocketConfig;
import es.nicolas.config.websockets.WebSocketHandler;
import es.nicolas.websockets.notifications.dto.AlumnoNotificationResponse;
import es.nicolas.websockets.notifications.mappers.AlumnoNotificationMapper;
import es.nicolas.websockets.notifications.models.Notification;
import jakarta.persistence.criteria.Join;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@CacheConfig(cacheNames = {"alumnos"})
@Slf4j
@Service
public class AlumnosServiceImpl implements AlumnosService, InitializingBean {
    private final AlumnosRepository alumnosRepository;
    private final AlumnoMapper alumnoMapper;
    private final AsignaturaService asignaturaService;

    // Dependencias para WebSockets
    private final WebSocketConfig webSocketConfig;
    private final ObjectMapper objectMapper;
    private final AlumnoNotificationMapper alumnoNotificationMapper;
    private WebSocketHandler webSocketService;

    public void afterPropertiesSet() {
        this.webSocketService = this.webSocketConfig.webSocketAlumnosHandler();
    }

    // Para inicializar los test's
    public void setWebSocketService(WebSocketHandler webSocketService) {
        this.webSocketService = webSocketService;
    }

    @Override
    public Page<AlumnoResponseDto> findAll(Optional<String> nombre, Optional<String> apellido, Optional<Boolean> isDeleted, Pageable pageable) {
//        //Si todos los args están vacios o nulos, devolvemos todos los alumnos
//        if ((nombre == null || nombre.isEmpty()) && (apellido == null || apellido.isEmpty())) {
//            log.info("Buscando todos los alumnos");
////            return alumnoMapper.toResponseDtoList(alumnosRepository.findAll());
//            return alumnosRepository.findAll(pageable).map(alumnoMapper::toAlumnoResponseDto);
//        }
//
//        //Si el nombre no está vacío pero el apellido si, buscamos por nombre
//        if ((nombre != null && !nombre.isEmpty()) && (apellido == null || apellido.isEmpty())) {
//            log.info("Buscando alumnos por nombre: {}", nombre);
////            return alumnoMapper.toResponseDtoList(alumnosRepository.findByNombre(nombre));
//            return alumnosRepository.findByNombre(nombre, pageable).map(alumnoMapper::toAlumnoResponseDto);
//        }
//
//        //Si el apellido no está vacío, pero el nombre sí y buscamos por apellido
//        if ((nombre == null || nombre.isEmpty())) {
//            log.info("Buscando alumnos por apellido: {}", apellido);
////            return alumnoMapper.toResponseDtoList(alumnosRepository.findByApellidoContainsIgnoreCase(apellido));
//            return alumnosRepository.findByApellidoContainsIgnoreCase(apellido.toLowerCase(), pageable)
//                    .map(alumnoMapper::toAlumnoResponseDto);
//        }

        log.info("Buscando alumnos por nombre: {}, apellido: {}, isDeleted: {}", nombre, apellido, isDeleted);
        // Busqyeda por nombre
        Specification<Alumno> specNombreAlumno = (root, query, criteriaBuilder) ->
                nombre.map(n -> criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + n.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true))); // si no hay numero no filtramos

        // Busqueda por apellido
        Specification<Alumno> specApellidoAlumno = (root, query, criteriaBuilder) ->
                //{Join<Alumno, Alumno> alumnoJoin = root.join("alumno"); Si tuvieramos que tomar datos de otra tabla, hariamos un "JOIN<nomeTabla, nomeTabla>"
                apellido.map(t -> criteriaBuilder.like(criteriaBuilder.lower(root.get("apellido")), "%" + t.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Alumno> specIsDeleted = (root, query, criteriaBuilder) ->
                isDeleted.map(d -> criteriaBuilder.equal(root.get("isDeleted"), d))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Alumno> criterio = Specification.allOf(specNombreAlumno, specApellidoAlumno, specIsDeleted);
        return alumnosRepository.findAll(criterio, pageable)
                .map(alumnoMapper::toAlumnoResponseDto);
    }

    // Cachea con el id como key
    @Cacheable(key = "#id")
    @Override
    public AlumnoResponseDto findById(Long id) {
        log.info("Buscando alumno por id: {}", id);
        return alumnoMapper.toAlumnoResponseDto(alumnosRepository.findById(id)
                .orElseThrow(() -> new AlumnoNotFoundException(id)));
    }

    // Cachea con el uuid como key
    @Cacheable(key = "#uuid")
    @Override
    public AlumnoResponseDto findByUuid(String uuid) {
        log.info("Buscando alumno por uuid: {}", uuid);
        try {
            var myUUID = UUID.fromString(uuid);
            return alumnoMapper.toAlumnoResponseDto(alumnosRepository.findByUuid(myUUID)
                    .orElseThrow(() -> new AlumnoNotFoundException(myUUID)));
        } catch (IllegalArgumentException e) {
            throw new AlumnoBadUuidException(uuid);
        }
    }


    // Cachea con el id del resultado de la operacion como key
    @CachePut(key = "#result.id")
    @Override
    public AlumnoResponseDto save(AlumnoCreateDto alumnoCreateDto) {
        log.info("Guardando alumno: {}", alumnoCreateDto);
        //Creamos un nuevo alumno con los datos que nos vienen y la guardamos
        var asignatura = asignaturaService.findByNombre(alumnoCreateDto.getAsignatura());
        Alumno alumnoSaved = alumnosRepository.save(alumnoMapper.toAlumno(alumnoCreateDto, asignatura));
        // Enviamos la notificacion a los clientes mediante ws
        onChange(Notification.Tipo.CREATE, alumnoSaved);
        return alumnoMapper.toAlumnoResponseDto(alumnoSaved);
    }


    @CachePut(key = "#result.id")
    @Override
    public AlumnoResponseDto update(Long id, AlumnoUpdateDto alumnoUpdateDto) {
        log.info("Actualizando alumno con id: {}", id);
        //Si no existe, lanzamos una excepcion
        var alumnoActual = alumnosRepository.findById(id).orElseThrow(() -> new AlumnoNotFoundException(id));
        // Actualizamos los campos del alumno
        Alumno alumnoActualizado = alumnosRepository.save(alumnoMapper.toAlumno(alumnoUpdateDto, alumnoActual));
        // Enviamos la notificación a los clientes WS
        onChange(Notification.Tipo.UPDATE, alumnoActualizado);
        //Lo guardamos en el repositorio
        return alumnoMapper.toAlumnoResponseDto(alumnoActualizado);
    }

    // El key es opcional, si no se pone, usa todos los parametros del metodo
    @CacheEvict(key = "#id")
    @Override
    public void deleteById(Long id) {
        log.info("Borrando alumno por id: {}", id);
        // Si no existe, lanzamos una excepcion
        Alumno alumnoDeleted = alumnosRepository.findById(id).orElseThrow(() ->
                new AlumnoNotFoundException(id));
        // Si lo encontramos, lo borramos
        alumnosRepository.deleteById(id);

        // Enviamos la notificacion a los clientes ws
        onChange(Notification.Tipo.DELETE, alumnoDeleted);
    }

    void onChange(Notification.Tipo tipo, Alumno data) {
        log.debug("Servicio de alumnos onChange con tipo: {} y dato: {}", tipo, data);

        if (webSocketService == null) {
            log.warn("No se ha podido enviar la notificacion a los clientes ws, no se ha encontrado el servicio");
            webSocketService = this.webSocketConfig.webSocketAlumnosHandler();
        }


        try {
            Notification<AlumnoNotificationResponse> notificacion = new Notification<>(
                    "ALUMNOS",
                    tipo,
                    alumnoNotificationMapper.toAlumnoNotificationDto(data),
                    LocalDateTime.now().toString()
            );

            String json = objectMapper.writeValueAsString((notificacion));

            log.info("Enviando mensaje a los clientes ws");

            Thread senderThread = new Thread(() -> {
                try {
                    webSocketService.sendMessage(json);
                } catch (Exception e) {
                    log.error("Error al enviar mensaje a través del servicio WebSocket", e);
                }
            });
            senderThread.setName("WebSocketAlumno-" + data.getId());
            senderThread.setDaemon(true); // Para que no impida que la app se cierre
            senderThread.start();
            log.info("Hilo de websocket iniciando: {}", data.getId());
        } catch (JsonProcessingException e) {
            log.error("Error al convertir la notificación a JSON", e);
        }
    }
}
