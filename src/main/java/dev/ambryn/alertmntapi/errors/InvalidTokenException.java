package dev.ambryn.alertmntapi.errors;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(String message, Throwable ex) {
        super(message, ex);
    }
}
