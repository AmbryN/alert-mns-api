package dev.ambryn.alertmntapi.dto;


import dev.ambryn.alertmntapi.dto.user.UserGetDTO;

import java.sql.Timestamp;

public record MeetingGetDTO(
        Long id,
        UserGetDTO organizer,
        Timestamp datetime,
        int duration) {}
