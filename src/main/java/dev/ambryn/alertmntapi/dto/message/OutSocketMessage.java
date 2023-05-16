package dev.ambryn.alertmntapi.dto.message;

import dev.ambryn.alertmntapi.dto.user.UserGetDTO;

public record OutSocketMessage(UserGetDTO sender, String content) {}

