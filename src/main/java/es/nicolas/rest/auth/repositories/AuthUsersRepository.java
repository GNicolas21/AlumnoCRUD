package es.nicolas.rest.auth.repositories;

import es.nicolas.rest.user.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthUsersRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
}
