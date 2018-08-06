package io.gulp.validation;

import lombok.Value;

import java.util.List;

@Value
public class ValidationException extends RuntimeException {

    private final List<ValidatorViolation> violations;

    @Override
    public String getMessage() {
        return this.toString();
    }
}
