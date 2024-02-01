package com.modsen.payment.dto.request;

import com.modsen.payment.constants.ValidationConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerRequest {
    @NotEmpty(message = ValidationConstants.ID_NOT_EMPTY)
    private Long passengerId;
    @Email
    private String email;
    private String name;
}
