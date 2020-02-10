package com.github.bduisenov;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class ValidatorCollectionViolation extends ValidatorViolation {

    private final int idx;

    public ValidatorCollectionViolation(String fieldName, int idx, List<String> errors, List<ValidatorViolation> violations) {
        super(fieldName, errors, violations);
        this.idx = idx;
    }

    public static ValidatorViolation fromCollectionViolations(String fieldName, int idx, List<ValidatorViolation> violations) {
        return new ValidatorCollectionViolation(fieldName, idx, null, violations);
    }

    public static ValidatorViolation fromCollectionErrors(String fieldName, int idx, List<String> errors) {
        return new ValidatorCollectionViolation(fieldName, idx, errors, null);
    }
}
