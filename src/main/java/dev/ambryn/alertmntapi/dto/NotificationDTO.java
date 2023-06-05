package dev.ambryn.alertmntapi.dto;

import dev.ambryn.alertmntapi.dto.channel.ChannelGetDTO;
import dev.ambryn.alertmntapi.dto.user.UserGetDTO;

import java.time.LocalDateTime;

public record NotificationDTO(Long id,
                              ChannelGetDTO channel,
                              UserGetDTO user,
                              String type,
                              LocalDateTime createdAt,
                              LocalDateTime seenAt) {}
