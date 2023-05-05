package dev.ambryn.alertmntapi.errors;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
    public NotFoundException(String message, Throwable ex) {
        super(message, ex);
    }
}
