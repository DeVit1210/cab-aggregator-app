package com.modsen.driver.constants;

import lombok.AllArgsConstructor;

import java.util.ResourceBundle;

@AllArgsConstructor
public enum MessageTemplates {
    DRIVER_NOT_FOUND_BY_ID("driver.not-found.id"),
    DRIVER_NOT_FOUND_BY_STATUS("driver.not-found.status"),
    DRIVER_ALREADY_ONLINE("driver.already-online"),
    DRIVER_NOT_AVAILABLE("driver.not-available"),
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

