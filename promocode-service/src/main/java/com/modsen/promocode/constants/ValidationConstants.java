package com.modsen.promocode.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidationConstants {
    public static final String PROMOCODE_NAME_BLANK = "promocode name cannot be empty!";
    public static final String DAYS_QUANTITY_INVALID = "invalid promocode days quantity!";
    public static final String DISCOUNT_PERCENT_INVALID = "discount percent for promocode should be between 1 and 99!";
    public static final String ID_NOT_NULL = "id cannot be null!";
}
