package dev.ambryn.alertmntapi.dto.mappers.dto;

import dev.ambryn.alertmntapi.beans.Message;
import dev.ambryn.alertmntapi.beans.Notification;
import dev.ambryn.alertmntapi.dto.NotificationDTO;

public class NotificationMapper {
    public static NotificationDTO toDTO(Notification notification) {

        return new NotificationDTO(notification.getId(),
                                   ChannelMapper.toDTO(notification.getSubject()
                                                                   .getChannel()),
                                   UserMapper.toDto(notification.getReceiver()),
                                   notification.getSubject() instanceof Message ? "MESSAGE" : "MEETING",
                                   notification.getSubject()
                                               .getSentAt(),
                                   notification.getSeenAt());
    }
}
