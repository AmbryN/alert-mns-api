package dev.ambryn.alertmntapi.errors;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable ex) {
        super(message, ex);
    }
}
