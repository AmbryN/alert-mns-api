package dev.ambryn.alertmntapi.dto;


import dev.ambryn.alertmntapi.dto.user.UserGetDTO;

import java.util.List;

public record GroupGetFinestDTO(
        Long id,
        String name,
        List<UserGetDTO> members) {}
