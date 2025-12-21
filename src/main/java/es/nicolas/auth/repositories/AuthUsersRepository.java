package es.nicolas.auth.repositories;

import es.nicolas.user.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthUsersRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
}
