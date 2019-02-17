package com.github.bduisenov;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class BasicValidations {

    public static Pair<Predicate<String>, String> notEmpty() {
        return new Pair<>(val -> val != null && val.length() > 0, "may not be empty");
    }

    public static <T> Pair<Predicate<T>, String> notNull() {
        return new Pair<>(Objects::nonNull, "may not be null");
    }

    public static <T> List<String> notNull(T val) {
        return notNull(val, "may not be null");
    }

    public static <T> List<String> notNull(T val, String message) {
        if (val != null) {
            return emptyList();
        }
        return singletonList(message);
    }

    public static <T> List<String> isNull(T val) {
        return isNull(val, "must be null");
    }

    public static <T> List<String> isNull(T val, String message) {
        if (val == null) {
            return emptyList();
        }
        return singletonList(message);
    }

    public static List<String> min(BigDecimal val, long min) {
        return min(val.longValue(), min);
    }

    public static List<String> min(BigDecimal val, long min, String message) {
        return min(val.longValue(), min, message);
    }

    public static List<String> min(BigInteger val, long min) {
        return min(val.longValue(), min);
    }

    public static List<String> min(BigInteger val, long min, String message) {
        return min(val.longValue(), min, message);
    }

    public static List<String> min(long val, long min) {
        return min(val, min, MessageFormat.format("must be larger than or equal to {0}", min));
    }

    public static List<String> min(long val, long min, String message) {
        if (val >= min) {
            return emptyList();
        }
        return singletonList(message);
    }

    public static List<String> max(BigDecimal val, long max) {
        return max(val.longValue(), max);
    }

    public static List<String> max(BigDecimal val, long max, String message) {
        return max(val.longValue(), max, message);
    }

    public static List<String> max(BigInteger val, long max) {
        return max(val.longValue(), max);
    }

    public static List<String> max(BigInteger val, long max, String message) {
        return max(val.longValue(), max, message);
    }

    public static List<String> max(long val, long max) {
        return max(val, max, MessageFormat.format("must be less than or equal to {0}", max));
    }

    public static List<String> max(long val, long max, String message) {
        if (val <= max) {
            return emptyList();
        }
        return singletonList(message);
    }

    public static List<String> minLength(String val, int min) {
        return minLength(val.toCharArray(), min);
    }

    public static List<String> minLength(char[] val, int min) {
        return min(val.length, min, MessageFormat.format("length must be larger then or equal to {0}", min));
    }

    public static List<String> maxLength(String val, int max) {
        return maxLength(val.toCharArray(), max);
    }

    public static List<String> maxLength(char[] val, int max) {
        return max(val.length, max, MessageFormat.format("length must be less or equal to {0}", max));
    }

    public static List<String> size(String val, int min, int max) {
        return size(val.length(), min, max);
    }

    public static List<String> size(String val, int min, int max, String message) {
        return size(val.length(), min, max, message);
    }

    public static List<String> size(Collection val, int min, int max) {
        return size(val.size(), min, max);
    }

    public static List<String> size(Collection val, int min, int max, String message) {
        return size(val.size(), min, max, message);
    }

    public static List<String> size(Map val, int min, int max) {
        return size(val.size(), min, max);
    }

    public static List<String> size(Map val, int min, int max, String message) {
        return size(val.size(), min, max, message);
    }

    public static List<String> size(int val, int min, int max) {
        return size(val, min, max, MessageFormat.format("size must be between {0} and {1}", min, max));
    }

    public static List<String> size(int val, int min, int max, String message) {
        if (val >= min && val <= max) {
            return emptyList();
        }
        return singletonList(message);
    }

}
