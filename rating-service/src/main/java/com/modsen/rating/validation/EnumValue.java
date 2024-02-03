package com.modsen.rating.validation;

import com.modsen.rating.constants.ValidationConstants;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EnumValueValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface EnumValue {
    Class<? extends Enum<?>> enumClass();
    String message() default ValidationConstants.ENUM_INVALID;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

