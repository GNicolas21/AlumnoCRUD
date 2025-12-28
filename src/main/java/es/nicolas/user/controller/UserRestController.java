package es.nicolas.user.controller;

import es.nicolas.alumnos.dto.AlumnoCreateDto;
import es.nicolas.alumnos.dto.AlumnoResponseDto;
import es.nicolas.alumnos.dto.AlumnoUpdateDto;
import es.nicolas.alumnos.services.AlumnosService;
import es.nicolas.user.dto.UserInfoResponse;
import es.nicolas.user.dto.UserRequest;
import es.nicolas.user.dto.UserResponse;
import es.nicolas.user.exceptions.UserNameOrEmailExists;
import es.nicolas.user.exceptions.UserNotFound;
import es.nicolas.user.models.User;
import es.nicolas.user.services.UserService;
import es.nicolas.utils.pagination.PageResponse;
import es.nicolas.utils.pagination.PaginationLinksUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/${api.version}/users")
@PreAuthorize("hasRole('USER')")
public class UserRestController {
    private final UserService userService;
    private final PaginationLinksUtils  paginationLinksUtils;
    private final AlumnosService alumnosService;

    /**
     * Obtiene todos los usuarios
     *
     * @param username username del usuario
     * @param email    email del usuario
     * @param isDeleted si está borrado o no
     * @param page      página
     * @param size      tamaño
     * @param sortBy    campo de ordenación
     * @param direction dirección de ordenación
     * @param request   petición
     * @return Respuesta con la página de usuarios
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // Solo los ADMIN pueden ver todos los usuarios
    public ResponseEntity<PageResponse<UserResponse>> findAll(
            @RequestParam(required = false) Optional<String> username,
            @RequestParam(required = false) Optional<String> email,
            @RequestParam(required = false) Optional<Boolean> isDeleted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ){
        log.info("findAll: username: {}, email: {}, isDeleted: {}, page: {}, size: {}, sortBy: {}, direction: {}",
                username, email, isDeleted, page, size, sortBy, direction);

        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(request.getRequestURL().toString());
        Page<UserResponse> pageResult = userService.findAll(username, email, isDeleted, PageRequest.of(page, size, sort));
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
    }

    /**
     * Obtiene un usuario por id
     * @param id del usuario
     * @return Usuario si existe
     * @throws UserNotFound si no existe el usuario
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserInfoResponse> findById(@PathVariable Long id) {
        log.info("findById: id: {}", id);
        return ResponseEntity.ok(userService.findById(id));
    }

    /**
     * Crea un nuevo usuario
     *
     * @param userRequest usuario a crear
     * @return Usuario creado
     * @throws UserNameOrEmailExists               si el nombre de usuario o el email ya existen
     * @throws HttpClientErrorException.BadRequest si hay algún error de validación
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Solo los ADMIN pueden crear usuarios
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest userRequest) {
        log.info("save: userRequest: {}", userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(userRequest));
    }

    /**
     * Actualiza un usuario
     *
     * @param id          id del usuario
     * @param userRequest usuario a actualizar
     * @return Usuario actualizado
     * @throws UserNotFound                        si no existe el usuario (404)
     * @throws HttpClientErrorException.BadRequest si hay algún error de validación (400)
     * @throws UserNameOrEmailExists               si el nombre de usuario o el email ya existen (400)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest userRequest) {
        log.info("update: id: {}, userRequest: {}", id, userRequest);
        return ResponseEntity.ok(userService.update(id, userRequest));
    }

    /**
     * Borra un usuario
     *
     * @param id id del usuario
     * @return Respuesta vacía
     * @throws UserNotFound si no existe el usuario (404)
     */
    @DeleteMapping("/{id}")
    // PreAuthorize sirve para definir permisos a nivel de método
    @PreAuthorize("hasRole('ADMIN')") // Solo los ADMIN pueden borrar usuarios
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("delete: id: {}", id);
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Métodos para USER'S
    /**
     * Obtiene el usuario actual
     *
     * @param user usuario autenticado
     * @return Datos del usuario
     */
    @GetMapping("/me/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserInfoResponse> me(@AuthenticationPrincipal User user) {
        log.info("Obteniendo usuario: {}", user);
        // Como está autenticado, ya sabemos su id
        return ResponseEntity.ok(userService.findById(user.getId()));
    }

