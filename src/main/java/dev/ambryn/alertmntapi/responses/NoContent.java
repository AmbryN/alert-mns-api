package dev.ambryn.alertmntapi.responses;

import org.springframework.http.ResponseEntity;

public final class NoContent {

    public static ResponseEntity build() {
        return ResponseEntity.noContent()
                             .build();
    }
}
