package com.modsen.promocode.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumValueValidator implements ConstraintValidator<EnumValue, String> {
    private Set<String> enumNames;

    @Override
    public void initialize(EnumValue enumValue) {
        enumNames = Stream.of(enumValue.enumClass().getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return false;
        }

        return enumNames.contains(value);
    }
}