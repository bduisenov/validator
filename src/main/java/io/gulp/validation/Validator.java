package io.gulp.validation;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Collections.singletonList;

/**
 * Be careful with Method::refs in conjunction with intersection types like {@code <T extends A & B>} there's a
 * bug jvm http://bugs.java.com/bugdatabase/view_bug.do?bug_id=8142476. As a workaround you should consider using lambdas instead.
 *
 * @param <T>
 */
public class Validator<T, SELF extends Validator<T, SELF>> {

    static final String NOT_NULL_MESSAGE = "may not be null";

    /**
     * Object that is validated
     */
    private final T value;

    @SuppressWarnings("unchecked")
    private SELF self = (SELF) this;

    /**
     * List of exception thrown during validation.
     */
    private final List<ValidatorViolation> violations = new ArrayList<>();

    /**
     * Creates an applicative functor.
     * {@see http://robotlolita.me/2013/12/08/a-monad-in-practicality-first-class-failures.html}
     *
     * @param value object to be validated
     */
    protected Validator(T value) {
        this.value = value;
    }

    public static <T> Validator<T, ?> of(@NonNull T t) {
        return new Validator<>(t);
    }

    /**
     * Base {@code validate} method
     *
     * @param fieldName
     * @param validation
     * @param message
     * @return
     */
    public SELF validate(@NonNull String fieldName, @NonNull Predicate<T> validation, @NonNull String message) {
        if (!validation.test(getValue())) {
            addViolation(ValidatorViolation.fromErrors(fieldName, singletonList(message)));
        }
        return self;
    }

    /**
     * @param fieldName
     * @param projection
     * @param validation
     * @param message
     * @param <U>
     * @return
     * @see #validate(String, Predicate, String)
     */
    public <U> SELF validate(String fieldName, Function<T, U> projection, Predicate<U> validation, String message) {
        return projection.apply(getValue()) == null
                ? validate(fieldName, $_ -> false, getNotNullMessage())
                : validate(fieldName, projection.andThen(validation::test)::apply, message);
    }

    /**
     * @param projection
     * @param validation
     * @param message
     * @param <U>
     * @return
     * @see #validate(String, Predicate, String)
     */
    public <U> SELF validate(Projection<T, U> projection, Predicate<U> validation, String message) {
        String getter = projection.getName();

        return validate(getter, projection, validation, message);
    }

    /**
     * @param projection
     * @param predicateAndMessage
     * @param <U>
     * @return
     * @see #validate(String, Predicate, String)
     */
    public <U> SELF validate(Projection<T, U> projection, Pair<Predicate<U>, String> predicateAndMessage) {
        String getter = projection.getName();

        return validate(getter, projection, predicateAndMessage._1(), predicateAndMessage._2());
    }

    /**
     * @param fieldName
     * @param projection
     * @param validation
     * @param message
     * @param <U>
     * @return
     * @see #validate(String, Predicate, String)
     */
    public <U> SELF validateOpt(String fieldName, Function<T, U> projection, Predicate<U> validation, String message) {
        Predicate<T> wrapper = projection.andThen(Optional::ofNullable)
                .andThen(opt -> opt.map(validation::test).orElse(true))::apply;

        return validate(fieldName, wrapper, message);
    }

    /**
     * @param projection
     * @param validation
     * @param message
     * @param <U>
     * @return
     * @see #validate(String, Predicate, String)
     */
    public <U> SELF validateOpt(Projection<T, U> projection, Predicate<U> validation, String message) {
        String getter = projection.getName();

        return validateOpt(getter, projection, validation, message);
    }

    /**
     * @param projection
     * @param validation
     * @param message
     * @param <U>
     * @return
     * @see #validate(String, Predicate, String)
     */
    public <U> SELF validateOpt(ProjectionOpt<T, U> projection, Predicate<U> validation, String message) {
        String getter = projection.getName();

        return projection.apply(getValue())
                .map(attr -> validateOpt(getter, $_ -> attr, validation, message))
                .orElse(self);
    }

    /**
     * Base {@code validate} method
     *
     * @param fieldName
     * @param attr
     * @param validation
     * @param <U>
     * @return
     */
    <U> SELF validate(@NonNull String fieldName, @NonNull U attr, @NonNull Function<U, List<String>> validation) {
        List<String> errors = validation.apply(attr);
        if (!errors.isEmpty()) {
            addViolation(ValidatorViolation.fromErrors(fieldName, errors));
        }
        return self;
    }

