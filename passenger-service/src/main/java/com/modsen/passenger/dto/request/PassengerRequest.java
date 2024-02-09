package com.modsen.passenger.dto.request;

import com.modsen.passenger.constants.RegexTemplates;
import com.modsen.passenger.constants.ValidationMessages;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PassengerRequest {
    @NotBlank(message = ValidationMessages.NAME_NOT_BLANK)
    private String firstName;
    @NotBlank(message = ValidationMessages.NAME_NOT_BLANK)
    private String lastName;
    @Email(message = ValidationMessages.EMAIL_INVALID)
    private String email;
    @NotBlank(message = ValidationMessages.PHONE_NUMBER_NOT_BLANK)
    @Pattern(regexp = RegexTemplates.PHONE_NUMBER_REGEX, message = ValidationMessages.PHONE_NUMBER_INVALID)
    private String phoneNumber;
}
