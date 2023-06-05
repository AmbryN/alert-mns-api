package dev.ambryn.alertmntapi.controllers;

import dev.ambryn.alertmntapi.beans.Notification;
import dev.ambryn.alertmntapi.beans.User;
import dev.ambryn.alertmntapi.dto.NotificationDTO;
import dev.ambryn.alertmntapi.dto.mappers.dto.NotificationMapper;
import dev.ambryn.alertmntapi.errors.NotFoundException;
import dev.ambryn.alertmntapi.repositories.NotificationRepository;
import dev.ambryn.alertmntapi.repositories.UserRepository;
import dev.ambryn.alertmntapi.responses.NoContent;
import dev.ambryn.alertmntapi.responses.Ok;
import dev.ambryn.alertmntapi.security.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    Logger logger = LoggerFactory.getLogger("NotificationController");
    @Autowired
    UserRepository userRepository;
    @Autowired
    NotificationRepository notificationRepository;
    @Autowired
    JwtUtils jwtUtils;

    @GetMapping
    public ResponseEntity<Set<NotificationDTO>> getNotifications(@RequestHeader("Authorization") String token) {
        logger.debug("Getting all notifications");
        return jwtUtils.getEmailFromBearer(token)
                       .flatMap(email -> userRepository.findByEmail(email))
                       .map(User::getNotifications)
                       .map(notifications -> notifications.stream()
                                                          .map(NotificationMapper::toDTO)
                                                          .collect(Collectors.toSet()))
                       .map(Ok::build)
                       .orElseThrow(() -> new NotFoundException("Could not find user corresponding to Jwt"));
    }

    @PutMapping("/channel/{channelId}")
    public ResponseEntity<List<NotificationDTO>> deleteNotificationFromChannel(@RequestHeader("Authorization") String token,
                                                                               @PathVariable("channelId") Long channelId) {
        logger.debug("Marking user notification from channel with id {} as seen", channelId);
        User user = jwtUtils.getEmailFromBearer(token)
                            .flatMap(email -> userRepository.findByEmail(email))
                            .orElseThrow(() -> new NotFoundException("Could not find user corresponding to Jwt"));

        notificationRepository.findByReceiverAndSubject_Channel_Id(user, channelId)
                              .forEach(notification -> {
                                  notification.markAsSeen();
                                  notificationRepository.save(notification);
                              });

        return Ok.build(user.getNotifications()
                            .stream()
                            .map(NotificationMapper::toDTO)
                            .toList());
    }
}
