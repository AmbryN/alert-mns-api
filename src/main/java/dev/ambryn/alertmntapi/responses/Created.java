package dev.ambryn.alertmntapi.responses;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public final class Created {
    public static<T> ResponseEntity<T> build(T dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(dto);
    }
}
