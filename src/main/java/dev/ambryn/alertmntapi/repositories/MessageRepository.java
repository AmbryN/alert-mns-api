package dev.ambryn.alertmntapi.repositories;

import dev.ambryn.alertmntapi.beans.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Logger logger = LoggerFactory.getLogger("MessageRepository");

    void deleteAllByCreatorId(Long id);
}
