package dev.ambryn.alertmntapi.dto.user;


import dev.ambryn.alertmntapi.dto.channel.ChannelGetDTO;

import java.util.List;

public record UserGetFinestDTO(Long id, String email, String lastname, String firstname, List<String> roles, List<ChannelGetDTO> channels){}
