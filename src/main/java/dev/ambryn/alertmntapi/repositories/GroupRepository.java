package dev.ambryn.alertmntapi.repositories;

import dev.ambryn.alertmntapi.beans.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    Logger logger = LoggerFactory.getLogger("GroupRepository");
}
