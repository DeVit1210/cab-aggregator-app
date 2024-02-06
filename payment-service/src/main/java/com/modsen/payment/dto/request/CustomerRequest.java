package com.modsen.payment.dto.request;

import com.modsen.payment.constants.ValidationConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerRequest {
    @NotNull(message = ValidationConstants.ID_NOT_NULL)
    private Long passengerId;
    @Email
    private String email;
    @NotBlank(message = ValidationConstants.NAME_NOT_BLANK)
    private String name;
}
