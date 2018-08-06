package io.gulp.validation;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

public interface LambdaMethodReferenceReflection {

    MethodHandles.Lookup lookup = MethodHandles.lookup();

    default SerializedLambda serialized() {
        try {
            Method replaceMethod = getClass().getDeclaredMethod("writeReplace");
            replaceMethod.setAccessible(true);
            MethodHandle mh = lookup.unreflect(replaceMethod);
            return (SerializedLambda) mh.invokeWithArguments(this);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    default String getName() {
        return normalize(serialized().getImplMethodName());
    }

    default String normalize(String val) {
        if (val.startsWith("lambda$")) {
            throw new IllegalArgumentException("Only stateless lambdas should be used as serializable functions");
        }
        if (val.startsWith("get") && val.length() > 3) {
            return val.substring(3, 4).toLowerCase() + val.substring(4);
        }
        return val;
    }

}
