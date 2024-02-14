package com.modsen.promocode.constants;

import lombok.AllArgsConstructor;

import java.util.ResourceBundle;

@AllArgsConstructor
public enum MessageTemplates {
    PROMOCODE_NOT_FOUND_BY_ID("promocode.not-found.id"),
    PROMOCODE_NOT_FOUND_BY_NAME("promocode.not-found.name"),
    PROMOCODE_ALREADY_EXISTS("promocode.already.exists"),
    PROMOCODE_ALREADY_APPLIED("promocode.already.applied"),
    EXPIRED_PROMOCODE("promocode.expired"),
    INVALID_RIDE_AMOUNT_FOR_PROMOCODE("promocode.ride.amount.invalid"),
    NO_PROMOCODE_FOR_PASSENGER("promocode.not-found.passenger");

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("exception-messages");
    private final String key;

    public String getValue() {
        return resourceBundle.getString(this.key);
    }
}