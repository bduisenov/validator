package io.gulp.validation;

import lombok.Value;

import java.util.List;

@Value
public class ValidatorViolation {

    private String fieldName;

    private List<String> errors;

    private List<ValidatorViolation> violations;

    public static ValidatorViolation fromViolations(String fieldName, List<ValidatorViolation> violations) {
        return new ValidatorViolation(fieldName, null, violations);
    }

    public static ValidatorViolation fromErrors(String fieldName, List<String> errors) {
        return new ValidatorViolation(fieldName, errors, null);
    }

}