    /**
     * @param fieldName
     * @param projection
     * @param validation
     * @param <U>
     * @return
     * @see #validate(String, U, Function)
     */
    public <U> SELF validate(String fieldName, Function<T, U> projection, Function<U, List<String>> validation) {
        return Optional.ofNullable(projection.apply(getValue()))
                .map(attr -> validate(fieldName, attr, validation)).orElseGet(() -> {
                    addViolation(ValidatorViolation.fromErrors(fieldName, singletonList(getNotNullMessage())));
                    return self;
                });
    }

    /**
     * @param projection
     * @param validation
     * @param <U>
     * @return
     * @see #validate(String, U, Function)
     */
    public <U> SELF validate(Projection<T, U> projection, Function<U, List<String>> validation) {
        String getter = projection.getName();

        return validate(getter, projection, validation);
    }

    /**
     * @param fieldName
     * @param projection
     * @param validation
     * @param <U>
     * @return
     * @see #validate(String, U, Function)
     */
    public <U> SELF validateOpt(String fieldName, Function<T, U> projection, Function<U, List<String>> validation) {
        return Optional.ofNullable(projection.apply(getValue()))
                .map(attr -> validate(fieldName, attr, validation))
                .orElse(self);
    }

    /**
     * @param projection
     * @param validation
     * @param <U>
     * @return
     * @see #validate(String, U, Function)
     */
    public <U> SELF validateOpt(Projection<T, U> projection, Function<U, List<String>> validation) {
        String getter = projection.getName();

        return validateOpt(getter, projection, validation);
    }

    /**
     * @param projection
     * @param validation
     * @param <U>
     * @return
     * @see #validate(String, U, Function)
     */
    public <U> SELF validateOpt(ProjectionOpt<T, U> projection, Function<U, List<String>> validation) {
        String getter = projection.getName();

        return projection.apply(getValue())
                .map(attr -> validateOpt(getter, $_ -> attr, validation))
                .orElse(self);
    }

    public <U, V extends Validator<U, ?>> SELF validate(String fieldName, Function<T, U> projection, ValidatorFunction<U, V> validatorFunc) {
        U val = projection.apply(getValue());

        if (val != null) {
            V subValidator = validatorFunc.apply(val);
            if (!subValidator.getViolations().isEmpty()) {
                ValidatorViolation violation = ValidatorViolation.fromViolations(fieldName, subValidator.getViolations());
                addViolation(violation);
            }
        } else {
            ValidatorViolation violation = ValidatorViolation.fromErrors(fieldName, singletonList(getNotNullMessage()));
            addViolation(violation);
        }
        return self;
    }

    public <U, V extends Validator<U, ?>> SELF validate(Projection<T, U> projection, ValidatorFunction<U, V> validatorFunc) {
        String getter = projection.getName();

        return validate(getter, projection, validatorFunc);
    }

    public <U, V extends Validator<U, ?>> SELF validateOpt(String fieldName, Function<T, U> projection, ValidatorFunction<U, V> validatorFunc) {
        U val = projection.apply(getValue());

        if (val != null) {
            V subValidator = validatorFunc.apply(val);
            if (!subValidator.getViolations().isEmpty()) {
                ValidatorViolation violation = ValidatorViolation.fromViolations(fieldName, subValidator.getViolations());
                addViolation(violation);
            }
        }

        return self;
    }

    public <U, V extends Validator<U, ?>> SELF validateOpt(Projection<T, U> projection, ValidatorFunction<U, V> validatorFunc) {
        String getter = projection.getName();

        return validateOpt(getter, projection, validatorFunc);
    }

    public <U, V extends Validator<U, ?>> SELF validateOpt(ProjectionOpt<T, U> projection, ValidatorFunction<U, V> validatorFunc) {
        String getter = projection.getName();

        return projection.apply(getValue())
                .map(attr -> validateOpt(getter, $_ -> attr, validatorFunc))
                .orElse(self);
    }

    protected T getValue() {
        return value;
    }

    protected List<ValidatorViolation> getViolations() {
        return violations;
    }

    protected void addViolation(ValidatorViolation violation) {
        violations.add(violation);
    }

    protected boolean hasViolations() {
        return !violations.isEmpty();
    }

    protected String getNotNullMessage() {
        return NOT_NULL_MESSAGE;
    }

    /**
     * @return object that was validated
     */
    public T getOrThrow() {
        if (hasViolations()) {
            throw new ValidationException(getViolations());
        }
        return getValue();
    }

}
