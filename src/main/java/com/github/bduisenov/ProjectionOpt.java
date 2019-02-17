package com.github.bduisenov;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Function;

public interface ProjectionOpt<T, R> extends Function<T, Optional<R>>, Serializable, LambdaMethodReferenceReflection {
}
