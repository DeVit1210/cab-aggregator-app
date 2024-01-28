package com.modsen.passenger.constants;

public interface MessageTemplates {
    String PASSENGER_NOT_FOUND_BY_ID = "Passenger with id %s was not found!";
    String INCORRECT_PAGE_SIZE = "Page size should be at least 1, but requested %s!";
    String INCORRECT_PAGE_NUMBER = "Page number should be at least 1, but requested %s!";
    String INCORRECT_PAGE_NUMBER_WITH_LIMIT = "For the size page %s there are only %s pages, but requested %s";
    String INCORRECT_SORT_FIELD_NAME = "Field with name %s was not found in Passenger entity!";
}
