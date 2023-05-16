package dev.ambryn.alertmntapi.repositories;

import dev.ambryn.alertmntapi.beans.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, Long> {
    final Logger logger = LoggerFactory.getLogger("ChannelRepository");

    @Modifying
    @Query(value = "DELETE FROM is_allowed_in WHERE usr_id = :id", nativeQuery = true)
    void deleteUserFromChannels(@Param("id") Long id);
}
