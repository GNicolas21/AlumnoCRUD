package es.nicolas.rest.user.services;

import es.nicolas.rest.user.dto.UserInfoResponse;
import es.nicolas.rest.user.dto.UserRequest;
import es.nicolas.rest.user.dto.UserResponse;
import es.nicolas.rest.user.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Page<UserResponse> findAll(Optional<String> username, Optional<String> email, Optional<Boolean> isDeleted, Pageable pageable);

    UserInfoResponse findById(Long id);

    UserResponse save(UserRequest userRequest);

    UserResponse update(Long id, UserRequest userRequest);

    void deleteById(Long id);

    List<User> findAllActiveUsers();
}
