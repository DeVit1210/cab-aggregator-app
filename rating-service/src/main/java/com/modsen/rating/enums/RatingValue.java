package com.modsen.rating.enums;

public enum RatingValue {
    ZERO,
    ONE,
    TWO,
    THREE,
    FOUR,
    FIVE;

    public int getValue(RatingValue ratingValue) {
        return ratingValue.ordinal();
    }
}
