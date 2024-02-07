package com.modsen.promocode.dto.request;

import com.modsen.promocode.constants.ValidationConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApplyPromocodeRequest {
    @NotNull(message = ValidationConstants.ID_NOT_NULL)
    private Long passengerId;
    @NotBlank(message = ValidationConstants.PROMOCODE_NAME_BLANK)
    private String promocodeName;
}