    /**
     * Actualiza el usuario actual
     *
     * @param user          usuario autenticado
     * @param userRequest   datos a actualizar
     * @return Usuario actualizado
     * @throws HttpClientErrorException.BadRequest si hay algún error de validación (400)
     */
    @PutMapping("/me/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponse> updateMe(@AuthenticationPrincipal User user,
                                                 @Valid @RequestBody UserRequest userRequest) {
        log.info("Actualizando perfil del usuario: {}", user);
        return ResponseEntity.ok(userService.update(user.getId(), userRequest));
    }

    /**
     * Borra el usuario actual
     *
     * @param user usuario autenticado
     * @return Respuesta vacía
     */
    @DeleteMapping("/me/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteMe(@AuthenticationPrincipal User user) {
        log.info("Eliminando usuario: {}", user);
        userService.deleteById(user.getId());
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtenemos las asignaturas del usuario actual
     *
     * @param user usuario autenticado
     * @param page página
     * @param size tamaño
     * @param sortBy campo de ordenación
     * @param direction dirección de ordenación
     * @return Página de asignaturas
     */
    @GetMapping("/me/alumnos")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PageResponse<AlumnoResponseDto>> getAlumnosByUsuario(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        log.info("Obteniendo alumnos del usuario con id: {}", user.getId());
        Sort sort = direction.equalsIgnoreCase(
                Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(PageResponse.of(
                alumnosService.findByUsuarioId(user.getId(), pageable), sortBy, direction));
    }

    /**
     * Obtiene un alumno del usuario actual
     *
     * @param user usuario autenticado
     * @param idAlumno id del alumno
     * @return Alumno
     * @throws es.nicolas.alumnos.exceptions.AlumnoNotFoundException si no existe el alumno
     */
    @GetMapping("/me/alumnos/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AlumnoResponseDto> getAlumnoByUsuarioAndId(
            @AuthenticationPrincipal User user,
            @PathVariable("id") Long idAlumno) {
        log.info("Obteniendo alumno con id: {}", user);
        return ResponseEntity.ok(alumnosService.findByUsuarioId(user.getId(), idAlumno));
    }

    /**
     * Crea un alumno para el usuario actual
     *
     * @param user usuario autenticado
     * @param alumno alumno a crear
     * @return Alumno creado
     * @throws HttpClientErrorException.BadRequest si hay algún error de validación
     */
    @PostMapping("/me/alumnos")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AlumnoResponseDto> saveAlumno(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody AlumnoCreateDto alumno) {
        log.info("Creando alumno: {}", alumno);
        return ResponseEntity.status(HttpStatus.CREATED).body(alumnosService.save(alumno, user.getId()));
    }


    /**
     * Actualiza un alumno del usuario actual
     *
     * @param user usuario autenticado
     * @param idAlumno id del alumno
     * @param alumno alumno a actualizar
     * @return Alumno actualizado
     * @throws HttpClientErrorException.BadRequest si hay algún error de validación
     * @throws es.nicolas.alumnos.exceptions.AlumnoNotFoundException si no existe el alumno
     */
    @PutMapping("/me/alumnos/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AlumnoResponseDto> updateAlumno(
            @AuthenticationPrincipal User user,
            @PathVariable("id") Long idAlumno,
            @Valid @RequestBody AlumnoUpdateDto alumno) {
        log.info("Actualizando alumno con id: {}", idAlumno);
        return ResponseEntity.ok(alumnosService.update(idAlumno, alumno, user.getId()));
    }


    /**
     * Borra un alumno del usuario actual
     *
     * @param   user usuario autenticado
     * @param   idAlumno id del alumno
     * @return  Respuesta vacía
     * @throws  AlumnoNotFoundException si no existe el alumno
     * @throws  HttpClientErrorException.BadRequest si hay algún error de validación
     */
    @DeleteMapping("/me/alumnos/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteAlumno(
            @AuthenticationPrincipal User user,
            @PathVariable("id") Long idAlumno) {
        log.info("Borrando alumno con id: {}", idAlumno);
        alumnosService.deleteById(idAlumno, user.getId());
        return ResponseEntity.noContent().build();
    }


    /**
     * Manejador de excepciones de Validación: 400 Bad Request
     *
     * @param ex excepción
     * @return Mapa de errores de validación con el campo y el mensaje
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        BindingResult result = ex.getBindingResult();
        problemDetail.setDetail("Falló la validación para el objeto='" + result.getObjectName()
                + "'. " + "Núm. errores: " + result.getErrorCount());

        Map<String, String> errores = new HashMap<>();
        result.getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errores.put(fieldName, errorMessage);
        });

        problemDetail.setProperty("errores", errores);
        return problemDetail;
    }
}