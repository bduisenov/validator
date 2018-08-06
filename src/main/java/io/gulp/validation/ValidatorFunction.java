package io.gulp.validation;

import java.util.function.Function;

public interface ValidatorFunction<T, V extends Validator> extends Function<T, V> {
}
