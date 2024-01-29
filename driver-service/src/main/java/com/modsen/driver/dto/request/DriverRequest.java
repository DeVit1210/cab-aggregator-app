package com.modsen.driver.dto.request;

import com.modsen.driver.constants.RegexConstants;
import com.modsen.driver.constants.ValidationConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DriverRequest {
    @NotEmpty(message = ValidationConstants.FIRST_NAME_NOT_EMPTY)
    private String firstName;
    @NotEmpty(message = ValidationConstants.SECOND_NAME_NOT_EMPTY)
    private String secondName;
    @Email(message = ValidationConstants.EMAIL_INVALID)
    private String email;
    @NotEmpty(message = ValidationConstants.PHONE_NUMBER_NOT_EMPTY)
    @Pattern(regexp = RegexConstants.PHONE_NUMBER_REGEX, message = ValidationConstants.PHONE_NUMBER_INVALID)
    private String phoneNumber;
    @NotEmpty(message = ValidationConstants.DRIVER_LICENCE_NOT_EMPTY)
    private String licenceNumber;
}
