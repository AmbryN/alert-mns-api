package dev.ambryn.alertmntapi.repositories;

import dev.ambryn.alertmntapi.beans.Meeting;
import dev.ambryn.alertmntapi.beans.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Logger logger = LoggerFactory.getLogger("NotificationRepository");

    void deleteAllByReceiverId(Long id);
}
