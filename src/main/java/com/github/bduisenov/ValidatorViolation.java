package com.github.bduisenov;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class ValidatorViolation {

    private final String fieldName;

    private final List<String> errors;

    private final List<ValidatorViolation> violations;

    public static ValidatorViolation fromViolations(String fieldName, List<ValidatorViolation> violations) {
        return new ValidatorViolation(fieldName, null, violations);
    }

    public static ValidatorViolation fromErrors(String fieldName, List<String> errors) {
        return new ValidatorViolation(fieldName, errors, null);
    }
}
