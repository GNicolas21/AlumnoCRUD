package es.nicolas.rest.user.services;

import es.nicolas.rest.alumnos.repositories.AlumnosRepository;
import es.nicolas.rest.user.dto.UserInfoResponse;
import es.nicolas.rest.user.dto.UserRequest;
import es.nicolas.rest.user.dto.UserResponse;
import es.nicolas.rest.user.exceptions.UserNameOrEmailExists;
import es.nicolas.rest.user.exceptions.UserNotFound;
import es.nicolas.rest.user.mapper.UsersMapper;
import es.nicolas.rest.user.models.User;
import es.nicolas.rest.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"users"})
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final UsersMapper usersMapper;
    private final AlumnosRepository alumnosRepository;

    @Override
    public Page<UserResponse> findAll(Optional<String> username, Optional<String> email, Optional<Boolean> isDeleted, Pageable pageable) {
        log.info("Bucando todos los usuarios con username : {} y borrados: {}", username, isDeleted);
        // Criterio de búsqueda por nombre
        Specification<User> specUsernameUser = (root, query, criteriaBuilder) ->
                username.map(m -> criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), "%" +
                        m.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        // Criterio de búsqueda por email
        Specification<User> specEmailUser = (root, query, criteriaBuilder) ->
                email.map(m -> criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" +
                        m.toLowerCase() + "%"))
                        .orElseGet(() ->  criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        // Criterio de búsqueda por borrado
        Specification<User> specIsDeleted = (root, query, criteriaBuilder) ->
                isDeleted.map(m -> criteriaBuilder.equal(root.get("isDeleted"), m))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<User> criterio = Specification.allOf(specUsernameUser, specEmailUser, specIsDeleted);

        return userRepository.findAll(criterio, pageable).map(usersMapper::toUserResponse);
    }

    @Override
    @Cacheable
    public UserInfoResponse findById(Long id) {
        log.info("Bucando usuario con id: {}", id);
        // Buscar el usuario
        var user = userRepository.findById(id).orElseThrow(() -> new UserNotFound(id));
        // Buscar los alumnos asociados al usuario
        var alumnos = alumnosRepository.findByUsuarioId(id).stream()
                .map(p -> p.getNombre()).toList();
        return usersMapper.toUserInfoResponse(user, alumnos);
    }

    @Override
    @CachePut(key = "#result.id")
    public UserResponse save(UserRequest userRequest) {
        log.info("Guardando usuario: {}", userRequest);
        userRepository.findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(userRequest.getUsername(), userRequest.getEmail())
                .ifPresent(u -> {
                    throw new UserNameOrEmailExists("Ya existe un usuario con ese username o email");
                });
        return usersMapper.toUserResponse(userRepository.save(usersMapper.toUser(userRequest)));
    }

    @Override
    @CachePut(key = "#result.id")
    public UserResponse update(Long id, UserRequest userRequest) {
        log.info("Actualizando usuario: {}", userRequest);
        userRepository.findById(id).orElseThrow(() -> new UserNotFound(id));
        userRepository.findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(userRequest.getUsername(), userRequest.getEmail())
                .ifPresent(u -> {
                    if (!u.getId().equals(id)) {
                        System.out.println("usuario encontrado: " + u.getId() + " Mi id: " + id);
                        throw new UserNameOrEmailExists("Ya existe un usuario con ese username o email");
                    }
                });
        return usersMapper.toUserResponse(userRepository.save(usersMapper.toUser(userRequest, id)));
    }

    @Override
    @Transactional
    @CacheEvict(key = "#id")
    public void deleteById(Long id) {
        log.info("Eliminando usuario con id: {}", id);
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFound(id));
        if(alumnosRepository.existsByUsuarioId(id)){
            // Si hay alumnos, lo marcamos como borrado lógico ¿)
            log.info("Borrado lógico de usuario por id: {}", id);
            userRepository.updateIsDeletedToTrueById(id);
        } else {
            log.info("Borrado fisico de usuario por id: {}", id);
            userRepository.delete(user);
        }
    }

    public List<User> findAllActiveUsers() {
        log.info("Buscando todos los usuarios activos");
        return userRepository.findAllByIsDeletedFalse();
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
