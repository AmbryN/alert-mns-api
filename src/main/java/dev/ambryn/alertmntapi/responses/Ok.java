package dev.ambryn.alertmntapi.responses;

import org.springframework.http.ResponseEntity;

public final class Ok {

    public static ResponseEntity build() {
        return ResponseEntity.ok()
                             .build();
    }

    public static <T> ResponseEntity<T> build(T entity) {
        return ResponseEntity.ok()
                             .body(entity);
    }
}
