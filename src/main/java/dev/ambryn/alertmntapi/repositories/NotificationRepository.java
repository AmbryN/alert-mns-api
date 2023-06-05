package dev.ambryn.alertmntapi.repositories;

import dev.ambryn.alertmntapi.beans.Meeting;
import dev.ambryn.alertmntapi.beans.Notification;
import dev.ambryn.alertmntapi.beans.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReceiverAndSubject_Channel_Id(User receiver, Long id);

    Logger logger = LoggerFactory.getLogger("NotificationRepository");

    void deleteAllByReceiverId(Long id);
}
