package dev.ambryn.alertmntapi.dto;


import dev.ambryn.alertmntapi.enums.ERole;

public record JwtPayload(Long id, String email, ERole[] roles) {
}
