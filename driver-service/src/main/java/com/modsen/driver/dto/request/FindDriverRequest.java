package com.modsen.driver.dto.request;

import com.modsen.driver.constants.ValidationConstants;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindDriverRequest {
    @NotNull(message = ValidationConstants.ID_NOT_NULL)
    private Long rideId;
}
