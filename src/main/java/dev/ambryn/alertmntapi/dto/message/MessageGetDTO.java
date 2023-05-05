package dev.ambryn.alertmntapi.dto.message;


import dev.ambryn.alertmntapi.dto.user.UserGetDTO;

public record MessageGetDTO(
        Long id,
        UserGetDTO sender,
        String content,
        String fileName,
        String filePath) {}
