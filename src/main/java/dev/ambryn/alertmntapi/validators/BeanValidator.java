package dev.ambryn.alertmntapi.validators;

import jakarta.validation.*;

import java.util.Set;

public class BeanValidator {
    public static <T> void validate(T bean) {
        Set<ConstraintViolation<T>> violations = computeViolations(bean);
        validate(violations);
    }

    static <T> void validate(Set<ConstraintViolation<T>> violations) {
        if (violations.size() > 0) {
            throw new ConstraintViolationException(violations);
        }
    }

    static <T> Set<ConstraintViolation<T>> computeViolations(T bean) {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = validatorFactory.getValidator();
            return validator.validate(bean);
        }
    }
}
