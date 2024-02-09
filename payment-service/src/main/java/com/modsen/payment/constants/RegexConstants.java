package com.modsen.payment.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RegexConstants {
    public static final String CARD_NUMBER_PATTERN = "\\d{4}\\s?\\d{4}\\s?\\d{4}\\s?\\d{4}";
    public static final String CVC_PATTERN = "\\d{3}";
}
