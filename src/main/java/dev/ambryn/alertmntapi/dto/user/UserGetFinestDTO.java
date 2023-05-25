package dev.ambryn.alertmntapi.dto.user;

import dev.ambryn.alertmntapi.beans.Role;
import dev.ambryn.alertmntapi.dto.RoleDTO;
import dev.ambryn.alertmntapi.dto.channel.ChannelGetDTO;

import java.util.List;

public record UserGetFinestDTO(Long id,
                               String email,
                               String lastname,
                               String firstname,
                               List<Role> roles,
                               List<ChannelGetDTO> channels) {}
