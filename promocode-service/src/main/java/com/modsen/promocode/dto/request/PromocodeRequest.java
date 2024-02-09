package com.modsen.promocode.dto.request;

import com.modsen.promocode.constants.ValidationConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
@Builder
public class PromocodeRequest {
    @NotBlank(message = ValidationConstants.PROMOCODE_NAME_BLANK)
    private String name;
    @NotNull(message = ValidationConstants.DAYS_QUANTITY_INVALID)
    @Positive
    private int daysQuantity;
    @Range(min = 1, max = 99, message = ValidationConstants.DISCOUNT_PERCENT_INVALID)
    private int discountPercent;
    @Positive
    private int maxUsageCount;
    @PositiveOrZero
    private int minRidesQuantity;
}
