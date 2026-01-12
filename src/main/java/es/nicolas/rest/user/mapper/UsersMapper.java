package es.nicolas.rest.user.mapper;

import es.nicolas.rest.user.dto.UserInfoResponse;
import es.nicolas.rest.user.dto.UserRequest;
import es.nicolas.rest.user.dto.UserResponse;
import es.nicolas.rest.user.models.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UsersMapper {
    public User toUser(UserRequest userRequest){
        return User.builder()
                .nombre(userRequest.getNombre())
                .apellidos(userRequest.getApellidos())
                .username(userRequest.getUsername())
                .email(userRequest.getEmail())
                .password(userRequest.getPassword())
                .roles(userRequest.getRoles())
                .isDeleted(userRequest.getIsDeleted())
                .build();
    }

    public User toUser(UserRequest request, Long id) {
        return User.builder()
                .id(id)
                .nombre(request.getNombre())
                .apellidos(request.getApellidos())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .roles(request.getRoles())
                .isDeleted(request.getIsDeleted())
                .build();
    }

    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .nombre(user.getNombre())
                .apellidos(user.getApellidos())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles())
                .isDeleted(user.getIsDeleted())
                .build();
    }

    public UserInfoResponse toUserInfoResponse(User user, List<String> alumnos) {
        return UserInfoResponse.builder()
                .id(user.getId())
                .nombre(user.getNombre())
                .apellidos(user.getApellidos())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles())
                .isDeleted(user.getIsDeleted())
                .alumnos(alumnos)
                .build();
    }

}