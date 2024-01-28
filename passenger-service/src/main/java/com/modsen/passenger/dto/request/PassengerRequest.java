package com.modsen.passenger.dto.request;

import com.modsen.passenger.constants.RegexTemplates;
import com.modsen.passenger.constants.ValidationMessages;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PassengerRequest {
    @NotEmpty(message = ValidationMessages.FIRST_NAME_NOT_EMPTY)
    private String firstName;
    private String lastName;
    @Email(message = ValidationMessages.EMAIL_INVALID)
    private String email;
    @NotEmpty(message = ValidationMessages.PHONE_NUMBER_NOT_EMPTY)
    @Pattern(regexp = RegexTemplates.PHONE_NUMBER_REGEX, message = ValidationMessages.PHONE_NUMBER_INVALID)
    private String phoneNumber;
}
