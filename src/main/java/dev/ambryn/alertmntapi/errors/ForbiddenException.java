package dev.ambryn.alertmntapi.errors;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException() { super(); }

    public ForbiddenException(String message) {
        super(message);
    }
    public ForbiddenException(String message, Throwable ex) {
        super(message, ex);
    }
}
