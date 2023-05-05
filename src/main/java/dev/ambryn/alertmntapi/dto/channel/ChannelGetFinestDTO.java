package dev.ambryn.alertmntapi.dto.channel;


import dev.ambryn.alertmntapi.dto.user.UserGetDTO;
import dev.ambryn.alertmntapi.dto.message.MessageGetDTO;

public record ChannelGetFinestDTO(
        Long id,
        String name,
        String visibility,
        MessageGetDTO[] messages,
        UserGetDTO[] members,
        UserGetDTO[] subscribers) {}
