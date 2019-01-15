package io.gulp.validation;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public interface ConstraintFactory<T> {

    void addPredicate(Predicate<T> predicate, String message);

    void addFunction(Function<T, List<String>> validationFunction);
}
