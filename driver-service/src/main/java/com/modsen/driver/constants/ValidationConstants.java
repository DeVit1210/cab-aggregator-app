package com.modsen.driver.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidationConstants {
    public static final String FIRST_NAME_NOT_BLANK = "first name cannot be empty or blank!";
    public static final String LAST_NAME_NOT_BLANK = "last name cannot be blank for a driver!";
    public static final String EMAIL_INVALID = "invalid email format!";
    public static final String PHONE_NUMBER_NOT_BLANK = "phone number cannot be blank!";
    public static final String DRIVER_LICENCE_NOT_BLANK = "driver's license cannot be blank!";
    public static final String PHONE_NUMBER_INVALID = "invalid phone number format!";
    public static final String ID_NOT_NULL = "id cannot be null!";
    public static final String ADDRESS_NOT_BLANK = "address cannot be blank!";
    public static final String NEGATIVE_AMOUNT = "amount has to be greater than 0.00!";
}
