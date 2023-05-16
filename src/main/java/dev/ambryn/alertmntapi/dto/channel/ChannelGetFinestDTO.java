package dev.ambryn.alertmntapi.dto.channel;

import dev.ambryn.alertmntapi.dto.user.UserGetDTO;
import dev.ambryn.alertmntapi.dto.message.MessageGetDTO;

import java.util.List;

public record ChannelGetFinestDTO(Long id, String name, String visibility, List<UserGetDTO> members) {}
