package io.gulp.validation;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public interface ConstraintFactory<T> {

    ConstraintFactory<T> addPredicate(Predicate<T> predicate, String message);

    ConstraintFactory<T> addFunction(Function<T, List<String>> validationFunction);
}
