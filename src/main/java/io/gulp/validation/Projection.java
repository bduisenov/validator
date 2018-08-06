package io.gulp.validation;

import java.io.Serializable;
import java.util.function.Function;

public interface Projection<T, R> extends Function<T, R>, Serializable, LambdaMethodReferenceReflection {
}
