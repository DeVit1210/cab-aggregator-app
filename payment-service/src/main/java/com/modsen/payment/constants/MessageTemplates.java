package com.modsen.payment.constants;

import lombok.AllArgsConstructor;

import java.util.ResourceBundle;

@AllArgsConstructor
public enum MessageTemplates {
    INCORRECT_PAGE_SIZE("page.invalid.size"),
    INCORRECT_PAGE_NUMBER("page.invalid.number"),
    INCORRECT_PAGE_NUMBER_WITH_LIMIT("page.invalid.number.with-limit"),
    INCORRECT_SORT_FIELD_NAME("page.invalid.sortField"),
    STRIPE_ERROR("stripe.message"),
    STRIPE_CUSTOMER_ALREADY_EXISTS("stripe.customer.already-exists"),
    ENTITY_NOT_FOUND_BY_ID("entity.not-found.id"),
    ENTITY_NOT_FOUND_BY_STRIPE_ID("entity.not-found.stripe.id"),
    INCUFFICIENT_ACCOUNT_BALANCE("account.balance.insufficient"),
    DRIVER_ACCOUNT_NOT_FOUND_FOR_DRIVER_ID("account.not-found.driver-id"),
    CREDIT_CARD_ALREADY_EXISTS("card.already-exists"),
    DEFAULT_CREDIT_CARD("card.default"),
    CREDIT_CARD_INVALID_HOLDER("card.holder.invalid"),
    RIDE_ALREADY_PAID("ride.already-paid"),
    DRIVER_ACCOUNT_ALREADY_EXISTS("driver.already-exists");

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("exception-messages");
    private final String key;

    public String getValue() {
        return resourceBundle.getString(this.key);
    }
}