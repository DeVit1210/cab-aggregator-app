package com.modsen.driver.dto.response;

import com.modsen.driver.enums.Role;
import lombok.Builder;

@Builder
public record AverageRatingResponse(
        Long ratedPersonId,
        Role role,
        double averageRating,
        int ratesTotalQuantity
) {
    public static AverageRatingResponse empty(Long ratedPersonId) {
        return new AverageRatingResponse(ratedPersonId, Role.DRIVER, 5.0, 0);
    }
}
