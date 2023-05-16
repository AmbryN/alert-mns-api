package dev.ambryn.alertmntapi.repositories;

import dev.ambryn.alertmntapi.beans.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    Logger logger = LoggerFactory.getLogger("GroupRepository");

    @Modifying
    @Query(value = "DELETE FROM is_member_of WHERE usr_id = :id", nativeQuery = true)
    void deleteUserFromGroups(@Param("id") Long id);
}
