package com.modsen.promocode.dto.request;

import com.modsen.promocode.constants.ValidationConstants;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PromocodeRequest {
    @NotEmpty(message = ValidationConstants.PROMOCODE_NAME_EMPTY)
    private String name;
    @NotNull(message = ValidationConstants.DAYS_QUANTITY_INVALID)
    @Positive
    private int daysQuantity;
    @Min(value = 1, message = ValidationConstants.DISCOUNT_PERCENT_INVALID)
    @Max(value = 99, message = ValidationConstants.DISCOUNT_PERCENT_INVALID)
    private int discountPercent;
    @Positive
    private int maxUsageCount;
    @PositiveOrZero
    private int minRidesQuantity;
}
