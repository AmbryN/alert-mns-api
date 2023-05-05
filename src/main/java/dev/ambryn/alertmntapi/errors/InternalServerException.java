package dev.ambryn.alertmntapi.errors;

public class InternalServerException extends RuntimeException {

    public InternalServerException(String message) {
        super(message);
    }
    public InternalServerException(String message, Throwable ex) {
        super(message, ex);
    }
}
