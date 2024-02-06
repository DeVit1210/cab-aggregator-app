package com.modsen.rating.dto.request;

import com.modsen.rating.constants.ValidationConstants;
import com.modsen.rating.enums.RatingValue;
import com.modsen.rating.enums.Role;
import com.modsen.rating.validation.EnumValue;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RatingRequest {
    @NotNull(message = ValidationConstants.ID_NOT_EMPTY)
    private Long ratedPersonId;
    @EnumValue(enumClass = Role.class)
    private String role;
    @EnumValue(enumClass = RatingValue.class)
    private String ratingValue;
    private String comment;
    @NotNull(message = ValidationConstants.ID_NOT_EMPTY)
    private Long rideId;
}
