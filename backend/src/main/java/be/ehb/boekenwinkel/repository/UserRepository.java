package be.ehb.boekenwinkel.repository;

import be.ehb.boekenwinkel.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Data-access voor gebruikers.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByToken(String token);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
