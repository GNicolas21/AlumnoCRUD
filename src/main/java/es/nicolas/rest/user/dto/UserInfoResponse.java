package es.nicolas.rest.user.dto;

import es.nicolas.rest.user.models.Role;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfoResponse {
    private Long id;
    private String nombre;
    private String apellidos;
    private String username;
    private String email;

    @Builder.Default
    private Set<Role> roles = Set.of(Role.USER);

    @Builder.Default
    private Boolean isDeleted = false;

    @Builder.Default
    private List<String> alumnos = new ArrayList<>();
}
