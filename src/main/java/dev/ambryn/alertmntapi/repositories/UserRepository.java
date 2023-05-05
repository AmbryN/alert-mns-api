package dev.ambryn.alertmntapi.repositories;

import dev.ambryn.alertmntapi.beans.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Logger logger = LoggerFactory.getLogger("UserRepository");

    @Query(value = "SELECT u from User u JOIN FETCH u.roles r JOIN FETCH u.channels c WHERE u.email = :email")
    Optional<User> findByEmail(String email);
}
