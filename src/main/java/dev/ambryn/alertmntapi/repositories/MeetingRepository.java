package dev.ambryn.alertmntapi.repositories;

import dev.ambryn.alertmntapi.beans.Meeting;
import dev.ambryn.alertmntapi.beans.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    Logger logger = LoggerFactory.getLogger("MeetingRepository");

    void deleteAllByOrganizerId(Long id);
}
