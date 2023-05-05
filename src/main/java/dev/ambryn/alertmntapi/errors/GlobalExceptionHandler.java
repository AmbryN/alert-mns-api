package dev.ambryn.alertmntapi.errors;

import dev.ambryn.alertmntapi.enums.EError;
import dev.ambryn.alertmntapi.responses.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handle(UnauthorizedException exception) {
        ApplicationError.Builder builder = new ApplicationError.Builder();
        builder.setCode(EError.Unauthorized);
        builder.setMessage(exception.getMessage());
        ApplicationError error = builder.build();
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(error));
    }

    @ExceptionHandler(HttpClientErrorException.BadRequest.class)
    public ResponseEntity<ErrorResponse> handle(HttpClientErrorException.BadRequest exception) {
        ApplicationError.Builder builder = new ApplicationError.Builder();
        builder.setCode(EError.BadArgument);
        builder.setMessage(exception.getMessage());
        ApplicationError error = builder.build();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(error));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handle(HttpMessageNotReadableException exception) {
        ApplicationError.Builder builder = new ApplicationError.Builder();
        builder.setCode(EError.BadArgument);
        builder.setMessage(exception.getMessage());
        ApplicationError error = builder.build();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(error));
    }


    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(NotFoundException exception) {
        ApplicationError.Builder builder = new ApplicationError.Builder();
        builder.setCode(EError.NotFound);
        builder.setMessage(exception.getMessage());
        ApplicationError error = builder.build();
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(error));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handle(ForbiddenException exception) {
        ApplicationError.Builder builder = new ApplicationError.Builder();
        builder.setCode(EError.Forbidden);
        builder.setMessage(exception.getMessage());
        ApplicationError error = builder.build();
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(error));
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ErrorResponse> handle(InternalServerException exception) {
        ApplicationError.Builder builder = new ApplicationError.Builder();
        builder.setCode(EError.ServerError);
        builder.setMessage(exception.getMessage());
        ApplicationError error = builder.build();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(error));
    }

        @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handle(ConstraintViolationException exception) {
        List<ConstraintViolationBean> messages = new ArrayList<>();
        List<ApplicationError> errors = new ArrayList<>();
        for (ConstraintViolation cv : exception.getConstraintViolations()) {
            ConstraintViolationBean cvb = new ConstraintViolationBean(cv);
            ApplicationError.Builder builder = new ApplicationError.Builder();
            builder.setCode(EError.BadArgument);
            builder.setTarget(cvb.getTarget());
            builder.setValue(cvb.getValue());
            builder.setMessage(cvb.getMessage());
            ApplicationError error = builder.build();
            messages.add(new ConstraintViolationBean(cv));
            errors.add(error);
        }

        ApplicationError.Builder builder = new ApplicationError.Builder();
        builder.setCode(EError.BadArgument);
        builder.setDetails(errors);
        ApplicationError error = builder.build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(error));
    }

    public static class ConstraintViolationBean {
        private final String target;
        private final String message;
        private final String value;

        private ConstraintViolationBean(ConstraintViolation constraintViolation) {
            String propertyPath = null;
            for (Path.Node node : constraintViolation.getPropertyPath()) {
                propertyPath = node.getName();
            }
            this.target = propertyPath;
            this.message = constraintViolation.getMessage();
            if (constraintViolation.getInvalidValue() != null) this.value = constraintViolation.getInvalidValue().toString();
            else this.value = null;
        }

        public String getTarget() {
            return target;
        }

        public String getMessage() {
            return message;
        }

        public String getValue() {
            return value;
        }
    }
}
