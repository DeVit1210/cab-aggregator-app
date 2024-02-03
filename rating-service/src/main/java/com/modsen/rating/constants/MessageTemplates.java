package com.modsen.rating.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ResourceBundle;

@AllArgsConstructor
public enum MessageTemplates {
    RATING_NOT_FOUND_FOR_PASSENGER("rating.not-found.passenger"),
    RATING_NOT_FOUND_FOR_DRIVER("rating.not-found.driver"),
    INCORRECT_PAGE_SIZE("page.invalid.size"),
    INCORRECT_PAGE_NUMBER("page.invalid.number"),
    INCORRECT_PAGE_NUMBER_WITH_LIMIT("page.invalid.number.with-limit"),
    INCORRECT_SORT_FIELD_NAME("page.invalid.sortField");

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("exception-messages");
    private final String key;

    public String getValue() {
        return resourceBundle.getString(this.key);
    }
}
