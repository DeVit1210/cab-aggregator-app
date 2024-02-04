package com.modsen.promocode.dto.request;

import com.modsen.promocode.constants.ValidationConstants;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApplyPromocodeRequest {
    @NotNull(message = ValidationConstants.ID_NULL)
    private Long passengerId;
    @NotEmpty(message = ValidationConstants.PROMOCODE_NAME_EMPTY)
    private String promocodeName;
}
