package com.github.bduisenov;

import io.vavr.control.Try;
import lombok.NonNull;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.bduisenov.Validator.NOT_NULL_MESSAGE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ValidatorTest {

    static class TryValidator<T, V extends TryValidator<T, V>> extends Validator<T, V> {
        protected TryValidator(T value) {
            super(value);
        }

        public static <T> TryValidator<T, ?> of(@NonNull T t) {
            return new TryValidator<>(t);
        }

        public Try<T> get() {
            return Try.of(this::getOrThrow);
        }

    }

    private TryValidator<Object, ?> validator = TryValidator.of(new Object());

    @Test
    public void validate() {
        Try aTry = validator.validate("test", o -> true, "valid").get();
        assertTrue(aTry.isSuccess());
    }

    @Test
    public void validate_failed() {
        Try<Object> aTry = validator.validate("test", o -> false, "not valid").get();
        assertTrue(aTry.isFailure());
        ValidatorViolation expected = ValidatorViolation.fromErrors("test", Collections.singletonList("not valid"));
        assertEquals(expected, ((ValidationException) aTry.getCause()).getViolations().get(0));
    }

    @Test
    public void validate1() {
        Try<Object> aTry = validator.validate("test", Object::toString, x -> true, "valid").get();
        assertTrue(aTry.isSuccess());
    }

    @Test
    public void validate1_withNull() {
        Try<Object> aTry = validator.validate("test", x -> null, x -> {
            throw new UnsupportedOperationException("should not be called because of null");
        }, "valid").get();
        assertTrue(aTry.isFailure());
        ValidatorViolation expected = ValidatorViolation.fromErrors("test", Collections.singletonList(NOT_NULL_MESSAGE));
        assertEquals(expected, ((ValidationException) aTry.getCause()).getViolations().get(0));
    }

    @Test
    public void validate_serializableFunc() {
        Try<Object> aTry = validator.validate(Object::toString, x -> true, "valid").get();
        assertTrue(aTry.isSuccess());
    }

    @Test
    public void validate_serializableFuncWithNull() {
        Object target = new Object() {
            @Override public String toString() {
                return null;
            }
        };

        Try<Object> aTry = TryValidator.of(target).validate(Object::toString, x -> {
            throw new UnsupportedOperationException("should not be called because of null");
        }, "valid").get();
        assertTrue(aTry.isFailure());
        ValidatorViolation expected = ValidatorViolation.fromErrors("toString", Collections.singletonList(NOT_NULL_MESSAGE));
        assertEquals(expected, ((ValidationException) aTry.getCause()).getViolations().get(0));
    }

    @Test
    public void validateOpt() {
        Try<Object> aTry = validator.validateOpt("test", x -> x, o -> true, "valid").get();
        assertTrue(aTry.isSuccess());
    }

    @Test
    public void validateOpt_serializableFunc() {
        Try<Object> aTry = validator.validateOpt(Object::toString, x -> true, "valid").get();
        assertTrue(aTry.isSuccess());
    }

    @Test
    public void validateOpt_serializableFuncFailed() {
        Try<Object> aTry = validator.validateOpt(Object::toString, x -> false, "not valid").get();
        assertTrue(aTry.isFailure());
        ValidatorViolation expected = ValidatorViolation.fromErrors("toString", Collections.singletonList("not valid"));
        assertEquals(expected, ((ValidationException) aTry.getCause()).getViolations().get(0));
    }

    @Test
    public void validateOpt_withNull() {
        Try<Object> aTry = validator.validateOpt("test", x -> null, o -> true, "valid").get();
        assertTrue(aTry.isSuccess());
    }

    @Test
    public void validateOpt_withNull2() {
        Try<Object> aTry = validator.validateOpt("test", x -> null, o -> false, "not valid").get();
        assertTrue(aTry.isSuccess());
    }

    @Test
    public void validateOpt_failed() {
        Try<Object> aTry = validator.validateOpt("test", x -> x, o -> false, "not valid").get();
        assertTrue(aTry.isFailure());
        ValidatorViolation expected = ValidatorViolation.fromErrors("test", Collections.singletonList("not valid"));
        assertEquals(expected, ((ValidationException) aTry.getCause()).getViolations().get(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateOpt_failedBecauseOfAnonymousSerializable() {
        Projection<Object, String> projection = x -> "";
        validator.validateOpt(projection, o -> false, "not valid").get();
    }

    @Test
    public void validate_withValidation() {
        Function<Object, List<String>> validation = x -> Collections.emptyList();
        Function<Object, String> projection = x -> "";
        Try<Object> aTry = validator.validate("test", projection, validation).get();
        assertTrue(aTry.isSuccess());
    }

    @Test
    public void validate_withValidationWithNull() {
        Function<String, List<String>> validation = x -> {
            throw new UnsupportedOperationException("should not be called because of null");
        };

        Projection<Object, String> projection = x -> null;
        Try<Object> aTry = validator.validate("test", projection, validation).get();
        assertTrue(aTry.isFailure());
        ValidatorViolation expected = ValidatorViolation.fromErrors("test", Collections.singletonList(NOT_NULL_MESSAGE));
        assertEquals(expected, ((ValidationException) aTry.getCause()).getViolations().get(0));
    }

    @Test
    public void validate_withValidationFailed() {
        Function<Object, List<String>> validation = x -> Collections.singletonList("not valid");

        Function<Object, String> projection = x -> "";
        Try<Object> aTry = validator.validate("test", projection, validation).get();
        assertTrue(aTry.isFailure());
        ValidatorViolation expected = ValidatorViolation.fromErrors("test", Collections.singletonList("not valid"));
        assertEquals(expected, ((ValidationException) aTry.getCause()).getViolations().get(0));
    }

    @Test
    public void validate_withProjection() {
        Function<Object, List<String>> validation = x -> Collections.emptyList();
        Try<Object> aTry = validator.validate(Object::getClass, validation).get();
        assertTrue(aTry.isSuccess());
    }

    @Test
    public void validate_withProjection_withNull() {
        Function<Object, List<String>> validation = x -> {
            throw new UnsupportedOperationException("should not be called because of null");
        };
        Object target = new Object() {
            @Override public String toString() {
                return null;
            }
        };

        Try<Object> aTry = TryValidator.of(target).validate(Object::toString, validation).get();
        assertTrue(aTry.isFailure());
        ValidatorViolation expected = ValidatorViolation.fromErrors("toString", Collections.singletonList(NOT_NULL_MESSAGE));
        assertEquals(expected, ((ValidationException) aTry.getCause()).getViolations().get(0));
    }

    @Test
    public void validate_withProjection_failed() {
        Function<Object, List<String>> validation = x -> Collections.singletonList("not valid");
        Try<Object> aTry = validator.validate(Object::getClass, validation).get();
        assertTrue(aTry.isFailure());
        ValidatorViolation expected = ValidatorViolation.fromErrors("class", Collections.singletonList("not valid"));
        assertEquals(expected, ((ValidationException) aTry.getCause()).getViolations().get(0));
    }

    @Test
    public void validateOpt_withValidation() {
        Function<Object, List<String>> validation = x -> Collections.emptyList();
        Projection<Object, String> projection = x -> "";
        Try<Object> aTry = validator.validate("test", projection, validation).get();
        assertTrue(aTry.isSuccess());
    }

    @Test
    public void validateOpt_withValidationWithNull() {
        Function<Object, List<String>> validation = x -> Collections.emptyList();
        Try<Object> aTry = validator.validateOpt("test", x -> null, validation).get();
        assertTrue(aTry.isSuccess());
    }

    @Test
    public void validateOpt_withValidationFailed() {
        Function<Object, List<String>> validation = x -> Collections.singletonList("not valid");
        Try<Object> aTry = validator.validateOpt("test", Object::getClass, validation).get();
        assertTrue(aTry.isFailure());
        ValidatorViolation expected = ValidatorViolation.fromErrors("test", Collections.singletonList("not valid"));
        assertEquals(expected, ((ValidationException) aTry.getCause()).getViolations().get(0));
    }

    @Test
    public void validateOpt_withProjection_failed() {
        Function<Object, List<String>> validation = x -> Collections.singletonList("not valid");
        Try<Object> aTry = validator.validateOpt(Object::getClass, validation).get();
        assertTrue(aTry.isFailure());
        ValidatorViolation expected = ValidatorViolation.fromErrors("class", Collections.singletonList("not valid"));
        assertEquals(expected, ((ValidationException) aTry.getCause()).getViolations().get(0));
    }

    @Test
    public void validate_withValidatorFunc() {
        Function<String, TryValidator<String, ?>> validation = x -> TryValidator.of("");

        Projection<Object, String> projection = x -> "";
        Try<Object> aTry = validator.nest("test", projection, validation).get();
        assertTrue(aTry.isSuccess());
    }

    @Test
    public void validate_withValidatorFuncWithNull() {
        Function<Object, TryValidator<Object, ?>> validation = x -> {
            throw new UnsupportedOperationException("should not be called because of null");
        };

        Object target = new Object() {
            @Override public String toString() {
                return null;
            }
        };

        Try<Object> aTry = TryValidator.of(target).nest("test", Object::toString, validation).get();
        assertTrue(aTry.isFailure());
        ValidatorViolation expected = ValidatorViolation.fromErrors("test", Collections.singletonList(NOT_NULL_MESSAGE));
        assertEquals(expected, ((ValidationException) aTry.getCause()).getViolations().get(0));
    }

    @Test
    public void validateOpt_withValidatorFuncWithNull() {
        Function<Object, TryValidator<Object, ?>> validation = x -> {
            throw new UnsupportedOperationException("should not be called because of null");
        };

        Object target = new Object() {
            @Override public String toString() {
                return null;
            }
        };

        Try<Object> aTry = TryValidator.of(target).nestOpt("test", Object::toString, validation).get();
        assertTrue(aTry.isSuccess());
    }

    @Test
    public void validateOpt_withValidatorFunc() {
        Function<Object, TryValidator<Object, ?>> validation = x -> TryValidator.of(new Object());

        Try<Object> aTry = validator.nestOpt("test", Object::toString, validation).get();
        assertTrue(aTry.isSuccess());
    }

    @Test
    public void validateOpt_withValidatorFuncFailed() {
        Function<Object, TryValidator<Object, ?>> validation = x -> {
            ValidatorViolation violation = ValidatorViolation.fromErrors("inner.field", Collections.singletonList("not valid"));
            TryValidator<Object, ?> subValidator = TryValidator.of(new Object());
            subValidator.addViolation(violation);
            return subValidator;
        };

        Try<Object> aTry = validator.nestOpt("test", Object::toString, validation).get();
        assertTrue(aTry.isFailure());
        List<ValidatorViolation> violations = Collections.singletonList(ValidatorViolation.fromErrors("inner.field", Collections.singletonList("not valid")));
        ValidatorViolation expected = ValidatorViolation.fromViolations("test", violations);

        assertEquals(expected, ((ValidationException) aTry.getCause()).getViolations().get(0));
    }

    @Test
    public void validateOpt_withProjectionOpt_returnsNone() {
        class T {
            public Optional<Integer> get() {
                return Optional.empty();
            }
        }
        Function<Integer, List<String>> validation = x -> Collections.singletonList("should success because of Optional::empty");

        Try<T> aTry = TryValidator.of(new T()).validateOpt(T::get, validation).get();
        assertTrue(aTry.isSuccess());
    }

    @Test
    public void validateOpt_withProjectionOpt_returnsSome() {
        Function<Integer, List<String>> validation = x -> Collections.singletonList("validation failed");
        Try<TClass> aTry = TryValidator.of(new TClass()).validateOpt(TClass::get, validation).get();
        assertTrue(aTry.isFailure());
    }

    @Test
    public void validateOpt_withProjectionOpt_withValidatorFunction() {
        Function<Integer, TryValidator<Integer, ?>> validation = TryValidator::of;
        Try<TClass> aTry = TryValidator.of(new TClass()).nestOpt(TClass::get, validation).get();
        assertTrue(aTry.isSuccess());
    }

    @Test
    public void validateOpt_withProjectionOpt_withPredicate() {
        Try<TClass> aTry = TryValidator.of(new TClass()).validateOpt(TClass::get, (Predicate<Integer>) i -> true, "error msg").get();
        assertTrue(aTry.isSuccess());
    }

    static class TClass {
        public Optional<Integer> get() {
            return Optional.of(1);
        }
    }

}
