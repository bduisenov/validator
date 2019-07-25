# Validator

Library for functional validation in Java.

#### Why?

The goal is to enable developers to write very complex validation logic in an easy way.
It is zero dependency, purely functional library. 

#### Usage

Add the following maven dependency:
```xml
<dependency>
    <groupId>com.github.bduisenov</groupId>
    <artifactId>validator</artifactId>
    <version>0.0.3</version>
</dependency>
```

##### Examples

###### Basic usage
 
```java
User validatedUser = Validator.of(user)
    .validate(User::getName, Objects::nonNull, "Name must not be null")
    .validate(User::getEmail, Objects::nonNull, "Email is mandatory")
    .getOrThrow();
```

The `#validate` method is overloaded to provide a variety of options to perform validation.
In case if you provide a projection (method reference) to a target value, validator will use reflection
to extract the name of the field and include this name together with provided message in `ValidationException`.
For example:
```java
ValidationException(violations=[ValidatorViolation(fieldName=name, errors=[may not be null], violations=null), ValidatorViolation(fieldName=email, errors=[may not be null], violations=null)])
```

###### Don't want to use reflection?

Instead of relying on reflection for field name extraction, you can provide the field name yourself
```java
User validatedUser = Validator.of(user)
    .validate("name", User::getName, Objects::nonNull, "Name must not be null")
    .validate("email", User::getEmail, Objects::nonNull, "Email is mandatory")
    .getOrThrow();
```

###### Customizing validator

By default `ValidationException` is thrown when object is not valid.
As some projects do not follow exception based flow, you can customize the behaviour of
validator to give you validation result in another way.

Let's assume that you are using `VAVR` in your project and you want to get `Either`
```java
import com.github.bduisenov.ValidationException;
import com.github.bduisenov.Validator;
import io.vavr.control.Either;
import lombok.NonNull;

import static io.vavr.API.Left;
import static io.vavr.API.Right;

public class CustomValidator<T, V extends CustomValidator<T, V>> extends Validator<T, V> {

    public CustomValidator(T value) {
        super(value);
    }

    public static <T> CustomValidator<T, ?> of(@NonNull T t) {
        return new CustomValidator<>(t);
    }

    public Either<ValidationException, T> getEither() {
        if (getViolations().isEmpty()) {
            return Right(getValue());
        }
        return Left(new ValidationException(getViolations()));
    }
}
```

###### Optional field validation

You can use `#validateOpt` method, that will apply validation logic for non null values.

```java
User validatedUser = Validator.of(user)
    .validate("name", User::getName, Objects::nonNull, "Name must not be null")
    .validateOpt("email", User::getEmail, not(String::isBlank), "Email must be non empty value")
    .getOrThrow();
```

###### Conditional validation

```java
User validatedUser = Validator.of(user)
    .validate(User::getName, Objects::nonNull, "Name must not be null")
    .validateWhen(u -> u.gender == Gender.MALE, validator -> validator
        .validate(User::getAge, age -> age > 18, "You must be older 18 years"))
    .validateOpt(User::getEmail, not(String::isBlank), "Email is mandatory")
    .getOrThrow();
```

###### Composing multiple validation rules

 ```java
public static Consumer<ConstraintFactory<String>> nameValidation() {
        return constraints -> {
            constraints.addPredicate(n -> n.length() > 2, "Name length is not valid");
            constraints.addPredicate(noControlChars(), "Name must not contain control characters");
        };
    }
```

```java
User validatedUser = Validator.of(user)
    .validate(User::getName, nameValidation())
    .validateWhen(u -> u.gender == Gender.MALE, validator -> validator
        .validate(User::getAge, age -> age > 18, "You must be older 18 years"))
    .validateOpt(User::getEmail, not(String::isBlank), "Email is mandatory")
    .getOrThrow();
```
