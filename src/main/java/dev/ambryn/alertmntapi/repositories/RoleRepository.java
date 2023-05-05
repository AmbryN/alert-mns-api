package dev.ambryn.alertmntapi.repositories;

import dev.ambryn.alertmntapi.beans.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Logger logger = LoggerFactory.getLogger("RoleRepository");
}
