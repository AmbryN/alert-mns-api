package dev.ambryn.alertmntapi.repositories;

import dev.ambryn.alertmntapi.beans.User;
import jakarta.persistence.NamedStoredProcedureQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Logger logger = LoggerFactory.getLogger("UserRepository");

    @Query(value = "SELECT u from User u LEFT JOIN FETCH u.roles r LEFT JOIN FETCH u.channels c WHERE u.email = :email")
    Optional<User> findByEmail(String email);

    /**
     * Deletes a user and all data related to this user, using a stored procedure.
     * Linked data:
     * - is_allowed_in (Channel)
     * - is_subscribed_to (Channel)
     * - is_member_of (Group)
     * - subject, message, meeting
     * - has_role (Role)
     *
     * @param id must not be {@literal null}.
     */
    @Override
    @Query(nativeQuery = true, value = "CALL delete_user(:id)")
    void deleteById(@Param("id") Long id);
}
