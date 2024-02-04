package com.modsen.driver.dto.request;

import com.modsen.driver.constants.RegexConstants;
import com.modsen.driver.constants.ValidationConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DriverRequest {
    @NotBlank(message = ValidationConstants.FIRST_NAME_NOT_BLANK)
    private String firstName;
    @NotBlank(message = ValidationConstants.LAST_NAME_NOT_BLANK)
    private String lastName;
    @Email(message = ValidationConstants.EMAIL_INVALID)
    private String email;
    @NotBlank(message = ValidationConstants.PHONE_NUMBER_NOT_BLANK)
    @Pattern(regexp = RegexConstants.PHONE_NUMBER_REGEX, message = ValidationConstants.PHONE_NUMBER_INVALID)
    private String phoneNumber;
    @NotBlank(message = ValidationConstants.DRIVER_LICENCE_NOT_BLANK)
    private String licenceNumber;
}
