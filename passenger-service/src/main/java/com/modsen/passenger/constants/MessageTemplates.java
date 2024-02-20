package com.modsen.passenger.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ResourceBundle;

@AllArgsConstructor
@Getter
public enum MessageTemplates {
    PASSENGER_NOT_FOUND_BY_ID("passenger.not-found.id"),
    PASSENGER_NOT_FOUND_BY_EMAIL("passenger.not-found.email"),
    INCORRECT_PAGE_SIZE("page.invalid.size"),
    INCORRECT_PAGE_NUMBER("page.invalid.number"),
    INCORRECT_PAGE_NUMBER_WITH_LIMIT("page.invalid.number.with-limit"),
    INCORRECT_SORT_FIELD_NAME("page.invalid.sortField"),
    EMAIL_NOT_UNIQUE("passenger.not-unique.email"),
    PHONE_NUMBER_NOT_UNIQUE("passenger.not-unique.number");

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("exception-messages");
    private final String key;

    public String getValue() {
        return resourceBundle.getString(this.key);
    }
}