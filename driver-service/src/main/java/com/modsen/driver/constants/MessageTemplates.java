package com.modsen.driver.constants;

public interface MessageTemplates {
    String DRIVER_NOT_FOUND_BY_ID = "Driver with id %s was not found!";
    String DRIVER_NOT_FOUND_BY_EMAIL = "Driver with email %s was not found!";
    String INCORRECT_PAGE_SIZE = "Page size should be at least 1, but requested %s!";
    String INCORRECT_PAGE_NUMBER = "Page number should be at least 1, but requested %s!";
    String INCORRECT_PAGE_NUMBER_WITH_LIMIT = "For the size page %s there are only %s pages, but requested %s";
    String INCORRECT_SORT_FIELD_NAME = "Field with name %s was not found in Driver entity!";
}

