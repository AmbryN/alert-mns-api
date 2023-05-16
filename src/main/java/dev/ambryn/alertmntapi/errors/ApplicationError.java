package dev.ambryn.alertmntapi.errors;

import com.fasterxml.jackson.annotation.JsonFilter;
import dev.ambryn.alertmntapi.enums.EError;
import org.springframework.lang.Nullable;

import java.util.List;

public class ApplicationError {
    EError code;
    String target;
    String value;
    String message;
    List<ApplicationError> details;

    private ApplicationError() {
        this(null, null, null, null, null);
    }

    private ApplicationError(EError code, String message) {
        this(code, null, null, message, null);
    }

    private ApplicationError(EError code, String target, String value, String message) {
        this(code, target, message, value, null);
    }

    private ApplicationError(EError code, String target, String value, String message, List<ApplicationError> details) {
        this.code = code;
        this.target = target;
        this.value = value;
        this.message = message;
        this.details = details;
    }

    public EError getCode() {
        return code;
    }

    private void setCode(EError code) {
        this.code = code;
    }

    public String getTarget() {
        return target;
    }

    private void setTarget(String target) {
        this.target = target;
    }

    public String getValue() {
        return value;
    }

    private void setValue(String value) {
        this.value = value;
    }

    public String getMessage() {
        return message;
    }

    private void setMessage(String message) {
        this.message = message;
    }

    public List<ApplicationError> getDetails() {
        return details;
    }

    private void setDetails(List<ApplicationError> details) {
        this.details = details;
    }

    public static class Builder {
        private final ApplicationError applicationError;

        public Builder() {
            this.applicationError = new ApplicationError();
        }

        public Builder setCode(EError errorCode) {
            applicationError.setCode(errorCode);
            return this;
        }

        public Builder setTarget(String target) {
            applicationError.setTarget(target);
            return this;
        }

        public Builder setValue(String value) {
            applicationError.setValue(value);
            return this;
        }

        public Builder setMessage(String message) {
            applicationError.setMessage(message);
            return this;
        }

        public Builder setDetails(List<ApplicationError> sub) {
            applicationError.setDetails(sub);
            return this;
        }

        public ApplicationError build() {
            return applicationError;
        }
    }
}
