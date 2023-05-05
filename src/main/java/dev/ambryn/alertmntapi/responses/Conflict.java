package dev.ambryn.alertmntapi.responses;

import dev.ambryn.alertmntapi.enums.EError;
import dev.ambryn.alertmntapi.errors.ApplicationError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public final class Conflict {
    public static ResponseEntity<ErrorResponse> build(String message) {
        ApplicationError.Builder builder = new ApplicationError.Builder();
        builder.setCode(EError.Conflict);
        builder.setMessage(message);
        ApplicationError error = builder.build();
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(error));
    }
}
