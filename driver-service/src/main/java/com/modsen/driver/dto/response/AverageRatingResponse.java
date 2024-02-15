package com.modsen.driver.dto.response;

import com.modsen.driver.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AverageRatingResponse {
    private Long ratedPersonId;
    private Role role;
    private double averageRating;
    private int ratesTotalQuantity;

    public static AverageRatingResponse empty(Long ratedPersonId) {
        return AverageRatingResponse.builder()
                .averageRating(5.00)
                .ratedPersonId(ratedPersonId)
                .role(Role.DRIVER)
                .ratesTotalQuantity(0)
                .build();
    }
}
