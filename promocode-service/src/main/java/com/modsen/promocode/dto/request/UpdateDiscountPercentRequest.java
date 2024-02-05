package com.modsen.promocode.dto.request;

import com.modsen.promocode.constants.ValidationConstants;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateDiscountPercentRequest {
    @NotNull(message = ValidationConstants.ID_NOT_NULL)
    private Long promocodeId;
    @Min(value = 1, message = ValidationConstants.DISCOUNT_PERCENT_INVALID)
    @Max(value = 99, message = ValidationConstants.DISCOUNT_PERCENT_INVALID)
    private int discountPercent;
}
